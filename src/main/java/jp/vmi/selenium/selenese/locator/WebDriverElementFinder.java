package jp.vmi.selenium.selenese.locator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.SeleneseRunnerRuntimeException;
import jp.vmi.selenium.selenese.utils.LangUtils;
import jp.vmi.selenium.selenese.utils.SeleniumUtils;
import jp.vmi.selenium.selenese.utils.Wait;

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
public class WebDriverElementFinder {

    private static final int MIN_TIMEOUT = 3000; // ms
    private static final long RETRY_INTERVAL = 100; // ms
    private static final int MAX_FRAME_DEPTH = 16;

    private static final Logger log = LoggerFactory.getLogger(WebDriverElementFinder.class);

    /**
     * NoSuchElementException for WebDriverElementFinder.
     */
    public static class ElementFinderNoSuchElementException extends NoSuchElementException {

        private static final long serialVersionUID = 1L;

        private final String message;

        private static String newMessage(Locator ploc) {
            return "Element " + ploc + " not found";
        }

        ElementFinderNoSuchElementException(Locator ploc) {
            super(newMessage(ploc));
            message = newMessage(ploc);
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    /**
     * Convert to option locator with parent.
     *
     * @param parentLocator parent locator.
     * @param optionLocator child option locator.
     * @return option locator with parent.
     */
    @Deprecated
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
        registerHandler(new LinkTextHandler());
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

    /**
     * Get option locator handler.
     *
     * @param poptloc option locator.
     * @return option locator handler.
     */
    public OptionLocatorHandler getOptionHandler(OptionLocator poptloc) {
        OptionLocatorHandler handler = optionHandlerMap.get(poptloc.type);
        if (handler == null)
            throw new UnsupportedOperationException("Unknown option locator type: " + poptloc);
        return handler;
    }

    /**
     * Add user defined handler of element finder.
     *
     * @param strategyName strategy name.
     * @param implementation JavaScript code.
     */
    public void add(String strategyName, String implementation) {
        registerHandler(new AdditionalHandler(strategyName, implementation));
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
            if (SeleniumUtils.isElementNotFound(e)) {
                if (plocs == currentFrameLocators) {
                    log.warn("The selected frame has disapeared: {}", LangUtils.join("/", plocs));
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
        if (e instanceof UnsupportedCommandException)
            return true;
        if (e instanceof WebDriverException) {
            String msg = e.getMessage();
            return msg != null ? msg.contains("switchToParentFrame") : false;
        }
        return false;
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
        if (ploc.frameIndexList.size() >= MAX_FRAME_DEPTH) {
            log.warn("Nested (i)frame depth exceeded limit: " + MAX_FRAME_DEPTH);
            return null;
        }
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

    /**
     * Find "option" elements of specified option locator.
     *
     * @param element "select" element.
     * @param poptloc parsed option locator.
     * @return list of found "option" elements.
     */
    public List<WebElement> findOptions(WebElement element, OptionLocator poptloc) {
        return getOptionHandler(poptloc).handle(element, poptloc.arg);

    }

    private List<WebElement> findOptions(List<WebElement> elements, OptionLocator poptloc) {
        if (elements.size() == 1) {
            return findOptions(elements.get(0), poptloc);
        } else {
            OptionLocatorHandler handler = getOptionHandler(poptloc);
            List<WebElement> result = new ArrayList<>();
            for (WebElement element : elements)
                result.addAll(handler.handle(element, poptloc.arg));
            return result;
        }
    }

    private List<WebElement> findElementsInternal(WebDriver driver, Locator ploc, List<Locator> selectedFrameLocators) {
        LocatorHandler handler = handlerMap.get(ploc.type);
        if (handler == null)
            throw new UnsupportedOperationException("Unknown locator type: " + ploc);
        List<WebElement> elements = findElementsByLocator(handler, driver, ploc, selectedFrameLocators);
        if (elements == null)
            throw new ElementFinderNoSuchElementException(ploc);
        return ploc.poptloc == null ? elements : findOptions(elements, ploc.poptloc);
    }

    /**
     * Find elements of specified locator.
     *
     * @param driver WebDriver.
     * @param ploc parsed locator.
     * @param selectedFrameLocators selected frame locators.
     * @return list of found elements. (empty if no element)
     */
    public List<WebElement> findElements(WebDriver driver, Locator ploc, List<Locator> selectedFrameLocators) {
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
                throw new SeleneseRunnerRuntimeException("Invalid \"relative\" locator argument: " + ploc.arg);
            }
            return Arrays.asList(driver.switchTo().activeElement());
        } else if (ploc.isTypeIndex()) {
            switchToFrame(driver, selectedFrameLocators);
            List<WebElement> frames = driver.findElements(By.tagName("iframe"));
            if (frames.isEmpty())
                frames = driver.findElements(By.tagName("frame"));
            int index = ploc.getIndex();
            if (index < 0 || index >= frames.size())
                throw new SeleneseRunnerRuntimeException("\"index\" locator argument is out of range: " + ploc.arg);
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
     * @param ploc parsed locator.
     * @return list of found elements. (empty if no element)
     */
    public List<WebElement> findElements(WebDriver driver, Locator ploc) {
        return findElements(driver, ploc, currentFrameLocators);
    }

    /**
     * Find elements of specified locator.
     *
     * @param driver WebDriver.
     * @param locator locator.
     * @return list of found elements. (empty if no element)
     */
    public List<WebElement> findElements(WebDriver driver, String locator) {
        return findElements(driver, new Locator(locator));
    }

    /**
     * Find an element of specified locator.
     *
     * @param driver WebDriver.
     * @param ploc parsed locator.
     * @param selectedFrameLocators selected frame locators.
     * @return found element.
     * @throws ElementFinderNoSuchElementException throw if element not found.
     */
    public WebElement findElement(WebDriver driver, Locator ploc, List<Locator> selectedFrameLocators) {
        List<WebElement> elements = findElements(driver, ploc, selectedFrameLocators);
        if (elements.isEmpty())
            throw new ElementFinderNoSuchElementException(ploc);
        return elements.get(0);
    }

    /**
     * Find an element.
     *
     * @param driver WebDriver.
     * @param ploc parsed locator.
     * @return found element.
     * @throws ElementFinderNoSuchElementException throw if element not found.
     */
    public WebElement findElement(WebDriver driver, Locator ploc) {
        List<WebElement> elements = findElements(driver, ploc);
        if (elements.isEmpty())
            throw new ElementFinderNoSuchElementException(ploc);
        return elements.get(0);
    }

    /**
     * Find an element.
     *
     * @param driver WebDriver.
     * @param locator locator.
     * @return found element.
     * @throws ElementFinderNoSuchElementException throw if element not found.
     */
    public WebElement findElement(WebDriver driver, String locator) {
        return findElement(driver, new Locator(locator));
    }

    /**
     * Find an element.
     *
     * @param driver WebDriver.
     * @param ploc parsed locator.
     * @param isRetryable retry finding an element. If false, this is the same as {@link #findElement(WebDriver, Locator)}.
     * @param timeout timeout (ms).
     * @return found element.
     * @throws ElementFinderNoSuchElementException throw if element not found.
     */
    public WebElement findElementWithTimeout(WebDriver driver, Locator ploc, boolean isRetryable, int timeout) {
        if (timeout < MIN_TIMEOUT)
            timeout = MIN_TIMEOUT;
        long nanoTimeout = timeout * 1000L * 1000L; // ns
        long start = System.nanoTime();
        while (true) {
            try {
                List<WebElement> elements = findElements(driver, ploc);
                if (!elements.isEmpty())
                    return elements.get(0);
            } catch (NoSuchElementException e) {
                // no operation.
            }
            if (!isRetryable || System.nanoTime() - start > nanoTimeout)
                throw new ElementFinderNoSuchElementException(ploc);
            Wait.sleep(RETRY_INTERVAL);
        }
    }

    /**
     * Find an element.
     *
     * @param driver WebDriver.
     * @param locator locator.
     * @param isRetryable retry finding an element. If false, this is the same as {@link #findElement(WebDriver, Locator)}.
     * @param timeout timeout (ms).
     * @return found element.
     * @throws ElementFinderNoSuchElementException throw if element not found.
     */
    public WebElement findElementWithTimeout(WebDriver driver, String locator, boolean isRetryable, int timeout) {
        return findElementWithTimeout(driver, new Locator(locator), isRetryable, timeout);
    }

    /**
     * Select frame or iframe.
     *
     * @param driver WebDriver.
     * @param ploc parsed locator to frame/iframe.
     * @throws NoSuchFrameException If the frame cannot be found
     */
    public void selectFrame(WebDriver driver, Locator ploc) {
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
                throw new SeleneseRunnerRuntimeException("Invalid frame locator: " + ploc);
            }
        } else if (ploc.isTypeIndex()) {
            switchToFrame(driver, currentFrameLocators);
            int index = ploc.getIndex();
            driver.switchTo().frame(index);
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
     * Select frame or iframe.
     *
     * @param driver WebDriver.
     * @param locator locator to frame/iframe.
     */
    public void selectFrame(WebDriver driver, String locator) {
        selectFrame(driver, new Locator(locator));
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
