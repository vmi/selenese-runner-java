package jp.vmi.selenium.selenese.cmdproc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

/**
 * WebDriverCommandProcessor no timeout version.
 */
public class CustomCommandProcessor extends WebDriverCommandProcessor {

    private final Map<String, Object> varsMap;
    private final Eval eval;

    /**
     * Constructor.
     *
     * @param baseUrl base URL.
     * @param driver WebDriver instance.
     */
    public CustomCommandProcessor(String baseUrl, WebDriver driver) {
        super(baseUrl, driver);
        this.varsMap = new HashMap<String, Object>();
        this.eval = new Eval(baseUrl, varsMap);
        addMethod("getEval", new GetEval(eval));
        addMethod("openWindow", new OpenWindow(eval));
        addMethod("runScript", new RunScript(eval));
        addMethod("waitForCondition", new WaitForCondition(eval));

    }

    @Override
    public String doCommand(String commandName, String[] args) {
        Object val = execute(commandName, args);
        return val != null ? val.toString() : null;
    }

    @Override
    public String getString(String commandName, String[] args) {
        return (String) execute(commandName, args);
    }

    @Override
    public String[] getStringArray(String commandName, String[] args) {
        return (String[]) execute(commandName, args);
    }

    @Override
    public Number getNumber(String commandName, String[] args) {
        return (Number) execute(commandName, args);
    }

    @Override
    public boolean getBoolean(String commandName, String[] args) {
        return (Boolean) execute(commandName, args);
    }

    /**
     * Execute command.
     *
     * @param commandName command name.
     * @param args arguments.
     * @return command result.
     */
    public Object execute(String commandName, String[] args) {
        SeleneseCommand<?> command = getMethod(commandName);
        if (command == null)
            throw new UnsupportedOperationException(commandName);
        return command.apply(getWrappedDriver(), replaceVarsForArray(args));
    }

    /**
     * Set variable value.
     *
     * @param value value.
     * @param varName variable name.
     */
    public void setVar(Object value, String varName) {
        varsMap.put(varName, value);
    }

    /**
     * Get variable value.
     *
     * @param varName variable name.
     * @return value.
     */
    public Object getVar(String varName) {
        return varsMap.get(varName);
    }

    /**
     * Replace variable reference to value.
     *
     * @param expr expression string.
     * @return replaced string.
     */
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
    public String[] replaceVarsForArray(String[] exprs) {
        String[] result = new String[exprs.length];
        for (int i = 0; i < exprs.length; i++)
            result[i] = replaceVars(exprs[i]);
        return result;
    }

    private static final Map<String, Integer> argCntMap = new HashMap<String, Integer>();

    static {
        argCntMap.put("addLocationStrategy", 2);
        argCntMap.put("addScript", 2);
        argCntMap.put("addSelection", 2);
        argCntMap.put("allowNativeXpath", 1);
        argCntMap.put("altKeyDown", 0);
        argCntMap.put("altKeyUp", 0);
        argCntMap.put("answerOnNextPrompt", 1);
        argCntMap.put("assignId", 2);
        argCntMap.put("break", 0);
        argCntMap.put("captureEntirePageScreenshot", 2);
        argCntMap.put("check", 1);
        argCntMap.put("chooseCancelOnNextConfirmation", 0);
        argCntMap.put("chooseOkOnNextConfirmation", 0);
        argCntMap.put("click", 1);
        argCntMap.put("clickAt", 2);
        argCntMap.put("close", 0);
        argCntMap.put("contextMenu", 1);
        argCntMap.put("contextMenuAt", 2);
        argCntMap.put("controlKeyDown", 0);
        argCntMap.put("controlKeyUp", 0);
        argCntMap.put("createCookie", 2);
        argCntMap.put("deleteAllVisibleCookies", 0);
        argCntMap.put("deleteCookie", 2);
        argCntMap.put("deselectPopUp", 0);
        argCntMap.put("doubleClick", 1);
        argCntMap.put("doubleClickAt", 2);
        argCntMap.put("dragAndDrop", 2);
        argCntMap.put("dragAndDropToObject", 2);
        argCntMap.put("dragdrop", 2);
        argCntMap.put("echo", 1);
        argCntMap.put("fireEvent", 2);
        argCntMap.put("focus", 1);
        argCntMap.put("getAlert", 0);
        argCntMap.put("getAllButtons", 0);
        argCntMap.put("getAllFields", 0);
        argCntMap.put("getAllLinks", 0);
        argCntMap.put("getAllWindowIds", 0);
        argCntMap.put("getAllWindowNames", 0);
        argCntMap.put("getAllWindowTitles", 0);
        argCntMap.put("getAttribute", 1);
        argCntMap.put("getAttributeFromAllWindows", 1);
        argCntMap.put("getBodyText", 0);
        argCntMap.put("getConfirmation", 0);
        argCntMap.put("getCookie", 0);
        argCntMap.put("getCookieByName", 1);
        argCntMap.put("getCursorPosition", 1);
        argCntMap.put("getElementHeight", 1);
        argCntMap.put("getElementIndex", 1);
        argCntMap.put("getElementPositionLeft", 1);
        argCntMap.put("getElementPositionTop", 1);
        argCntMap.put("getElementWidth", 1);
        argCntMap.put("getEval", 1);
        argCntMap.put("getExpression", 1);
        argCntMap.put("getHtmlSource", 0);
        argCntMap.put("getLocation", 0);
        argCntMap.put("getMouseSpeed", 0);
        argCntMap.put("getPrompt", 0);
        argCntMap.put("getSelectedId", 1);
        argCntMap.put("getSelectedIds", 1);
        argCntMap.put("getSelectedIndex", 1);
        argCntMap.put("getSelectedIndexes", 1);
        argCntMap.put("getSelectedLabel", 1);
        argCntMap.put("getSelectedLabels", 1);
        argCntMap.put("getSelectedValue", 1);
        argCntMap.put("getSelectedValues", 1);
        argCntMap.put("getSelectOptions", 1);
        argCntMap.put("getSpeed", 0);
        argCntMap.put("getTable", 1);
        argCntMap.put("getText", 1);
        argCntMap.put("getTitle", 0);
        argCntMap.put("getValue", 1);
        argCntMap.put("getWhetherThisFrameMatchFrameExpression", 2);
        argCntMap.put("getWhetherThisWindowMatchWindowExpression", 2);
        argCntMap.put("getXpathCount", 1);
        argCntMap.put("goBack", 0);
        argCntMap.put("highlight", 1);
        argCntMap.put("ignoreAttributesWithoutValue", 1);
        argCntMap.put("isAlertPresent", 0);
        argCntMap.put("isChecked", 1);
        argCntMap.put("isConfirmationPresent", 0);
        argCntMap.put("isCookiePresent", 1);
        argCntMap.put("isEditable", 1);
        argCntMap.put("isElementPresent", 1);
        argCntMap.put("isOrdered", 2);
        argCntMap.put("isPromptPresent", 0);
        argCntMap.put("isSomethingSelected", 1);
        argCntMap.put("isTextPresent", 1);
        argCntMap.put("isVisible", 1);
        argCntMap.put("keyDown", 2);
        argCntMap.put("keyPress", 2);
        argCntMap.put("keyUp", 2);
        argCntMap.put("metaKeyDown", 0);
        argCntMap.put("metaKeyUp", 0);
        argCntMap.put("mouseDown", 1);
        argCntMap.put("mouseDownAt", 2);
        argCntMap.put("mouseDownRight", 1);
        argCntMap.put("mouseDownRightAt", 2);
        argCntMap.put("mouseMove", 1);
        argCntMap.put("mouseMoveAt", 2);
        argCntMap.put("mouseOut", 1);
        argCntMap.put("mouseOver", 1);
        argCntMap.put("mouseUp", 1);
        argCntMap.put("mouseUpAt", 2);
        argCntMap.put("mouseUpRight", 1);
        argCntMap.put("mouseUpRightAt", 2);
        argCntMap.put("open", 1);
        argCntMap.put("openWindow", 2);
        argCntMap.put("pause", 1);
        argCntMap.put("refresh", 0);
        argCntMap.put("removeAllSelections", 1);
        argCntMap.put("removeScript", 1);
        argCntMap.put("removeSelection", 2);
        argCntMap.put("rollup", 2);
        argCntMap.put("runScript", 1);
        argCntMap.put("select", 2);
        argCntMap.put("selectFrame", 1);
        argCntMap.put("selectPopUp", 1);
        argCntMap.put("selectWindow", 1);
        argCntMap.put("sendKeys", 2);
        argCntMap.put("setBrowserLogLevel", 1);
        argCntMap.put("setCursorPosition", 2);
        argCntMap.put("setMouseSpeed", 1);
        argCntMap.put("setSpeed", 1);
        argCntMap.put("setTimeout", 1);
        argCntMap.put("shiftKeyDown", 0);
        argCntMap.put("shiftKeyUp", 0);
        argCntMap.put("store", 2);
        argCntMap.put("submit", 1);
        argCntMap.put("type", 2);
        argCntMap.put("typeKeys", 2);
        argCntMap.put("uncheck", 1);
        argCntMap.put("useXpathLibrary", 1);
        argCntMap.put("waitForCondition", 2);
        argCntMap.put("waitForFrameToLoad", 2);
        argCntMap.put("waitForPageToLoad", 1);
        argCntMap.put("waitForPopUp", 2);
        argCntMap.put("windowFocus", 0);
        argCntMap.put("windowMaximize", 0);
    }

    /**
     * Get command argument count.
     *
     * @param cmd command name.
     * @return argument count.
     */
    public static int getArgumentCount(String cmd) {
        Integer cnt = argCntMap.get(cmd);
        if (cnt == null)
            throw new IllegalArgumentException("argument count of \"" + cmd + "\" is not found.");
        return cnt;
    }
}
