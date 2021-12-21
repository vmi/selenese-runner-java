package jp.vmi.selenium.selenese.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Mouse utilities.
 */
public final class MouseUtils {

    private static final Logger logger = LoggerFactory.getLogger(MouseUtils.class);

    private MouseUtils() {
        // no operation.
    }

    /**
     * Move mouse.
     *
     * @param context context.
     * @param element WebElement.
     * @param coord coordinate.
     * @return Actions.
     */
    public static Actions moveTo(Context context, WebElement element, Point coord) {
        //if (context.isBrowser(HtmlUnitDriverFactory.BROWSER_NAME)) {
        //
        //    return false;
        //}
        int x = coord.x;
        int y = coord.y;
        if (context.isW3cAction()) {
            // Fix up following problem.
            // "When using the W3C Action commands, offsets are from the center of element"
            Dimension size = element.getSize();
            if (size != null) {
                x -= size.width / 2;
                y -= size.height / 2;
                logger.info("Adjust offsets for W3C Action command.");
            } else {
                logger.warn("Cannot adjust offsets because failed to get the element size.");
            }
        }
        Actions actions = new Actions(context.getWrappedDriver());
        if (context.isBrowser(WebDriverManager.HTMLUNIT)) {
            logger.warn("HtmlUnit driver does not support mouse movement to arbitrary X,Y coordinates.");
            return actions.moveToElement(element);
        } else {
            return actions.moveToElement(element, x, y);
        }
    }

    private static final Pattern MAJOR_VERSION_RE = Pattern.compile("(\\d+).*");

    /**
     * Is Action command W3C compatible?
     *
     * @param driver WebDriver.
     * @return true if Action command is W3C compatible.
     */
    public static boolean isW3cAction(WebDriver driver) {
        if (!(driver instanceof RemoteWebDriver))
            return false;
        Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
        if (caps == null)
            return false;
        String browserName = caps.getBrowserName();
        if (browserName == null)
            return false;
        String version = caps.getBrowserVersion();
        if (version == null)
            return false;
        Matcher matcher = MAJOR_VERSION_RE.matcher(version);
        if (!matcher.matches())
            return false;
        int majorVersion = Integer.parseInt(matcher.group(1));
        switch (browserName) {
        case WebDriverManager.FIREFOX:
            return true;
        case WebDriverManager.CHROME:
            return majorVersion >= 75;
        default:
            return false;
        }
    }
}
