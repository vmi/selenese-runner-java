package jp.vmi.selenium.selenese.inject;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class CommandLogInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CommandLogInterceptor.class);

    private void logResult(LogRecorder clr, String indent, String cmdStr, Result result, Context context) {
        PageInformation prevInfo = context.getLatestPageInformation();
        PageInformation info = new PageInformation(context);
        CookieFilter cookieFilter = context.getCookieFilter();
        String prefix = indent + "- Cookie: ";
        if (result.isFailed()) {
            String resStr = info.getFirstMessage(prevInfo, indent, cmdStr, "=>", result.toString());
            log.error(resStr);
            clr.error(resStr);
            for (String message : info.cookieMap.allMessages(cookieFilter)) {
                log.error(prefix + message);
                clr.error(prefix + message);
            }
        } else {
            String resStr = info.getFirstMessage(prevInfo, indent, "-", result.toString());
            log.info(resStr);
            clr.info(resStr);
            List<String> messages;
            if (info.isSameOrigin(prevInfo))
                messages = info.cookieMap.diffMessages(cookieFilter, prevInfo.cookieMap);
            else
                messages = info.cookieMap.allMessages(cookieFilter);
            for (String message : messages) {
                log.info(prefix + message);
                clr.info(prefix + message);
            }
        }
        if (info.origin != null)
            context.setLatestPageInformation(info);
    }

    private static final int CONTEXT = 0;
    private static final int COMMAND = 1;

    /*
     * target signature:
     * Result LogIndentLevelHolder#doCommand(Context context, ICommand command, String... curArgs)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        Context context = (Context) args[CONTEXT];
        CommandSequence commandSequence = context.getCommandListIterator().getCommandSequence();
        ICommand command = (ICommand) args[COMMAND];
        LogRecorder clr = context.getCurrentTestCase().getLogRecorder();
        String indent = StringUtils.repeat("  ", commandSequence.getLevel() - 1);
        String cmdStr = command.toString();
        String firstMsg = indent + "<" + commandSequence + "> " + cmdStr;
        log.info(firstMsg);
        clr.info(firstMsg);
        try {
            Result result = (Result) invocation.proceed();
            logResult(clr, indent, cmdStr, result, context);
            return result;
        } catch (Exception e) {
            String msg = cmdStr + " => " + e.getMessage();
            log.error(indent + msg);
            clr.error(indent + msg);
            if (context instanceof JUnitResultHolder)
                ((JUnitResultHolder) context).getJUnitResult().setError(context.getCurrentTestCase(), e.getMessage(), e.toString());
            throw e;
        }
    }
}
