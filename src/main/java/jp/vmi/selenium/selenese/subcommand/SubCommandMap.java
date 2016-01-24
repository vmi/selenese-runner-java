package jp.vmi.selenium.selenese.subcommand;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;
import com.thoughtworks.selenium.webdriven.SeleneseCommand;
import com.thoughtworks.selenium.webdriven.commands.*;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;
import jp.vmi.selenium.selenese.subcommand.MouseEventHandler.MouseEventType;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Replacement of WebDriverCommandProcessor for extention.
 */
public class SubCommandMap {

    private final Map<String, ISubCommand<?>> subCommands = Maps.newHashMap();
    private final boolean enableAlertOverrides = true;

    private final JavascriptLibrary javascriptLibrary;
    private final KeyState keyState;
    private final AlertOverride alertOverride;
    private final SeleneseRunnerWindows windows;

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
    public SubCommandMap(Context context) {
        this.context = context;
        this.javascriptLibrary = new JavascriptLibrary();
        this.keyState = new KeyState();
        this.alertOverride = new AlertOverride(enableAlertOverrides);
        this.windows = new SeleneseRunnerWindows(context);
        setUpSubCommands();
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

    private void setUpSubCommands() {
        WebDriverElementFinder elementFinder = context.getElementFinder();
        register(new AddLocationStrategy(elementFinder), "addLocationStrategy", VALUE, VALUE);
        register(new AddSelection(javascriptLibrary, elementFinder), "addSelection", LOCATOR, OPTION_LOCATOR);
        register(new AllowNativeXPath(), "allowNativeXpath", VALUE);
        register(new AltKeyDown(keyState), "altKeyDown");
        register(new AltKeyUp(keyState), "altKeyUp");
        register(new AssignId(javascriptLibrary, elementFinder), "assignId", LOCATOR, VALUE);
        register(new AttachFile(elementFinder), "attachFile", LOCATOR, VALUE);
        // can't handle the result:
        // register(new CaptureScreenshotToString(), "captureScreenshotToString");
        register(new Click(alertOverride, elementFinder), "click", LOCATOR);
        register(new jp.vmi.selenium.selenese.subcommand.ClickAt(alertOverride, elementFinder), "clickAt", LOCATOR, VALUE);
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
        register(new GetAttribute(javascriptLibrary, elementFinder), "getAttribute", ATTRIBUTE_LOCATOR);
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
        // "getSelectedId"
        // "getSelectedIds"
        // "getSelectedIndex"
        // "getSelectedIndexes"
        // "getSelectedLabel"
        // "getSelectedLabels"
        // "getSelectedValue"
        // "getSelectedValues"
        register(new GetSelectOptions(javascriptLibrary, elementFinder), "getSelectOptions", LOCATOR);
        // "getSpeed"
        register(new GetTable(elementFinder, javascriptLibrary), "getTable", VALUE);
        register(new GetText(javascriptLibrary, elementFinder), "getText", LOCATOR);
        register(new GetTitle(), "getTitle");
        register(new GetValue(elementFinder), "getValue", LOCATOR);
        register(new GetXpathCount(), "getXpathCount", VALUE);
        // "getCssCount"
        register(new GoBack(), "goBack");
        // "highlight"
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
        register(new KeyDownNative(), "keyDownNative", LOCATOR, VALUE);
        register(new TypeKeys(alertOverride, elementFinder), "keyPress", LOCATOR, VALUE);
        register(new KeyPressNative(), "keyPressNative", LOCATOR, VALUE);
        register(new KeyEvent(elementFinder, javascriptLibrary, keyState, "doKeyUp"), "keyUp", LOCATOR, VALUE);
        register(new KeyUpNative(), "keyUpNative", LOCATOR, VALUE);
        register(new MetaKeyDown(keyState), "metaKeyDown");
        register(new MetaKeyUp(keyState), "metaKeyUp");
        for (MouseEventType type : MouseEventType.values())
            register(new MouseEventHandler(elementFinder, type));
        // "open"
        // "openWindow"
        register(new Refresh(), "refresh");
        register(new RemoveAllSelections(elementFinder), "removeAllSelections", LOCATOR);
        register(new RemoveSelection(javascriptLibrary, elementFinder), "removeSelection", LOCATOR, OPTION_LOCATOR);
        // "runScript"
        register(new SelectOption(alertOverride, javascriptLibrary, elementFinder), "select", LOCATOR, OPTION_LOCATOR);
        // "selectFrame"
        register(new SelectPopUp(windows), "selectPopUp", VALUE);
        register(new SelectWindow(windows), "selectWindow", VALUE);
        register(new NoOp(null), "setBrowserLogLevel", VALUE);
        // "setSpeed"
        // "setTimeout"
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
        // windowMaximize

        // Customized methods.
        // "openWindow"
        // "runScript"

        register(new GetEval());
        register(new GetCssCount());
        register(new GetSpeed());
        register(new IsSomethingSelected());
        register(new WaitForCondition());
        register(new GetSelected(GetSelected.Type.LABEL, false));
        register(new GetSelected(GetSelected.Type.LABEL, true));
        register(new GetSelected(GetSelected.Type.VALUE, false));
        register(new GetSelected(GetSelected.Type.VALUE, true));
        register(new GetSelected(GetSelected.Type.INDEX, false));
        register(new GetSelected(GetSelected.Type.INDEX, true));
        register(new GetSelected(GetSelected.Type.ID, false));
        register(new GetSelected(GetSelected.Type.ID, true));

        // Aliases.
        subCommands.put("sendKeys", subCommands.get("typeKeys"));
    }

    /**
     * Register SeleneseCommand as ISubCommand.
     *
     * @param seleneseCommand SeleneseCommand object.
     * @param name SeleneseCommand name.
     * @param argTypes argument types.
     */
    public void register(SeleneseCommand<?> seleneseCommand, String name, ArgumentType... argTypes) {
        subCommands.put(name, new WDCommand(seleneseCommand, name, argTypes));
    }

    /**
     * Register sub-command.
     *
     * @param subCommand ISubCommand object.
     */
    public void register(ISubCommand<?> subCommand) {
        subCommands.put(subCommand.getName(), subCommand);
    }

    /**
     * Get sub-command.
     *
     * @param commandName sub-command name.
     * @return sub-command, or null if sub-command does not exist.
     */
    public ISubCommand<?> get(String commandName) {
        return subCommands.get(commandName);
    }

    /**
     * Get read-only sub-command map.
     * @return map.
     */
    public Map<String, ISubCommand<?>> getMap() {
        return Collections.unmodifiableMap(subCommands);
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
