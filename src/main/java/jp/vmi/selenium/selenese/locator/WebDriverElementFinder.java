package jp.vmi.selenium.selenese.locator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.seleniumemulation.ElementFinder;

import com.thoughtworks.selenium.SeleniumException;

/**
 * WebDriver Element Locator.
 * 
 * Fully override the implementation of original ElementFinder.
 * 
 * Note: This does not support ui locator.
 * 
 * <li>@see <a href="https://code.google.com/p/selenium/source/browse/ide/main/src/content/selenium-core/reference.html">SeleniumIDE Reference</a>
 * <li>@see <a href="https://code.google.com/p/selenium/source/browse/javascript/selenium-core/scripts/ui-doc.html">UI document</a>
 */
public class WebDriverElementFinder extends ElementFinder {

    private static final Pattern LOCATORS_RE = Pattern.compile("(\\w+)=(.*)|(document\\..*)|(//.*)");

    private final Map<String, LocatorHandler> handlerMap = new HashMap<String, LocatorHandler>();

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

        // unsupported locator.
        registerHandler(new LabelHandler());
        registerHandler(new IndexHandler());
        registerHandler(new ValueHandler());
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

    private List<WebElement> findElementsImpl(LocatorHandler handler, WebDriver driver, String arg, String locator) {
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
        }
        throw new SeleniumException("Element " + locator + " not found");
    }

    /**
     * Find elements of specified locator.
     *
     * @param driver WebDriver.
     * @param locator locator.
     * @return list of found elements. (empty if no element)
     */
    public List<WebElement> findElements(WebDriver driver, String locator) {
        Matcher matcher = LOCATORS_RE.matcher(locator);
        String type;
        String arg;
        if (matcher.matches()) {
            type = matcher.group(1);
            arg = matcher.group(2);
            if (StringUtils.isNotEmpty(type)) {
                type = type.toLowerCase();
            } else if (StringUtils.isNotEmpty(matcher.group(3))) {
                // start with "document."
                type = "dom";
                arg = locator;
            } else if (StringUtils.isNotEmpty(matcher.group(4))) {
                // start with "//"
                type = "xpath";
                arg = locator;
            } else {
                // not reached?
                throw new UnsupportedOperationException("Unknown locator type: " + locator);
            }
        } else {
            type = "identifier";
            arg = locator;
        }
        LocatorHandler handler = handlerMap.get(type);
        if (handler == null)
            throw new UnsupportedOperationException("Unknown locator type: " + locator);
        return findElementsImpl(handler, driver, arg, locator);
    }

    @Override
    public WebElement findElement(WebDriver driver, String locator) {
        return findElements(driver, locator).get(0);
    }
}
