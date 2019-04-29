package jp.vmi.selenium.selenese.inject;

import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import jp.vmi.junit.result.JUnitResultHolder;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.CommandSequence;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.log.CookieFilter;
import jp.vmi.selenium.selenese.log.PageInformation;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LogRecorder;

/**
 * Interceptor for logging each command execution.
 */
public class CommandLogInterceptor extends AbstractDoCommandInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CommandLogInterceptor.class);

    private void logResult(LogRecorder clr, String indent, String cmdStr, Result result, Context context) {
        PageInformation prevInfo = context.getLatestPageInformation();
        PageInformation info = PageInformation.newInstance(context);
        CookieFilter cookieFilter = context.getCookieFilter();
        String prefix = indent + "- Cookie: ";
        if (result.isFailed()) {
            String resStr = info.getFirstMessage(prevInfo, indent, cmdStr, "=>", result.toString());
            log.error(resStr);
            if (clr != null)
                clr.error(resStr);
            for (String message : info.cookieMap.allMessages(cookieFilter)) {
                log.error(prefix + message);
                if (clr != null)
                    clr.error(prefix + message);
            }
        } else {
            String resStr = info.getFirstMessage(prevInfo, indent, "-", result.toString());
            log.info(resStr);
            if (clr != null)
                clr.info(resStr);
            List<String> messages;
            if (info.isSameOrigin(prevInfo))
                messages = info.cookieMap.diffMessages(cookieFilter, prevInfo.cookieMap);
            else
                messages = info.cookieMap.allMessages(cookieFilter);
            for (String message : messages) {
                log.info(prefix + message);
                if (clr != null)
                    clr.info(prefix + message);
            }
        }
        if (info.origin != null)
            context.setLatestPageInformation(info);
    }

    @Override
    protected Result invoke(MethodInvocation invocation, Context context, ICommand command, String[] curArgs) throws Throwable {
        CommandSequence commandSequence = context.getCommandListIterator().getCommandSequence();
        LogRecorder clr = context.getCurrentTestCase().getLogRecorder();
        String indent = Strings.repeat("  ", commandSequence.getLevel() - 1);
        String cmdStr = command.toString();
        int retries = context.getRetries();
        int maxRetries = context.getMaxRetries();
        StringBuilder firstMsgBuf = new StringBuilder(indent).append('<').append(commandSequence).append("> ");
        if (retries >= 2)
            firstMsgBuf.append("(Retries:").append(retries).append('/').append(maxRetries).append(") ");
        String firstMsg = firstMsgBuf.append(cmdStr).toString();
        log.info(firstMsg);
        if (clr != null)
            clr.info(firstMsg);
        try {
            Result result = (Result) invocation.proceed();
            logResult(clr, indent, cmdStr, result, context);
            return result;
        } catch (Exception e) {
            String msg = cmdStr + " => " + e.getMessage();
            log.error(indent + msg);
            if (clr != null)
                clr.error(indent + msg);
            if (context instanceof JUnitResultHolder)
                ((JUnitResultHolder) context).getJUnitResult().setError(context.getCurrentTestCase(), e.getMessage(), e.toString());
            throw e;
        }
    }
}
