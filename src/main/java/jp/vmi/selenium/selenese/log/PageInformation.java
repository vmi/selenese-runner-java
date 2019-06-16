package jp.vmi.selenium.selenese.log;

import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.selenese.Context;

/**
 * Page information.
 */
@SuppressWarnings("javadoc")
public class PageInformation {

    public static final PageInformation EMPTY = new PageInformation();

    public final String message;
    public final String origin; // error if origin is null.
    public final CookieMap cookieMap = new CookieMap();

    public static PageInformation newInstance(Context context) {
        return context.getLogFilter().isEmpty() ? EMPTY : new PageInformation(context);
    }

    private PageInformation(Context context) {
        if (context.getCurrentTestCase().hasNativeAlertHandler()) {
            message = "No page information: This test-case has native alert handler.";
            origin = "";
            return;
        }
        String message;
        String origin;
        WebDriver driver = context.getWrappedDriver();
        EnumSet<LogFilter> logFilter = context.getLogFilter();
        try {
            String url = logFilter.contains(LogFilter.URL) ? driver.getCurrentUrl() : null;
            String title = logFilter.contains(LogFilter.TITLE) ? driver.getTitle() : null;
            message = formatUrlAndTitle(url, title);
            origin = (url == null) ? "" : getOrigin(url);
            if (logFilter.contains(LogFilter.COOKIE))
                for (Cookie cookie : driver.manage().getCookies())
                    cookieMap.add(cookie);
        } catch (NotFoundException | StaleElementReferenceException e) {
            message = "No focused window/frame.";
            origin = "";
        } catch (UnhandledAlertException e) {
            message = String.format("No page information: [%s]", getMessage(e));
            origin = "";
        } catch (Exception e) {
            message = String.format("Failed to get page information: [%s]", getMessage(e));
            origin = "";
        }
        this.message = message;
        this.origin = origin;
    }

    private PageInformation() {
        this.message = "";
        this.origin = "";
    }

    private String getMessage(Exception e) {
        String msg = e.getMessage();
        if (msg != null) {
            return msg.replaceFirst("(?s)\r?\nBuild info:.*", "");
        } else {
            List<String> msgs = new ArrayList<>();
            msgs.add(e.toString());
            String pkgName = PageInformation.class.getPackage().getName() + ".";
            for (StackTraceElement ste : e.getStackTrace()) {
                if (ste.getClassName().startsWith(pkgName))
                    break;
                msgs.add(ste.toString().trim());
            }
            return String.join(" / at ", msgs);
        }
    }

    private String formatUrlAndTitle(String url, String title) {
        StringBuilder s = new StringBuilder();
        if (url != null)
            s.append("URL: [" + url + "]");
        if (title != null) {
            if (s.length() > 0)
                s.append(" / ");
            s.append("Title: [" + title + "]");
        }
        return s.toString();
    }

    private String getOrigin(String url) {
        URI uri = URI.create(url);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        return (port < 0)
            ? (scheme + "//" + host)
            : (scheme + "//" + host + ":" + port);
    }

    public String getFirstMessage(PageInformation prevInfo, String indent, String... prefixes) {
        StringBuilder m = new StringBuilder(indent);
        for (String prefix : prefixes)
            m.append(prefix).append(' ');
        if (origin == null || !origin.equals(prevInfo.origin) || !message.equals(prevInfo.message))
            m.append(message);
        else
            m.deleteCharAt(m.length() - 1);
        return m.toString();
    }

    public boolean isSameOrigin(PageInformation other) {
        return origin != null && origin.equals(other.origin);
    }
}
