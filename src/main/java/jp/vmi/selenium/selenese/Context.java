package jp.vmi.selenium.selenese;

import java.io.PrintStream;
import java.util.EnumSet;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;

import jp.vmi.selenium.rollup.RollupRules;
import jp.vmi.selenium.selenese.command.CommandListIterator;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.javascript.JSLibrary;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;
import jp.vmi.selenium.selenese.log.CookieFilter;
import jp.vmi.selenium.selenese.log.LogFilter;
import jp.vmi.selenium.selenese.log.PageInformation;

/**
 * Selenese Runner Context.
 */
public interface Context extends WrapsDriver, SubCommandMapProvider {

    /**
     * Prepare WebDriver.
     */
    void prepareWebDriver();

    /**
     * Get browser name.
     *
     * @return browser name.
     */
    default String getBrowserName() {
        return "";
    }

    /**
     * Test browser name.
     *
     * @param name browser name.
     * @return true if match browser name.
     */
    default boolean isBrowser(String name) {
        return name.equals(getBrowserName());
    }

    /**
     * Get current test-case.
     *
     * @return current test-case.
     */
    TestCase getCurrentTestCase();

    /**
     * Set current test-case.
     *
     * @param testCase current test-case.
     */
    void setCurrentTestCase(TestCase testCase);

    /**
     * Get PrintStream for logging.
     *
     * @return PrintStram object.
     */
    PrintStream getPrintStream();

    /**
     * Get base URL for overriding test-case base URL.
     *
     * @return base URL.
     */
    String getOverridingBaseURL();

    /**
     * Get current base URL.
     *
     * @return base URL.
     */
    String getCurrentBaseURL();

    /**
     * Get CommandFactory instance.
     *
     * @return CommandFactory instance.
     */
    ICommandFactory getCommandFactory();

    /**
     * Get current CommandListIterator.
     *
     * @return current CommanListIterator.
     */
    CommandListIterator getCommandListIterator();

    /**
     * Push CommandListIterator.
     *
     * @param commandListIterator CommanListIterator.
     */
    void pushCommandListIterator(CommandListIterator commandListIterator);

    /**
     * Pop CommandListIterator.
     */
    void popCommandListIterator();

    /**
     * Get variables map.
     *
     * @return VarsMap.
     */
    VarsMap getVarsMap();

    /**
     * Get test-case map.
     *
     * @return test-case map.
     */
    default TestCaseMap getTestCaseMap() {
        return TestCaseMap.EMPTY;
    }

    /**
     * Set test-case map.
     *
     * @param testCaseMap test-case map.
     */
    default void setTestCaseMap(TestCaseMap testCaseMap) {
        // no operation.
    }

    /**
     * Get flow control state.
     *
     * @param command flow control command.
     * @param <T> the class implements FlowControlState.
     * @return flow control state.
     */
    default <T extends FlowControlState> T getFlowControlState(ICommand command) {
        return null;
    }

    /**
     * Set flow control state.
     *
     * @param command flow control command.
     * @param state flow control state.
     * @param <T> the class implements FlowControlState.
     */
    default <T extends FlowControlState> void setFlowControlState(ICommand command, T state) {
        // no operation.
    }

    /**
     * Get rollup rules.
     *
     * @return RollupRules object.
     */
    RollupRules getRollupRules();

    /**
     * Get collection map.
     *
     * @return CollectionMap.
     */
    CollectionMap getCollectionMap();

    /**
     * Get initial window handle.
     *
     * @return window handle.
     */
    String getInitialWindowHandle();

    /**
     * Get elemnt finder.
     *
     * @return element finder.
     */
    WebDriverElementFinder getElementFinder();

    /**
     * Find elements
     *
     * @param locator locator.
     * @return list of found elements. (empty if no element)
     */
    default List<WebElement> findElements(String locator) {
        return getElementFinder().findElements(getWrappedDriver(), locator);
    }

    /**
     * Find an element
     *
     * @param locator locator.
     * @return found element.
     * @throws NoSuchElementException throw if element not found.
     */
    default WebElement findElement(String locator) {
        return getElementFinder().findElement(getWrappedDriver(), locator);
    }

    /**
     * Get evaluater.
     *
     * @return eval object.
     */
    Eval getEval();

    /**
     * Get boolean value of expr.
     *
     * @param expr expression.
     * @return cast from result of expr to Javascript Boolean.
     */
    boolean isTrue(String expr);

    /**
     * Get timeout for waiting. (ms)
     *
     * @return timeout for waiting.
     */
    int getTimeout();

    /**
     * Set timeout for waiting. (ms)
     *
     * @param timeout for waiting.
     */
    void setTimeout(int timeout);

    /**
     * Reset speed as initial speed.
     */
    void resetSpeed();

    /**
     * Get speed for setSpeed command.
     *
     * @return speed.
     */
    long getSpeed();

    /**
     * Set speed for setSpeed command.
     *
     * @param speed speed.
     */
    void setSpeed(long speed);

    /**
     * Wait according to speed setting.
     */
    void waitSpeed();

    /**
     * Get latest page information.
     *
     * @return page information.
     */
    PageInformation getLatestPageInformation();

    /**
     * Set latest page information.
     *
     * @param pageInformation page information.
     */
    void setLatestPageInformation(PageInformation pageInformation);

    /**
     * Get list of disabled page information.
     *
     * @return list of disabled page information.
     */
    EnumSet<LogFilter> getLogFilter();

    /**
     * Get cookie filter.
     *
     * @return cookie filter.
     */
    CookieFilter getCookieFilter();

    /**
     * Set cookie filter.
     *
     * @param cookieFilter cookie filter.
     */
    void setCookieFilter(CookieFilter cookieFilter);

    /**
     * Get JavaScript library handler.
     *
     * @return JavaScript library handler.
     */
    JSLibrary getJSLibrary();

    /**
     * Set JavaScript library handler.
     *
     * @param jsLibrary JavaScript library handler.
     */
    void setJSLibrary(JSLibrary jsLibrary);

    /**
     * Get modifier key state.
     *
     * @return modifier key state.
     */
    ModifierKeyState getModifierKeyState();

    /**
     * Reset internal state.
     */
    void resetState();

    /**
     * Get interactive.
     *
     * @return interactive.
     *
     * @deprecated use {@link InteractiveModeHandler#isEnabled()} with {@link #getInteractiveModeHandler()} instead.
     */
    @Deprecated
    boolean isInteractive();

    /**
     * Get next native alert action.
     *
     * @return next native alert state
     */
    AlertActionListener getNextNativeAlertActionListener();

    /**
     * Is Action command W3C compatible?
     *
     * @return true if Action command is W3C compatible.
     */
    default boolean isW3cAction() {
        return false;
    }

    /**
     * Executes JavaScript in the context of the currently selected frame or window.
     *
     * see {@link JavascriptExecutor#executeScript(String, Object...)}.
     *
     * @param <T> type of return value.
     * @param script The JavaScript to execute
     * @param args The arguments to the script. May be empty
     * @return One of Boolean, Long, String, List or WebElement. Or null.
     */
    default <T> T executeScript(String script, Object... args) {
        @SuppressWarnings("unchecked")
        T result = (T) ((JavascriptExecutor) getWrappedDriver()).executeScript(script, args);
        return result;
    }

    /**
     * Executes JavaScript in the context of the currently selected frame or window.
     *
     * see {@link JavascriptExecutor#executeAsyncScript(String, Object...)}.
     *
     * @param <T> type of return value.
     * @param script The JavaScript to execute
     * @param args The arguments to the script. May be empty
     * @return One of Boolean, Long, String, List or WebElement. Or null.
     */
    default <T> T executeAsyncScript(String script, Object... args) {
        @SuppressWarnings("unchecked")
        T result = (T) ((JavascriptExecutor) getWrappedDriver()).executeAsyncScript(script, args);
        return result;
    }

    /**
     * Reset number of actually used retries.
     */
    default void resetRetries() {
    }

    /**
     * Increment number of actually used retries.
     */
    default void incrementRetries() {
    }

    /**
     * True if number of actually used retries has reached maximum number of retries.
     *
     * @return true if retries has reached max retries.
     */
    default boolean hasReachedMaxRetries() {
        return true;
    }

    /**
     * Get number of actually used retries.
     *
     * @return number of actually used retries.
     */
    default int getRetries() {
        return 0;
    }

    /**
     * Get maximum number of retries allowed for a given test.
     *
     * @return the maximum number of retries allowed for a given test.
     */
    default int getMaxRetries() {
        return 0;
    }

    /**
     * Set the maximum number of retries allowed for a given test.
     *
     * @param maxRetries the maximum number of retries allowed for a given test.
     */
    default void setMaxRetries(int maxRetries) {
    }

    /**
     * Setup MaxTimeActiveTimer.
     * @param maxTime the maxTime in milliseconds.
     */
    default void setupMaxTimeTimer(long maxTime) {
    }

    /**
     * Return whether to replace alert methods.
     * @return <code>true</code> to replace alert method, <code>false</code> otherwise.
     */
    default boolean isReplaceAlertMethod() {
        return true;
    }

    /**
     * Get interactive mode handler.
     *
     * @return interactive mode handler.
     */
    default InteractiveModeHandler getInteractiveModeHandler() {
        return InteractiveModeHandler.ALWAYS_NON_INTERACTIVE;
    }
}
