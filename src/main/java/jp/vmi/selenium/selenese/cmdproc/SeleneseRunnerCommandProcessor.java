package jp.vmi.selenium.selenese.cmdproc;

import java.util.Map;

import org.openqa.selenium.internal.seleniumemulation.*;

import com.google.common.collect.Maps;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;

import static jp.vmi.selenium.selenese.cmdproc.ArgType.*;

/**
 * Replacement of WebDriverCommandProcessor for extention.
 */
public class SeleneseRunnerCommandProcessor {

    private final Map<String, WDCommand> wdCommands = Maps.newHashMap();
    private final boolean enableAlertOverrides = true;

    private final Timer timer = null;
    private JavascriptLibrary javascriptLibrary = null;
    private KeyState keyState = null;
    private AlertOverride alertOverride = null;
    private SeleneseRunnerWindows windows = null;

    private final Context context;

    /**
     * Constructor.
     * <div>
     * Requrements:
     * <ul>
     * <li>context.getEval() is available.</li>
     * <li>context.getElementFinder() is available.</li>
     * </ul>
     * </div>
     * 
     * @param context Selenese Runner context.
     */
    public SeleneseRunnerCommandProcessor(Context context) {
        this.context = context;
        this.javascriptLibrary = new JavascriptLibrary();
        this.keyState = new KeyState();
        this.alertOverride = new AlertOverride(enableAlertOverrides);
        this.windows = new SeleneseRunnerWindows(context);
        setUpMethodMap();
    }

    /**
     * Get Selenese Runner context.
     * <br>
     * <b>Internal use only.</b>
     * 
     * @return Selenese Runner context.
     */
    @Deprecated
    public Context getContext() {
        return context;
    }

    private void setUpMethodMap() {
        Eval eval = context.getEval();
        WebDriverElementFinder elementFinder = context.getElementFinder();
        register(new AddLocationStrategy(elementFinder), "addLocationStrategy", VALUE, VALUE);
        register(new AddSelection(javascriptLibrary, elementFinder), "addSelection", LOCATOR, LOCATOR);
        register(new AllowNativeXPath(), "allowNativeXpath", VALUE);
        register(new AltKeyDown(keyState), "altKeyDown");
        register(new AltKeyUp(keyState), "altKeyUp");
        register(new AssignId(javascriptLibrary, elementFinder), "assignId", LOCATOR, VALUE);
        register(new AttachFile(elementFinder), "attachFile", LOCATOR, VALUE);
        // can't handle the result:
        // register(new CaptureScreenshotToString(), "captureScreenshotToString");
        register(new Click(alertOverride, elementFinder), "click", LOCATOR);
        register(new ClickAt(alertOverride, elementFinder), "clickAt", LOCATOR, VALUE);
        register(new Check(alertOverride, elementFinder), "check", LOCATOR);
        register(new SetNextConfirmationState(false), "chooseCancelOnNextConfirmation");
        register(new SetNextConfirmationState(true), "chooseOkOnNextConfirmation");
        register(new Close(), "close");
        register(new CreateCookie(), "createCookie", VALUE, VALUE);
        register(new ControlKeyDown(keyState), "controlKeyDown");
        register(new ControlKeyUp(keyState), "controlKeyUp");
        register(new DeleteAllVisibleCookies(), "deleteAllVisibleCookies");
        register(new DeleteCookie(), "deleteCookie", VALUE, VALUE); // 2nd parameter is optionsString.
        register(new DeselectPopUp(windows), "deselectPopUp");
        register(new DoubleClick(alertOverride, elementFinder), "doubleClick", LOCATOR);
        register(new DragAndDrop(elementFinder), "dragdrop", LOCATOR, VALUE);
        register(new DragAndDrop(elementFinder), "dragAndDrop", LOCATOR, VALUE);
        register(new DragAndDropToObject(elementFinder), "dragAndDropToObject", LOCATOR, LOCATOR);
        register(new FireEvent(elementFinder, javascriptLibrary), "fireEvent", LOCATOR, VALUE);
        register(new FireNamedEvent(elementFinder, javascriptLibrary, "focus"), "focus", LOCATOR);
        register(new GetAlert(alertOverride), "getAlert");
        register(new GetAllButtons(), "getAllButtons");
        register(new GetAllFields(), "getAllFields");
        register(new GetAllLinks(), "getAllLinks");
        register(new GetAllWindowNames(), "getAllWindowNames");
        register(new GetAllWindowTitles(), "getAllWindowTitles");
        register(new GetAttribute(javascriptLibrary, elementFinder), "getAttribute", LOCATOR);
        register(new GetAttributeFromAllWindows(), "getAttributeFromAllWindows", VALUE);
        register(new GetBodyText(), "getBodyText");
        register(new GetConfirmation(alertOverride), "getConfirmation");
        register(new GetCookie(), "getCookie");
        register(new GetCookieByName(), "getCookieByName", VALUE);
        register(new GetElementHeight(elementFinder), "getElementHeight", LOCATOR);
        register(new GetElementIndex(elementFinder, javascriptLibrary), "getElementIndex", LOCATOR);
        register(new GetElementPositionLeft(elementFinder), "getElementPositionLeft", LOCATOR);
        register(new GetElementPositionTop(elementFinder), "getElementPositionTop", LOCATOR);
        register(new GetElementWidth(elementFinder), "getElementWidth", LOCATOR);
        // "getEval"
        register(new GetExpression(), "getExpression", VALUE);
        register(new GetHtmlSource(), "getHtmlSource");
        register(new GetLocation(), "getLocation");
        register(new FindFirstSelectedOptionProperty(javascriptLibrary, elementFinder, "id"), "getSelectedId", LOCATOR);
        register(new FindSelectedOptionProperties(javascriptLibrary, elementFinder, "id"), "getSelectedIds", LOCATOR);
        register(new FindFirstSelectedOptionProperty(javascriptLibrary, elementFinder, "index"), "getSelectedIndex", LOCATOR);
        register(new FindSelectedOptionProperties(javascriptLibrary, elementFinder, "index"), "getSelectedIndexes", LOCATOR);
        register(new FindFirstSelectedOptionProperty(javascriptLibrary, elementFinder, "text"), "getSelectedLabel", LOCATOR);
        register(new FindSelectedOptionProperties(javascriptLibrary, elementFinder, "text"), "getSelectedLabels", LOCATOR);
        register(new FindFirstSelectedOptionProperty(javascriptLibrary, elementFinder, "value"), "getSelectedValue", LOCATOR);
        register(new FindSelectedOptionProperties(javascriptLibrary, elementFinder, "value"), "getSelectedValues", LOCATOR);
        register(new GetSelectOptions(javascriptLibrary, elementFinder), "getSelectOptions", LOCATOR);
        // "getSpeed"
        register(new GetTable(elementFinder, javascriptLibrary), "getTable", VALUE);
        register(new GetText(javascriptLibrary, elementFinder), "getText", LOCATOR);
        register(new GetTitle(), "getTitle");
        register(new GetValue(elementFinder), "getValue", LOCATOR);
        register(new GetXpathCount(), "getXpathCount", VALUE);
        // "getCssCount"
        register(new GoBack(), "goBack");
        register(new Highlight(elementFinder, javascriptLibrary), "highlight", LOCATOR);
        register(new IsAlertPresent(alertOverride), "isAlertPresent");
        register(new IsChecked(elementFinder), "isChecked", LOCATOR);
        register(new IsConfirmationPresent(alertOverride), "isConfirmationPresent");
        register(new IsCookiePresent(), "isCookiePresent", VALUE);
        register(new IsEditable(elementFinder), "isEditable", LOCATOR);
        register(new IsElementPresent(elementFinder), "isElementPresent", LOCATOR);
        register(new IsOrdered(elementFinder, javascriptLibrary), "isOrdered", LOCATOR, LOCATOR);
        // "isSomethingSelected"
        register(new IsTextPresent(javascriptLibrary), "isTextPresent", VALUE);
        register(new IsVisible(elementFinder), "isVisible", LOCATOR);
        register(new KeyEvent(elementFinder, javascriptLibrary, keyState, "doKeyDown"), "keyDown", LOCATOR, VALUE);
        // FIXME if Selenium version is 2.40?
        // register(new KeyDownNative(), "keyDownNative", LOCATOR, VALUE);
        register(new TypeKeys(alertOverride, elementFinder), "keyPress", LOCATOR, VALUE);
        // register(new KeyPressNative(), "keyPressNative", LOCATOR, VALUE);
        register(new KeyEvent(elementFinder, javascriptLibrary, keyState, "doKeyUp"), "keyUp", LOCATOR, VALUE);
        // register(new KeyUpNative(), "keyUpNative", LOCATOR, VALUE);
        register(new MetaKeyDown(keyState), "metaKeyDown");
        register(new MetaKeyUp(keyState), "metaKeyUp");
        register(new MouseEvent(elementFinder, javascriptLibrary, "mouseover"), "mouseOver", LOCATOR);
        register(new MouseEvent(elementFinder, javascriptLibrary, "mouseout"), "mouseOut", LOCATOR);
        register(new MouseEvent(elementFinder, javascriptLibrary, "mousedown"), "mouseDown", LOCATOR);
        register(new MouseEventAt(elementFinder, javascriptLibrary, "mousedown"), "mouseDownAt", LOCATOR, VALUE);
        register(new MouseEvent(elementFinder, javascriptLibrary, "mousemove"), "mouseMove", LOCATOR);
        register(new MouseEventAt(elementFinder, javascriptLibrary, "mousemove"), "mouseMoveAt", LOCATOR, VALUE);
        register(new MouseEvent(elementFinder, javascriptLibrary, "mouseup"), "mouseUp", LOCATOR);
        register(new MouseEventAt(elementFinder, javascriptLibrary, "mouseup"), "mouseUpAt", LOCATOR, VALUE);
        // "open"
        // "openWindow"
        register(new Refresh(), "refresh");
        register(new RemoveAllSelections(elementFinder), "removeAllSelections", LOCATOR);
        register(new RemoveSelection(javascriptLibrary, elementFinder), "removeSelection", LOCATOR, LOCATOR);
        // "runScript"
        register(new SelectOption(alertOverride, javascriptLibrary, elementFinder), "select", LOCATOR, LOCATOR);
        register(new SelectFrame(windows), "selectFrame", LOCATOR);
        register(new SelectPopUp(windows), "selectPopUp", VALUE);
        register(new SelectWindow(windows), "selectWindow", VALUE);
        register(new NoOp(null), "setBrowserLogLevel", VALUE);
        // "setSpeed"
        register(new SetTimeout(timer), "setTimeout", VALUE);
        register(new ShiftKeyDown(keyState), "shiftKeyDown");
        register(new ShiftKeyUp(keyState), "shiftKeyUp");
        register(new Submit(alertOverride, elementFinder), "submit", LOCATOR);
        register(new Type(alertOverride, javascriptLibrary, elementFinder, keyState), "type", LOCATOR, VALUE);
        register(new TypeKeys(alertOverride, elementFinder), "typeKeys", LOCATOR, VALUE);
        register(new Uncheck(alertOverride, elementFinder), "uncheck", LOCATOR);
        register(new UseXPathLibrary(), "useXpathLibrary", VALUE);
        // "waitForCondition"
        register(new NoOp(null), "waitForFrameToLoad", VALUE, VALUE);
        register(new WaitForPageToLoad(), "waitForPageToLoad", VALUE);
        register(new WaitForPopup(windows), "waitForPopUp", VALUE, VALUE);
        register(new WindowFocus(javascriptLibrary), "windowFocus");
        register(new WindowMaximize(javascriptLibrary), "windowMaximize");

        // Customized methods.
        register(new GetEval(eval), "getEval", VALUE);
        register(new OpenWindow(eval), "openWindow", VALUE, VALUE);
        register(new RunScript(eval), "runScript", VALUE);
        register(new WaitForCondition(eval), "waitForCondition", VALUE, VALUE);
        register(new IsSomethingSelected(elementFinder), "isSomethingSelected", LOCATOR);
        register(new GetCssCount(elementFinder), "getCssCount", CSS_LOCATOR);

        wdCommands.put("getSpeed", new GetSpeed());

        // Aliases.
        wdCommands.put("sendKeys", wdCommands.get("typeKeys"));
    }

    private void register(SeleneseCommand<?> seleneseCommand, String name, ArgType... argTypes) {
        wdCommands.put(name, new WDCommand(seleneseCommand, name, argTypes));
    }

    /**
     * Get WDCP command.
     * 
     * @param commandName command name.
     * @return WDCP command, or null if command does not exist.
     */
    public WDCommand getCommand(String commandName) {
        return wdCommands.get(commandName);
    }

    /**
     * Set variable value.
     *
     * @param value value.
     * @param varName variable name.
     */
    @Deprecated
    public void setVar(Object value, String varName) {
        context.getVarsMap().put(varName, value);
    }

    /**
     * Get variable value.
     *
     * @param varName variable name.
     * @return value.
     */
    @Deprecated
    public Object getVar(String varName) {
        return context.getVarsMap().get(varName);
    }

    /**
     * Replace variable reference to value.
     *
     * @param expr expression string.
     * @return replaced string.
     */
    @Deprecated
    public String replaceVars(String expr) {
        return context.getVarsMap().replaceVars(expr);
    }

    /**
     * Replace variable reference to value for each strings.
     *
     * @param exprs expression strings.
     * @return replaced strings.
     */
    @Deprecated
    public String[] replaceVarsForArray(String[] exprs) {
        return context.getVarsMap().replaceVarsForArray(exprs);
    }
}
