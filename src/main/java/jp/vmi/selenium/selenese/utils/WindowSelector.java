package jp.vmi.selenium.selenese.utils;

import java.util.Set;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Re-implementation of "com.thoughtworks.selenium.webdriven.Windows".
 */
public class WindowSelector {

    private static final String NO_PREFIX = "";

    private static final long WAIT_AFTER_SELECTING_WINDOW = 500; // ms

    private static WindowSelector windowSelector = new WindowSelector();

    /**
     * Set WindowSelector instance.
     *
     * @param ws WindowSelector instance.
     */
    public static void setInstance(WindowSelector ws) {
        windowSelector = ws;
    }

    /**
     * Get WindowSelector instance.
     *
     * @return windowSelector WindowSelector instance.
     */
    public static WindowSelector getInstance() {
        return windowSelector;
    }

    private boolean isNullWindowID(String windowID) {
        return windowID == null || windowID.isEmpty() || "null".equals(windowID);
    }

    private String getWindowHandle(WebDriver driver) {
        try {
            return driver.getWindowHandle();
        } catch (NoSuchWindowException e) {
            return null;
        }
    }

    private String switchToWindow(WebDriver driver, String nameOrHandle) {
        try {
            driver.switchTo().window(nameOrHandle);
            return nameOrHandle;
        } catch (NoSuchWindowException e) {
            return null;
        }
    }

    /**
     * Select window.
     *
     * @param context Selenese Runner context.
     * @param windowID window ID.
     * @return selected window handle or null.
     *
     * <p>
     * The following text is copied from:<br>
     * https://github.com/SeleniumHQ/selenium/blob/master/ide/main/src/content/selenium-core/reference.html<br>
     * (Note: "<strong>var=</strong>" is not supported with the restrictions of Selenium WebDriver)
     * </p>
     * <p>
     * Selects a popup window using a window locator; once a popup window has
     * been selected, all commands go to that window. To select the main
     * window again, use null as the target.
     * </p>
     *
     * <p>
     * Window locators provide different ways of specifying the window
     * object: by title, by internal JavaScript "name," or by JavaScript
     * variable.
     * </p>
     *
     * <dl>
     * <dt>title=My Special Window</dt>
     * <dd>
     * Finds the window using the text that appears in the title bar.
     * Be careful; two windows can share the same title.
     * If that happens, this locator will just pick one.
     * </dd>
     *
     * <dt>name=myWindow, handle=myWindow</dt>
     * <dd>
     * Finds the window using its internal JavaScript "name" property or
     * the window specified by window handle of Selenium.
     * The "name" property is the second parameter "windowName" passed
     * to the JavaScript method window.open(url, windowName, windowFeatures, replaceFlag)
     * (which Selenium intercepts).
     * </dd>
     *
     * <dt>var=variableName (* Selenese Runner not supported)</dt>
     * <dd>
     * Some pop-up windows are unnamed (anonymous), but are associated
     * with a JavaScript variable name in the current application window,
     * e.g. "window.foo = window.open(url);". In those cases, you can open
     * the window using "var=foo".
     * </dd>
     * </dl>
     *
     * <p>
     * If no window  locator prefix is provided, we'll try  to guess what you
     * mean like this:
     * </p>
     *
     * <ol>
     * <li>
     * if windowID is null, (or the string "null") then it is assumed the
     * user is referring to the original window instantiated by the browser).
     * </li>
     * <li>
     * (* Selenese Runner not supported)
     * if the value of the "windowID" parameter is a JavaScript variable
     * name in the current application window, then it is assumed that
     * this variable contains the return value from a call to the
     * JavaScript window.open() method.
     * </li>
     * <li>
     * (* on Selenese Runner, windowID is window.name or window handle)
     * Otherwise, selenium looks in a hash it maintains that maps string
     * names to window "names".
     * </li>
     * <li>
     * If that fails, we'll try looping over all of the known windows to
     * try to find the appropriate "title". Since "title" is not
     * necessarily unique, this may have unexpected behavior.
     * ({@link #selectWindow(Context, String)} does not try looping)
     * </li>
     * </ol>
     *
     * <p>
     * If you're having trouble figuring out the name of a window that you
     * want to manipulate, look at the Selenium log messages which identify
     * the names of windows created via window.open (and therefore
     * intercepted by Selenium). You will see messages like the following for
     * each window as it is opened:
     * </p>
     *
     * <p>
     * debug: window.open call intercepted; window ID (which you can use with
     * selectWindow()) is "myNewWindow"
     * </p>
     *
     * <p>
     * In some cases, Selenium will be unable to intercept a call to
     * window.open (if the call occurs during or before the "onLoad" event,
     * for example). (This is bug SEL-339.) In those cases, you can force
     * Selenium to notice the open window's name by using the Selenium
     * openWindow command, using an empty (blank) url, like this:
     * openWindow("", "myFunnyWindow").
     * </p>
     *
     * <p>
     * Arguments:<br>
     *   windowID - the JavaScript window ID of the window to select
     * </p>
     */
    public String selectWindow(Context context, String windowID) {
        WebDriver driver = context.getWrappedDriver();
        if (isNullWindowID(windowID))
            return selectPreviousWindow(context);
        else if ("_blank".equals(windowID))
            return selectBlankWindow(context);
        KeyValue wloc = KeyValue.parse(windowID, NO_PREFIX);
        switch (wloc.getKey()) {
        case NO_PREFIX:
            String handle = switchToWindow(driver, wloc.getValue());
            if (handle != null)
                return handle;
            // fall through
        case "title":
            return selectWindowWithTitle(context, wloc.getValue());
        case "name":
        case "handle":
            return switchToWindow(driver, wloc.getValue());
        default:
            throw new UnsupportedOperationException(windowID);
        }
    }

    /**
     *
     * @param context Selenese Runner context.
     * @param windowID windowID.
     * @return selected window handle or null.
     */
    public String selectPopUp(Context context, String windowID) {
        WebDriver driver = context.getWrappedDriver();
        if (isNullWindowID(windowID)) {
            Set<String> handles = driver.getWindowHandles();
            handles.remove(context.getInitialWindowHandle());
            for (String handle : handles) {
                if (switchToWindow(driver, handle) != null)
                    return handle;
            }
            return null;
        } else {
            return selectWindow(context, windowID);
        }
    }

    /**
     * Select window without current selected.
     *
     * @param context Selenese Runner context.
     * @return selected window handle or null.
     */
    public String selectPreviousWindow(Context context) {
        WebDriver driver = context.getWrappedDriver();
        String currentHandle;
        try {
            currentHandle = getWindowHandle(driver);
        } catch (WebDriverException e) {
            currentHandle = null;
        }
        Set<String> handles = driver.getWindowHandles();
        switch (handles.size()) {
        case 0:
            return null;
        case 1:
            if (currentHandle != null)
                return currentHandle;
            // fall through
        default:
            if (currentHandle != null)
                handles.remove(currentHandle);
            if (handles.size() >= 2)
                handles.remove(context.getInitialWindowHandle());
            for (String handle : handles) {
                if (switchToWindow(driver, handle) != null)
                    return handle;
            }
            return null;
        }
    }

    /**
     * Select window with title.
     *
     * @param context Selenese Runner context.
     * @param title window title.
     * @return selected window handle or null.
     */
    public String selectWindowWithTitle(Context context, String title) {
        WebDriver driver = context.getWrappedDriver();
        String currentHandle = getWindowHandle(driver);
        for (String handle : driver.getWindowHandles()) {
            if (switchToWindow(driver, handle) != null && title.equals(driver.getTitle()))
                return handle;
        }
        switchToWindow(driver, currentHandle);
        return null;
    }

    /**
     * Select blank window.
     *
     * @param context Selenese Runner context.
     * @return selected window handle or null.
     */
    public String selectBlankWindow(Context context) {
        WebDriver driver = context.getWrappedDriver();
        String currentHandle = getWindowHandle(driver);
        Set<String> handles = driver.getWindowHandles();
        // the original window will never be a _blank window, so don't even look at it
        // this is also important to skip, because the original/root window won't have
        // a name either, so if we didn't know better we might think it's a _blank popup!
        handles.remove(context.getInitialWindowHandle());
        if (handles.isEmpty())
            return null;
        for (String handle : handles) {
            if (switchToWindow(driver, handle) != null) {
                String value = (String) ((JavascriptExecutor) driver).executeScript("return window.name;");
                if (value == null || value.isEmpty())
                    return handle;
            }
        }
        switchToWindow(driver, currentHandle);
        return null;
    }

    /**
     * Wait after selecting a window if the browser is Firefox.
     *
     * @param context Selenese Runner context.
     */
    public static void waitAfterSelectingWindowIfNeed(Context context) {
        if (WebDriverManager.FIREFOX.equals(context.getBrowserName())) {
            // workaround for new FirefoxDriver.
            try {
                Thread.sleep(WAIT_AFTER_SELECTING_WINDOW);
            } catch (InterruptedException e) {
                // ignore.
            }
        }
    }
}
