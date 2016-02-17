package jp.vmi.selenium.selenese.locator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.SeleniumException;
import com.thoughtworks.selenium.webdriven.ElementFinder;

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

    private static final Logger log = LoggerFactory.getLogger(WebDriverElementFinder.class);

    /**
     * Convert to option locator with parent.
     *
     * @param parentLocator parent locator.
     * @param optionLocator child option locator.
     * @return option locator with parent.
     */
    public static String convertToOptionLocatorWithParent(String parentLocator, String optionLocator) {
        return parentLocator + Locator.OPTION_LOCATOR_SEPARATOR + optionLocator;
    }

    private final Map<String, LocatorHandler> handlerMap = new HashMap<>();
    private final Map<String, OptionLocatorHandler> optionHandlerMap = new HashMap<>();

    private final List<Locator> currentFrameLocators = new ArrayList<>();

    private WebDriver noParentFrameWebDriver = null;

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
        registerHandler(new ClassHandler());
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

    private void switchToFrame(WebDriver driver, List<Locator> plocs) {
        driver.switchTo().defaultContent();
        try {
            List<Locator> selectedFrameLocators = new ArrayList<>();
            for (Locator ploc : plocs) {
                for (int index : ploc.frameIndexList)
                    driver.switchTo().frame(index);
                if (!ploc.isTypeIndex()) {
                    WebElement frame = findElementsInternal(driver, ploc, selectedFrameLocators).get(0);
                    driver.switchTo().frame(frame);
                }
                selectedFrameLocators.add(ploc);
            }
        } catch (RuntimeException e) {
            if (e instanceof NotFoundException || e.getCause() instanceof NotFoundException) {
                if (plocs == currentFrameLocators) {
                    log.warn("The selected frame has disapeared: {}", StringUtils.join(plocs, '/'));
                    log.warn("Reset selected frame.");
                }
                driver.switchTo().defaultContent();
                plocs.clear();
            } else {
                throw e;
            }
        }
    }

    private void pushFrame(WebDriver driver, Locator ploc, int index) {
        driver.switchTo().frame(index);
        ploc.frameIndexList.addLast(index);
    }

    private boolean isParentFrameUnsupported(RuntimeException e) {
        return (e instanceof UnsupportedCommandException)
            || (e instanceof WebDriverException
            && StringUtils.contains(e.getMessage(), "switchToParentFrame"));
    }

    private void popFrame(WebDriver driver, Locator ploc, List<Locator> selectedFrameLocators) {
        ploc.frameIndexList.pollLast();
        if (noParentFrameWebDriver != driver) {
            try {
                driver.switchTo().parentFrame();
                return;
            } catch (RuntimeException e) {
                if (!isParentFrameUnsupported(e))
                    throw e;
                log.warn("{} does not support \"parentFrame\".", driver.getClass().getSimpleName());
                noParentFrameWebDriver = driver;
            }
        }
        switchToFrame(driver, selectedFrameLocators);
        for (int index : ploc.frameIndexList)
            driver.switchTo().frame(index);
    }

    private List<WebElement> findElementsByLocator(LocatorHandler handler, WebDriver driver, Locator ploc, List<Locator> selectedFrameLocators) {
        List<WebElement> result = handler.handle(driver, ploc.arg);
        if (!result.isEmpty())
            return result;
        int iframeCount = driver.findElements(By.tagName("iframe")).size();
        for (int index = 0; index < iframeCount; index++) {
            pushFrame(driver, ploc, index);
            result = findElementsByLocator(handler, driver, ploc, selectedFrameLocators);
            if (result != null)
                return result;
            popFrame(driver, ploc, selectedFrameLocators);
        }
        int frameCount = driver.findElements(By.tagName("frame")).size();
        for (int index = 0; index < frameCount; index++) {
            pushFrame(driver, ploc, index);
            result = findElementsByLocator(handler, driver, ploc, selectedFrameLocators);
            if (result != null)
                return result;
            popFrame(driver, ploc, selectedFrameLocators);
        }
        return null;
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
        List<WebElement> result = new ArrayList<>();
        for (WebElement element : elements)
            result.addAll(handler.handle(element, arg));
        return result;
    }

    private List<WebElement> findElementsInternal(WebDriver driver, Locator ploc, List<Locator> selectedFrameLocators) {
        LocatorHandler handler = handlerMap.get(ploc.type);
        if (handler == null)
            throw new UnsupportedOperationException("Unknown locator type: " + ploc);
        List<WebElement> elements = findElementsByLocator(handler, driver, ploc, selectedFrameLocators);
        if (elements == null)
            throw new SeleniumException("Element " + ploc + " not found", new NoSuchElementException(ploc.toString()));
        return filterElementsByOptionLocator(elements, ploc.option);
    }

    /**
     * Find elements of specified locator.
     *
     * @param driver WebDriver.
     * @param locator locator.
     * @param selectedFrameLocators selected frame locators.
     * @return list of found elements. (empty if no element)
     */
    public List<WebElement> findElements(WebDriver driver, String locator, List<Locator> selectedFrameLocators) {
        Locator ploc = new Locator(locator);
        if (ploc.isTypeRelative()) {
            if (ploc.isRelativeTop()) {
                driver.switchTo().defaultContent();
            } else if (ploc.isRelativeParent()) {
                int size = selectedFrameLocators.size();
                if (size > 0)
                    switchToFrame(driver, selectedFrameLocators.subList(0, size - 1));
                else
                    log.warn("The current selected frame is top level. \"" + ploc + "\" is ignored.");
            } else {
                throw new SeleniumException("Invalid \"relative\" locator argument: " + ploc.arg);
            }
            return Arrays.asList(driver.switchTo().activeElement());
        } else if (ploc.isTypeIndex()) {
            switchToFrame(driver, selectedFrameLocators);
            List<WebElement> frames = driver.findElements(By.tagName("iframe"));
            if (frames.isEmpty())
                frames = driver.findElements(By.tagName("frame"));
            int index = ploc.getIndex();
            if (index < 0 || index >= frames.size())
                throw new SeleniumException("\"index\" locator argument is out of range: " + ploc.arg);
            return Arrays.asList(frames.get(index));
        } else {
            switchToFrame(driver, selectedFrameLocators);
            return findElementsInternal(driver, ploc, selectedFrameLocators);
        }
    }

    /**
     * Find elements of specified locator.
     *
     * @param driver WebDriver.
     * @param locator locator.
     * @return list of found elements. (empty if no element)
     */
    public List<WebElement> findElements(WebDriver driver, String locator) {
        return findElements(driver, locator, currentFrameLocators);
    }

    /**
     * Find an element of specified locator.
     *
     * @param driver WebDriver.
     * @param locator locator.
     * @param selectedFrameLocators selected frame locators.
     * @return found element.
     */
    public WebElement findElement(WebDriver driver, String locator, List<Locator> selectedFrameLocators) {
        return findElements(driver, locator, selectedFrameLocators).get(0);
    }

    @Override
    public WebElement findElement(WebDriver driver, String locator) {
        return findElements(driver, locator).get(0);
    }

    /**
     * Select frame or iframe.
     *
     * @param driver WebDriver.
     * @param locator locator to frame/iframe.
     */
    public void selectFrame(WebDriver driver, String locator) {
        Locator ploc = new Locator(locator);
        if (ploc.isTypeRelative()) {
            if (ploc.isRelativeTop()) {
                driver.switchTo().defaultContent();
                currentFrameLocators.clear();
            } else if (ploc.isRelativeParent()) {
                int size = currentFrameLocators.size();
                if (size > 0) {
                    currentFrameLocators.remove(size - 1);
                    switchToFrame(driver, currentFrameLocators);
                } else {
                    log.warn("The current selected frame is top level. \"" + ploc + "\" is ignored.");
                }
            } else { // neither "relative=top" nor "relative=parent"
                throw new SeleniumException("Invalid frame locator: " + ploc);
            }
        } else if (ploc.isTypeIndex()) {
            switchToFrame(driver, currentFrameLocators);
            int index = ploc.getIndex();
            try {
                driver.switchTo().frame(index);
            } catch (NoSuchFrameException e) {
                throw new SeleniumException(e);
            }
            ploc.frameIndexList.add(index);
            currentFrameLocators.add(ploc);
        } else {
            switchToFrame(driver, currentFrameLocators);
            WebElement frame = findElementsInternal(driver, ploc, currentFrameLocators).get(0);
            driver.switchTo().frame(frame);
            currentFrameLocators.add(ploc);
        }
    }

    /**
     * Get copy of current frame locators.
     *
     * @return current frame locators.
     */
    public List<Locator> getCurrentFrameLocators() {
        return new ArrayList<>(currentFrameLocators);
    }
}
