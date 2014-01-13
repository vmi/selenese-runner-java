package jp.vmi.selenium.selenese.cmdproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.internal.seleniumemulation.*;

import com.google.common.collect.Maps;
import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.VarsMap;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;

/**
 * Replacement of WebDriverCommandProcessor for extention.
 */
public class SeleneseRunnerCommandProcessor implements WrapsDriver {

    private final Map<String, SeleneseCommand<?>> seleneseMethods = Maps.newHashMap();
    private final boolean enableAlertOverrides = true;
    private final WebDriver driver;

    private final Timer timer = null;
    private JavascriptLibrary javascriptLibrary = null;
    private WebDriverElementFinder elementFinder = null;
    private KeyState keyState = null;
    private AlertOverride alertOverride = null;
    private Windows windows = null;

    private final VarsMap varsMap;
    private final Eval eval;

    final List<HighlightStyleBackup> styleBackups = new ArrayList<HighlightStyleBackup>();

    /**
     * Constructor.
     *
     * @param context Selenese Runner context.
     * @param driver WebDriver instance.
     * @param varsMap variable map.
     */
    public SeleneseRunnerCommandProcessor(Context context, WebDriver driver, VarsMap varsMap) {
        this.driver = driver;
        this.varsMap = varsMap;
        this.eval = new Eval(context, varsMap);

        this.javascriptLibrary = new JavascriptLibrary();
        this.elementFinder = new WebDriverElementFinder();
        this.keyState = new KeyState();
        this.alertOverride = new AlertOverride(enableAlertOverrides);
        this.windows = new Windows(driver);
        setUpMethodMap();
    }

    private void setUpMethodMap() {
        seleneseMethods.put("addLocationStrategy", new AddLocationStrategy(elementFinder));
        seleneseMethods.put("addSelection", new AddSelection(javascriptLibrary, elementFinder));
        seleneseMethods.put("allowNativeXpath", new AllowNativeXPath());
        seleneseMethods.put("altKeyDown", new AltKeyDown(keyState));
        seleneseMethods.put("altKeyUp", new AltKeyUp(keyState));
        seleneseMethods.put("assignId", new AssignId(javascriptLibrary, elementFinder));
        seleneseMethods.put("attachFile", new AttachFile(elementFinder));
        seleneseMethods.put("captureScreenshotToString", new CaptureScreenshotToString());
        seleneseMethods.put("click", new Click(alertOverride, elementFinder));
        seleneseMethods.put("clickAt", new ClickAt(alertOverride, elementFinder));
        seleneseMethods.put("check", new Check(alertOverride, elementFinder));
        seleneseMethods.put("chooseCancelOnNextConfirmation", new SetNextConfirmationState(false));
        seleneseMethods.put("chooseOkOnNextConfirmation", new SetNextConfirmationState(true));
        seleneseMethods.put("close", new Close());
        seleneseMethods.put("createCookie", new CreateCookie());
        seleneseMethods.put("controlKeyDown", new ControlKeyDown(keyState));
        seleneseMethods.put("controlKeyUp", new ControlKeyUp(keyState));
        seleneseMethods.put("deleteAllVisibleCookies", new DeleteAllVisibleCookies());
        seleneseMethods.put("deleteCookie", new DeleteCookie());
        seleneseMethods.put("deselectPopUp", new DeselectPopUp(windows));
        seleneseMethods.put("doubleClick", new DoubleClick(alertOverride, elementFinder));
        seleneseMethods.put("dragdrop", new DragAndDrop(elementFinder));
        seleneseMethods.put("dragAndDrop", new DragAndDrop(elementFinder));
        seleneseMethods.put("dragAndDropToObject", new DragAndDropToObject(elementFinder));
        seleneseMethods.put("fireEvent", new FireEvent(elementFinder, javascriptLibrary));
        seleneseMethods.put("focus", new FireNamedEvent(elementFinder, javascriptLibrary, "focus"));
        seleneseMethods.put("getAlert", new GetAlert(alertOverride));
        seleneseMethods.put("getAllButtons", new GetAllButtons());
        seleneseMethods.put("getAllFields", new GetAllFields());
        seleneseMethods.put("getAllLinks", new GetAllLinks());
        seleneseMethods.put("getAllWindowNames", new GetAllWindowNames());
        seleneseMethods.put("getAllWindowTitles", new GetAllWindowTitles());
        seleneseMethods.put("getAttribute", new GetAttribute(javascriptLibrary, elementFinder));
        seleneseMethods.put("getAttributeFromAllWindows", new GetAttributeFromAllWindows());
        seleneseMethods.put("getBodyText", new GetBodyText());
        seleneseMethods.put("getConfirmation", new GetConfirmation(alertOverride));
        seleneseMethods.put("getCookie", new GetCookie());
        seleneseMethods.put("getCookieByName", new GetCookieByName());
        seleneseMethods.put("getElementHeight", new GetElementHeight(elementFinder));
        seleneseMethods.put("getElementIndex", new GetElementIndex(elementFinder,
            javascriptLibrary));
        seleneseMethods.put("getElementPositionLeft", new GetElementPositionLeft(elementFinder));
        seleneseMethods.put("getElementPositionTop", new GetElementPositionTop(elementFinder));
        seleneseMethods.put("getElementWidth", new GetElementWidth(elementFinder));
        // seleneseMethods.put("getEval", new GetEval(scriptMutator));
        seleneseMethods.put("getExpression", new GetExpression());
        seleneseMethods.put("getHtmlSource", new GetHtmlSource());
        seleneseMethods.put("getLocation", new GetLocation());
        seleneseMethods.put("getSelectedId", new FindFirstSelectedOptionProperty(javascriptLibrary,
            elementFinder, "id"));
        seleneseMethods.put("getSelectedIds", new FindSelectedOptionProperties(javascriptLibrary,
            elementFinder, "id"));
        seleneseMethods.put("getSelectedIndex", new FindFirstSelectedOptionProperty(javascriptLibrary,
            elementFinder, "index"));
        seleneseMethods.put("getSelectedIndexes", new FindSelectedOptionProperties(javascriptLibrary,
            elementFinder, "index"));
        seleneseMethods.put("getSelectedLabel", new FindFirstSelectedOptionProperty(javascriptLibrary,
            elementFinder, "text"));
        seleneseMethods.put("getSelectedLabels", new FindSelectedOptionProperties(javascriptLibrary,
            elementFinder, "text"));
        seleneseMethods.put("getSelectedValue", new FindFirstSelectedOptionProperty(javascriptLibrary,
            elementFinder, "value"));
        seleneseMethods.put("getSelectedValues", new FindSelectedOptionProperties(javascriptLibrary,
            elementFinder, "value"));
        seleneseMethods.put("getSelectOptions", new GetSelectOptions(javascriptLibrary, elementFinder));
        seleneseMethods.put("getSpeed", new NoOp("0"));
        seleneseMethods.put("getTable", new GetTable(elementFinder, javascriptLibrary));
        seleneseMethods.put("getText", new GetText(javascriptLibrary, elementFinder));
        seleneseMethods.put("getTitle", new GetTitle());
        seleneseMethods.put("getValue", new GetValue(elementFinder));
        seleneseMethods.put("getXpathCount", new GetXpathCount());
        // seleneseMethods.put("getCssCount", new GetCssCount());
        seleneseMethods.put("goBack", new GoBack());
        seleneseMethods.put("highlight", new Highlight(elementFinder, javascriptLibrary));
        seleneseMethods.put("isAlertPresent", new IsAlertPresent(alertOverride));
        seleneseMethods.put("isChecked", new IsChecked(elementFinder));
        seleneseMethods.put("isConfirmationPresent", new IsConfirmationPresent(alertOverride));
        seleneseMethods.put("isCookiePresent", new IsCookiePresent());
        seleneseMethods.put("isEditable", new IsEditable(elementFinder));
        seleneseMethods.put("isElementPresent", new IsElementPresent(elementFinder));
        seleneseMethods.put("isOrdered", new IsOrdered(elementFinder, javascriptLibrary));
        // seleneseMethods.put("isSomethingSelected", new IsSomethingSelected(javascriptLibrary));
        seleneseMethods.put("isTextPresent", new IsTextPresent(javascriptLibrary));
        seleneseMethods.put("isVisible", new IsVisible(elementFinder));
        seleneseMethods.put("keyDown", new KeyEvent(elementFinder, javascriptLibrary, keyState,
            "doKeyDown"));
        // FIXME if Selenium version is 2.40?
        // seleneseMethods.put("keyDownNative", new KeyDownNative());
        seleneseMethods.put("keyPress", new TypeKeys(alertOverride, elementFinder));
        // seleneseMethods.put("keyPressNative", new KeyPressNative());
        seleneseMethods.put("keyUp",
            new KeyEvent(elementFinder, javascriptLibrary, keyState, "doKeyUp"));
        // seleneseMethods.put("keyUpNative", new KeyUpNative());
        seleneseMethods.put("metaKeyDown", new MetaKeyDown(keyState));
        seleneseMethods.put("metaKeyUp", new MetaKeyUp(keyState));
        seleneseMethods.put("mouseOver", new MouseEvent(elementFinder, javascriptLibrary, "mouseover"));
        seleneseMethods.put("mouseOut", new MouseEvent(elementFinder, javascriptLibrary, "mouseout"));
        seleneseMethods.put("mouseDown", new MouseEvent(elementFinder, javascriptLibrary, "mousedown"));
        seleneseMethods.put("mouseDownAt", new MouseEventAt(elementFinder, javascriptLibrary,
            "mousedown"));
        seleneseMethods.put("mouseMove", new MouseEvent(elementFinder, javascriptLibrary, "mousemove"));
        seleneseMethods.put("mouseMoveAt", new MouseEventAt(elementFinder, javascriptLibrary,
            "mousemove"));
        seleneseMethods.put("mouseUp", new MouseEvent(elementFinder, javascriptLibrary, "mouseup"));
        seleneseMethods.put("mouseUpAt", new MouseEventAt(elementFinder, javascriptLibrary, "mouseup"));
        // seleneseMethods.put("open", new Open(baseUrl));
        // seleneseMethods.put("openWindow", new OpenWindow(baseUrl, new GetEval(scriptMutator)));
        seleneseMethods.put("refresh", new Refresh());
        seleneseMethods.put("removeAllSelections", new RemoveAllSelections(elementFinder));
        seleneseMethods.put("removeSelection", new RemoveSelection(javascriptLibrary, elementFinder));
        // seleneseMethods.put("runScript", new RunScript(scriptMutator));
        seleneseMethods.put("select",
            new SelectOption(alertOverride, javascriptLibrary, elementFinder));
        seleneseMethods.put("selectFrame", new SelectFrame(windows));
        seleneseMethods.put("selectPopUp", new SelectPopUp(windows));
        seleneseMethods.put("selectWindow", new SelectWindow(windows));
        seleneseMethods.put("setBrowserLogLevel", new NoOp(null));
        seleneseMethods.put("setContext", new NoOp(null));
        seleneseMethods.put("setSpeed", new NoOp(null));
        seleneseMethods.put("setTimeout", new SetTimeout(timer));
        seleneseMethods.put("shiftKeyDown", new ShiftKeyDown(keyState));
        seleneseMethods.put("shiftKeyUp", new ShiftKeyUp(keyState));
        seleneseMethods.put("submit", new Submit(alertOverride, elementFinder));
        seleneseMethods.put("type",
            new Type(alertOverride, javascriptLibrary, elementFinder, keyState));
        seleneseMethods.put("typeKeys", new TypeKeys(alertOverride, elementFinder));
        seleneseMethods.put("uncheck", new Uncheck(alertOverride, elementFinder));
        seleneseMethods.put("useXpathLibrary", new UseXPathLibrary());
        // seleneseMethods.put("waitForCondition", new WaitForCondition(scriptMutator));
        seleneseMethods.put("waitForFrameToLoad", new NoOp(null));
        seleneseMethods.put("waitForPageToLoad", new WaitForPageToLoad());
        seleneseMethods.put("waitForPopUp", new WaitForPopup(windows));
        seleneseMethods.put("windowFocus", new WindowFocus(javascriptLibrary));
        seleneseMethods.put("windowMaximize", new WindowMaximize(javascriptLibrary));

        // Customized methods.
        seleneseMethods.put("getEval", new GetEval(eval));
        seleneseMethods.put("openWindow", new OpenWindow(eval));
        seleneseMethods.put("runScript", new RunScript(eval));
        seleneseMethods.put("waitForCondition", new WaitForCondition(eval));
        seleneseMethods.put("sendKeys", seleneseMethods.get("typeKeys"));
        seleneseMethods.put("isSomethingSelected", new IsSomethingSelected(elementFinder));
        seleneseMethods.put("getCssCount", new GetCssCount(elementFinder));
    }

    @Override
    public WebDriver getWrappedDriver() {
        return driver;
    }

    /**
     * Check the command existence.
     * 
     * @param commandName command name.
     * @return true if the command exists.
     */
    public boolean isMethodAvailable(String commandName) {
        return seleneseMethods.containsKey(commandName);
    }

    /**
     * Execute command.
     *
     * @param commandName command name.
     * @param args arguments.
     * @return command result.
     */
    public <T> T execute(String commandName, String... args) {
        SeleneseCommand<?> command = seleneseMethods.get(commandName);
        if (command == null)
            throw new SeleniumException("No such command: " + commandName);
        try {
            @SuppressWarnings("unchecked")
            T result = (T) command.apply(driver, replaceVarsForArray(args));
            return result;
        } catch (RuntimeException e) {
            // for HtmlUnit
            if (!e.getClass().getSimpleName().contains("Script"))
                throw e;
            String message = e.getMessage().replaceFirst("\\s*\\([^()]+\\)$", "");
            throw new SeleniumException(message, e);
        }
    }

    /**
     * Convert to String from the result of execute().
     *
     * @param result the result of execute().
     * @return converted string.
     */
    public <T> String convertToString(T result) {
        if (result == null)
            return "";
        if (result instanceof Object[])
            return StringUtils.join((Object[]) result, ',');
        else
            return result.toString();
    }

    /**
     * Set variable value.
     *
     * @param value value.
     * @param varName variable name.
     */
    @Deprecated
    public void setVar(Object value, String varName) {
        varsMap.put(varName, value);
    }

    /**
     * Get variable value.
     *
     * @param varName variable name.
     * @return value.
     */
    @Deprecated
    public Object getVar(String varName) {
        return varsMap.get(varName);
    }

    /**
     * Replace variable reference to value.
     *
     * @param expr expression string.
     * @return replaced string.
     */
    @Deprecated
    public String replaceVars(String expr) {
        StrSubstitutor s = new StrSubstitutor(varsMap);
        return s.replace(expr);
    }

    /**
     * Replace variable reference to value for each strings.
     *
     * @param exprs expression strings.
     * @return replaced strings.
     */
    @Deprecated
    public String[] replaceVarsForArray(String[] exprs) {
        String[] result = new String[exprs.length];
        for (int i = 0; i < exprs.length; i++)
            result[i] = replaceVars(exprs[i]);
        return result;
    }

    /**
     * Highlight and backup specified locator.
     *
     * @param locator locator.
     * @param highlightStyle highlight style.
     */
    public void highlight(String locator, HighlightStyle highlightStyle) {
        WebElement element;
        try {
            element = elementFinder.findElement(driver, locator);
        } catch (SeleniumException e) {
            // element specified by locator is not found.
            return;
        }
        Map<String, String> prevStyles = highlightStyle.doHighlight(driver, element);
        HighlightStyleBackup backup = new HighlightStyleBackup(prevStyles, element);
        styleBackups.add(backup);
    }

    /**
     * Unhighlight backed up styles.
     */
    public void unhighlight() {
        if (styleBackups.isEmpty())
            return;
        for (HighlightStyleBackup backup : styleBackups)
            backup.restore(driver);
        styleBackups.clear();
    }

    private static class CommandArgumentInfo {
        public final int count;
        public final int[] locatorIndexes; // length = 0-2

        public CommandArgumentInfo(int count, int... locatorIndexes) {
            this.count = count;
            this.locatorIndexes = locatorIndexes;
        }
    }

    private static class CommandArgumentInfoMap extends HashMap<String, CommandArgumentInfo> {

        private static final long serialVersionUID = 1L;

        public void put(String cmd, int count, int... locatorIndexes) {
            put(cmd, new CommandArgumentInfo(count, locatorIndexes));
        }

        public int getCount(String cmd) {
            CommandArgumentInfo info = get(cmd);
            if (info == null)
                throw new IllegalArgumentException("argument count of \"" + cmd + "\" is not found.");
            return info.count;
        }

        public int[] getLocatorIndexes(String cmd) {
            CommandArgumentInfo info = get(cmd);
            if (info == null)
                throw new IllegalArgumentException("argument locator indexes of \"" + cmd + "\" is not found.");
            return info.locatorIndexes;
        }
    }

    private static final CommandArgumentInfoMap argInfoMap = new CommandArgumentInfoMap();

    static {
        CommandArgumentInfoMap m = argInfoMap;
        m.put("addLocationStrategy", 2);
        m.put("addScript", 2);
        m.put("addSelection", 2, 0, 1);
        m.put("allowNativeXpath", 1);
        m.put("altKeyDown", 0);
        m.put("altKeyUp", 0);
        m.put("answerOnNextPrompt", 1);
        m.put("assertErrorOnNext", 1);
        m.put("assertFailureOnNext", 1);
        m.put("assertSelected", 2, 0, 1);
        m.put("assignId", 2, 0);
        m.put("break", 0);
        m.put("captureEntirePageScreenshot", 2);
        m.put("check", 1, 0);
        m.put("chooseCancelOnNextConfirmation", 0);
        m.put("chooseOkOnNextConfirmation", 0);
        m.put("click", 1, 0);
        m.put("clickAt", 2, 0);
        m.put("close", 0);
        m.put("contextMenu", 1, 0);
        m.put("contextMenuAt", 2, 0);
        m.put("controlKeyDown", 0);
        m.put("controlKeyUp", 0);
        m.put("createCookie", 2);
        m.put("deleteAllVisibleCookies", 0);
        m.put("deleteCookie", 2);
        m.put("deselectPopUp", 0);
        m.put("doubleClick", 1, 0);
        m.put("doubleClickAt", 2, 0);
        m.put("dragAndDrop", 2, 0);
        m.put("dragAndDropToObject", 2, 0, 1);
        m.put("dragdrop", 2, 0);
        m.put("echo", 1);
        m.put("fireEvent", 2, 0);
        m.put("focus", 1, 0);
        m.put("getAlert", 0);
        m.put("getAllButtons", 0);
        m.put("getAllFields", 0);
        m.put("getAllLinks", 0);
        m.put("getAllWindowIds", 0);
        m.put("getAllWindowNames", 0);
        m.put("getAllWindowTitles", 0);
        m.put("getAttribute", 1, 0);
        m.put("getAttributeFromAllWindows", 1);
        m.put("getBodyText", 0);
        m.put("getConfirmation", 0);
        m.put("getCookie", 0);
        m.put("getCookieByName", 1);
        m.put("getCssCount", 1);
        m.put("getCursorPosition", 1, 0);
        m.put("getElementHeight", 1, 0);
        m.put("getElementIndex", 1, 0);
        m.put("getElementPositionLeft", 1, 0);
        m.put("getElementPositionTop", 1, 0);
        m.put("getElementWidth", 1, 0);
        m.put("getEval", 1);
        m.put("getExpression", 1);
        m.put("getHtmlSource", 0);
        m.put("getLocation", 0);
        m.put("getMouseSpeed", 0);
        m.put("getPrompt", 0);
        m.put("getSelectOptions", 1, 0);
        m.put("getSelectedId", 1, 0);
        m.put("getSelectedIds", 1, 0);
        m.put("getSelectedIndex", 1, 0);
        m.put("getSelectedIndexes", 1, 0);
        m.put("getSelectedLabel", 1, 0);
        m.put("getSelectedLabels", 1, 0);
        m.put("getSelectedValue", 1, 0);
        m.put("getSelectedValues", 1, 0);
        m.put("getSpeed", 0);
        m.put("getTable", 1);
        m.put("getText", 1, 0);
        m.put("getTitle", 0);
        m.put("getValue", 1, 0);
        m.put("getWhetherThisFrameMatchFrameExpression", 2);
        m.put("getWhetherThisWindowMatchWindowExpression", 2);
        m.put("getXpathCount", 1);
        m.put("goBack", 0);
        m.put("highlight", 1, 0);
        m.put("ignoreAttributesWithoutValue", 1);
        m.put("isAlertPresent", 0);
        m.put("isChecked", 1, 0);
        m.put("isConfirmationPresent", 0);
        m.put("isCookiePresent", 1);
        m.put("isEditable", 1, 0);
        m.put("isElementPresent", 1, 0);
        m.put("isOrdered", 2, 0, 1);
        m.put("isPromptPresent", 0);
        m.put("isSomethingSelected", 1, 0);
        m.put("isTextPresent", 1);
        m.put("isVisible", 1, 0);
        m.put("keyDown", 2, 0);
        m.put("keyPress", 2, 0);
        m.put("keyUp", 2, 0);
        m.put("metaKeyDown", 0);
        m.put("metaKeyUp", 0);
        m.put("mouseDown", 1, 0);
        m.put("mouseDownAt", 2, 0);
        m.put("mouseDownRight", 1, 0);
        m.put("mouseDownRightAt", 2, 0);
        m.put("mouseMove", 1, 0);
        m.put("mouseMoveAt", 2, 0);
        m.put("mouseOut", 1, 0);
        m.put("mouseOver", 1, 0);
        m.put("mouseUp", 1, 0);
        m.put("mouseUpAt", 2, 0);
        m.put("mouseUpRight", 1, 0);
        m.put("mouseUpRightAt", 2, 0);
        m.put("open", 1);
        m.put("openWindow", 2);
        m.put("pause", 1);
        m.put("refresh", 0);
        m.put("removeAllSelections", 1, 0);
        m.put("removeScript", 1);
        m.put("removeSelection", 2, 0, 1);
        m.put("rollup", 2);
        m.put("runScript", 1);
        m.put("select", 2, 0, 1);
        m.put("selectFrame", 1, 0);
        m.put("selectPopUp", 1);
        m.put("selectWindow", 1);
        m.put("sendKeys", 2, 0);
        m.put("setBrowserLogLevel", 1);
        m.put("setCursorPosition", 2, 0);
        m.put("setMouseSpeed", 1);
        m.put("setSpeed", 1);
        m.put("setTimeout", 1);
        m.put("shiftKeyDown", 0);
        m.put("shiftKeyUp", 0);
        m.put("store", 2);
        m.put("submit", 1, 0);
        m.put("type", 2, 0);
        m.put("typeKeys", 2, 0);
        m.put("uncheck", 1, 0);
        m.put("useXpathLibrary", 1);
        m.put("waitForCondition", 2);
        m.put("waitForFrameToLoad", 2);
        m.put("waitForPageToLoad", 1);
        m.put("waitForPopUp", 2);
        m.put("windowFocus", 0);
        m.put("windowMaximize", 0);
    }

    /**
     * Get command argument count.
     *
     * @param cmd command name.
     * @return argument count.
     */
    public static int getArgumentCount(String cmd) {
        return argInfoMap.getCount(cmd);
    }

    /**
     * Get command locator indexes.
     *
     * @param cmd command name.
     * @return locator indexes.
     */
    public static int[] getLocatorIndexes(String cmd) {
        return argInfoMap.getLocatorIndexes(cmd);
    }
}
