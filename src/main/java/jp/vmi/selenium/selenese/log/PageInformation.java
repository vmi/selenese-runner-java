package jp.vmi.selenium.selenese.log;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NotFoundException;
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
            return StringUtils.join(msgs, " / at ");
        }
    }

    public PageInformation(Context context) {
        String message;
        String origin;
        WebDriver driver = context.getWrappedDriver();
        try {
            String handle = driver.getWindowHandle();
            if (StringUtils.isEmpty(handle))
                throw new NotFoundException();
            driver.switchTo().window(handle);
            String url = driver.getCurrentUrl();
            String title = driver.getTitle();
            message = formatUrlAndTitle(url, title);
            origin = getOrigin(url);
            for (Cookie cookie : driver.manage().getCookies())
                cookieMap.add(cookie);
        } catch (NotFoundException e) {
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

    private String formatUrlAndTitle(String url, String title) {
        return "URL: [" + url + "] / Title: [" + title + "]";
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
