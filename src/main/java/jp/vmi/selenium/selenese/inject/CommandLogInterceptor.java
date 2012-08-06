package jp.vmi.selenium.selenese.inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.openqa.selenium.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.Context;

import jp.vmi.selenium.selenese.command.Command.Result;

public class CommandLogInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CommandLogInterceptor.class);

    private static final Comparator<Cookie> cookieComparator = new Comparator<Cookie>() {
        @Override
        public int compare(Cookie c1, Cookie c2) {
            return c1.getName().compareTo(c2.getName());
        }
    };

    private static final FastDateFormat expiryFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private List<String> prevMessages = new ArrayList<String>();

    private void cookieToMessage(List<String> messages, Set<Cookie> cookies) {
        List<Cookie> cookieList = new ArrayList<Cookie>(cookies);
        Collections.sort(cookieList, cookieComparator);
        for (Cookie cookie : cookieList) {
            Date expiry = cookie.getExpiry();
            String expiryString = expiry != null ? expiryFormat.format(expiry) : "*";
            messages.add(String.format("- Cookie: %s=[%s] (domain=%s, path=%s, expire=%s)", cookie.getName(), cookie.getValue(),
                cookie.getDomain(), cookie.getPath(), expiryString));
        }
    }

    private void log(Result result, Context ctx) {
        List<String> messages = new ArrayList<String>();
        messages.add(String.format("URL: [%s] / Title: [%s]", ctx.getDriver().getCurrentUrl(), ctx.getDriver().getTitle()));
        cookieToMessage(messages, ctx.getDriver().manage().getCookies());
        if (ListUtils.isEqualList(messages, prevMessages)) {
            if (result.isFailed()) {
                log.error("- {}", result);
            } else {
                log.info("- {}", result);
            }
        } else {
            Iterator<String> iter = messages.iterator();
            if (result.isFailed()) {
                log.error("- {} {}", result, iter.next());
                while (iter.hasNext())
                    log.error(iter.next());
            } else {
                log.info("- {} {}", result, iter.next());
                while (iter.hasNext())
                    log.info(iter.next());
            }
            prevMessages = messages;
        }
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // arguments
        Object[] args = invocation.getArguments();
        Context ctx;
        try {
            ctx = (Context) args[0];
        } catch (Exception e) {
            log.error("doCommand method arguments error", e);
            throw new RuntimeException(e);
        }
        Result r = (Result) invocation.proceed();
        log(r, ctx);
        return r;
    }
}
