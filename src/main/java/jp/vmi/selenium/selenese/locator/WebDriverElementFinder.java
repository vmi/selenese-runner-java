package jp.vmi.selenium.selenese.locator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;

import com.thoughtworks.selenium.SeleniumException;
import com.thoughtworks.selenium.webdriven.ElementFinder;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * WebDriver Element Locator.
 *
 * Fully override the implementation of original ElementFinder.
 *
 * Note: This does not support ui locator.
 *
 * <ul>
 * <li>@see <a href="https://code.google.com/p/selenium/source/browse/ide/main/src/content/selenium-core/reference.html">SeleniumIDE Reference</a>
 * <li>@see <a href="https://code.google.com/p/selenium/source/browse/javascript/selenium-core/scripts/ui-doc.html">UI document</a>
 * </ul>
 */
public class WebDriverElementFinder extends ElementFinder {

    /** Separator between locator and option locator. */
    public static final char OPTION_LOCATOR_SEPARATOR = '\0';

    /**
     * Convert to option locator with parent.
     *
     * @param parentLocator parent locator.
     * @param optionLocator child option locator.
     * @return option locator with parent.
     */
    public static String convertToOptionLocatorWithParent(String parentLocator, String optionLocator) {
        return parentLocator + OPTION_LOCATOR_SEPARATOR + optionLocator;
    }

    private static final Pattern LOCATORS_RE = Pattern.compile("(\\w+)=(.*)|(document\\..*)|(//.*)");
    private static final int LOCATOR_TYPE = 1;
    private static final int LOCATOR_ARG = 2;
    private static final int DOM_LOCATOR = 3;
    private static final int XPATH_LOCATOR = 4;

    private final Map<String, LocatorHandler> handlerMap = new HashMap<String, LocatorHandler>();
    private final Map<String, OptionLocatorHandler> optionHandlerMap = new HashMap<String, OptionLocatorHandler>();

    /**
     * Constructor.
     */
    public WebDriverElementFinder() {
        registerHandler(new IdentifierHandler());
        registerHandler(new IdHandler());
        registerHandler(new NameHandler());
        registerHandler(new DomHandler());
        registerHandler(new XPathHandler());
        registerHandler(new LinkHandler());
        registerHandler(new CSSHandler());
        registerOptionHandler(new OptionLabelHandler());
        registerOptionHandler(new OptionIdHandler());
        registerOptionHandler(new OptionIndexHandler());
        registerOptionHandler(new OptionValueHandler());
    }

    /**
     * Register locator handler.
     *
     * @param handler locator handler.
     * @return this.
     */
    public WebDriverElementFinder registerHandler(LocatorHandler handler) {
        handlerMap.put(handler.locatorType(), handler);
        return this;
    }

    /**
     * Register option locator handler.
     *
     * @param handler option locator handler.
     * @return this.
     */
    public WebDriverElementFinder registerOptionHandler(OptionLocatorHandler handler) {
        optionHandlerMap.put(handler.optionLocatorType(), handler);
        return this;
    }

    @Override
    public void add(String strategyName, String implementation) {
        registerHandler(new AdditionalHandler(strategyName, implementation));
    }

    /**
     * call findElement of superclass.
     *
     * @param driver driver.
     * @param locator locator.
     *
     * @return element.
     */
    public WebElement superFindElement(WebDriver driver, String locator) {
        return super.findElement(driver, locator);
    }

    private boolean hasFrames(WebDriver driver) {
        if (driver instanceof JavascriptExecutor)
            return (Boolean) ((JavascriptExecutor) driver).executeScript("return window.frames.length > 0");
        else
            return false;
    }

    private List<WebElement> findElementsByLocator(LocatorHandler handler, WebDriver driver, String arg, String locator) {
        TargetLocator switchTo = driver.switchTo();
        switchTo.defaultContent();
        List<WebElement> result = handler.handle(driver, arg);
        if (!result.isEmpty())
            return result;
        if (hasFrames(driver)) {
            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            for (WebElement iframe : iframes) {
                switchTo.frame(iframe);
                result = handler.handle(driver, arg);
                if (!result.isEmpty())
                    return result;
            }
            List<WebElement> frames = driver.findElements(By.tagName("frame"));
            for (WebElement frame : frames) {
                switchTo.frame(frame);
                result = handler.handle(driver, arg);
                if (!result.isEmpty())
                    return result;
            }
        }
        throw new SeleniumException("Element " + locator + " not found", new NoSuchElementException(locator));
    }

    private List<WebElement> filterElementsByOptionLocator(List<WebElement> elements, String option) {
        if (option == null)
            return elements;
        String[] pair = option.split("=", 2);
        String type;
        String arg;
        if (pair.length == 1) {
            type = "label";
            arg = option;
        } else {
            type = pair[0];
            arg = pair[1];
        }
        OptionLocatorHandler handler = optionHandlerMap.get(type);
        if (handler == null)
            throw new UnsupportedOperationException("Unknown option locator type: " + option);
        List<WebElement> result = new ArrayList<WebElement>();
        for (WebElement element : elements)
            result.addAll(handler.handle(element, arg));
        return result;
    }

    private String formatLocator(String locator, String option) {
        return (option == null) ? locator : locator + " (" + option + ")";
    }

    /**
     * Find elements of specified locator.
     *
     * @param driver WebDriver.
     * @param locator locator.
     * @return list of found elements. (empty if no element)
     */
    public List<WebElement> findElements(WebDriver driver, String locator) {
        String option;
        int optIndex = locator.indexOf(OPTION_LOCATOR_SEPARATOR);
        if (optIndex >= 0) {
            option = locator.substring(optIndex + 1);
            locator = locator.substring(0, optIndex);
        } else {
            option = null;
        }
        Matcher matcher = LOCATORS_RE.matcher(locator);
        String type;
        String arg;
        if (matcher.matches()) {
            type = matcher.group(LOCATOR_TYPE);
            arg = matcher.group(LOCATOR_ARG);
            if (isNotEmpty(type)) {
                type = type.toLowerCase();
            } else if (isNotEmpty(matcher.group(DOM_LOCATOR))) {
                // start with "document."
                type = "dom";
                arg = locator;
            } else if (isNotEmpty(matcher.group(XPATH_LOCATOR))) {
                // start with "//"
                type = "xpath";
                arg = locator;
            } else {
                // not reached?
                throw new UnsupportedOperationException("Unknown locator type: " + formatLocator(locator, option));
            }
        } else {
            type = "identifier";
            arg = locator;
        }
        LocatorHandler handler = handlerMap.get(type);
        if (handler == null)
            throw new UnsupportedOperationException("Unknown locator type: " + formatLocator(locator, option));
        return filterElementsByOptionLocator(findElementsByLocator(handler, driver, arg, locator), option);
    }

    @Override
    public WebElement findElement(WebDriver driver, String locator) {
        return findElements(driver, locator).get(0);
    }
}
