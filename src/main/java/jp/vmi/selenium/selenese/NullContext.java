package jp.vmi.selenium.selenese;

import java.io.PrintStream;
import java.util.EnumSet;

import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.rollup.RollupRules;
import jp.vmi.selenium.selenese.command.CommandListIterator;
import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.javascript.JSLibrary;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;
import jp.vmi.selenium.selenese.log.CookieFilter;
import jp.vmi.selenium.selenese.log.LogFilter;
import jp.vmi.selenium.selenese.log.PageInformation;
import jp.vmi.selenium.selenese.subcommand.SubCommandMap;

/**
 * Null implementation of Context.
 */
public class NullContext implements Context {

    @Override
    public void prepareWebDriver() {
    }

    @Override
    public TestCase getCurrentTestCase() {
        return null;
    }

    @Override
    public void setCurrentTestCase(TestCase testCase) {
    }

    @Override
    public PrintStream getPrintStream() {
        return null;
    }

    @Override
    public WebDriver getWrappedDriver() {
        return null;
    }

    @Override
    public String getOverridingBaseURL() {
        return null;
    }

    @Override
    public String getCurrentBaseURL() {
        return null;
    }

    @Override
    public ICommandFactory getCommandFactory() {
        return null;
    }

    @Override
    public CommandListIterator getCommandListIterator() {
        return null;
    }

    @Override
    public void pushCommandListIterator(CommandListIterator commandListIterator) {
    }

    @Override
    public void popCommandListIterator() {
    }

    @Override
    public VarsMap getVarsMap() {
        return null;
    }

    @Override
    public RollupRules getRollupRules() {
        return null;
    }

    @Override
    public CollectionMap getCollectionMap() {
        return null;
    }

    @Override
    public String getInitialWindowHandle() {
        return null;
    }

    @Override
    public WebDriverElementFinder getElementFinder() {
        return null;
    }

    @Override
    public Eval getEval() {
        return null;
    }

    @Override
    public boolean isTrue(String expr) {
        return false;
    }

    @Override
    public SubCommandMap getSubCommandMap() {
        return null;
    }

    @Override
    public int getTimeout() {
        return 0;
    }

    @Override
    public void setTimeout(int timeout) {
    }

    @Override
    public void resetSpeed() {
    }

    @Override
    public void setSpeed(long speed) {
    }

    @Override
    public long getSpeed() {
        return 0;
    }

    @Override
    public void waitSpeed() {
    }

    @Override
    public PageInformation getLatestPageInformation() {
        return null;
    }

    @Override
    public void setLatestPageInformation(PageInformation pageInformation) {
    }

    @Override
    public EnumSet<LogFilter> getLogFilter() {
        return null;
    }

    @Override
    public CookieFilter getCookieFilter() {
        return null;
    }

    @Override
    public void setCookieFilter(CookieFilter cookieFilter) {
    }

    @Override
    public JSLibrary getJSLibrary() {
        return null;
    }

    @Override
    public void setJSLibrary(JSLibrary jsLibrary) {
    }

    @Override
    public ModifierKeyState getModifierKeyState() {
        return null;
    }

    @Override
    public void resetState() {
    }

    @Override
    public boolean isInteractive() {
        return false;
    }

    @Override
    public AlertActionListener getNextNativeAlertActionListener() {
        return null;
    }
}
