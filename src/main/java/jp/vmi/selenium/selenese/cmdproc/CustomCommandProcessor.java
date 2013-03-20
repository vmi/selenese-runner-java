package jp.vmi.selenium.selenese.cmdproc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.seleniumemulation.ElementFinder;
import org.openqa.selenium.internal.seleniumemulation.GetText;
import org.openqa.selenium.internal.seleniumemulation.JavascriptLibrary;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

import com.thoughtworks.selenium.SeleniumException;

/**
 * WebDriverCommandProcessor no timeout version.
 */
public class CustomCommandProcessor extends WebDriverCommandProcessor {

    private final Map<String, Object> varsMap;
    private final Eval eval;

    //private final JavascriptLibrary library;
    private final ElementFinder finder;

    /**
     * Constructor.
     *
     * @param baseUrl base URL.
     * @param driver WebDriver instance.
     * @param varsMap variable map.
     */
    public CustomCommandProcessor(String baseUrl, WebDriver driver, Map<String, Object> varsMap) {
        super(baseUrl, driver);
        this.varsMap = varsMap;
        this.eval = new Eval(baseUrl, varsMap);
        addMethod("getEval", new GetEval(eval));
        addMethod("openWindow", new OpenWindow(eval));
        addMethod("runScript", new RunScript(eval));
        addMethod("waitForCondition", new WaitForCondition(eval));
        // hack. FIXME
        JavascriptLibrary library = null;
        ElementFinder finder = null;
        GetText getText = (GetText) getMethod("getText");
        try {
            Class<GetText> c = GetText.class;
            //Field fieldLibrary = c.getDeclaredField("library");
            Field fieldFinder = c.getDeclaredField("finder");
            //library = (JavascriptLibrary) fieldLibrary.get(getText);
            finder = (ElementFinder) fieldFinder.get(getText);
        } catch (Exception e) {
            library = new JavascriptLibrary();
            finder = new ElementFinder(library);
        }
        //this.library = library;
        this.finder = finder;
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
            throw new SeleniumException("No such command: " + commandName);
        try {
            return command.apply(getWrappedDriver(), replaceVarsForArray(args));
        } catch (RuntimeException e) {
            // for HtmlUnit
            if (!e.getClass().getSimpleName().contains("Script"))
                throw e;
            String message = e.getMessage().replaceFirst("\\s*\\([^()]+\\)$", "");
            throw new SeleniumException(message, e);
        }
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

    private final List<HighlightStyleBackup> styleBackups = new ArrayList<HighlightStyleBackup>();

    /**
     * Highlight and backup specified locator.
     *
     * @param locator locator.
     * @param highlightStyle highlight style.
     */
    public void highlight(String locator, HighlightStyle highlightStyle) {
        WebDriver driver = getWrappedDriver();
        WebElement element;
        try {
            element = finder.findElement(driver, locator);
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
        WebDriver driver = getWrappedDriver();
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
        m.put("getSelectedId", 1, 0);
        m.put("getSelectedIds", 1, 0);
        m.put("getSelectedIndex", 1, 0);
        m.put("getSelectedIndexes", 1, 0);
        m.put("getSelectedLabel", 1, 0);
        m.put("getSelectedLabels", 1, 0);
        m.put("getSelectedValue", 1, 0);
        m.put("getSelectedValues", 1, 0);
        m.put("getSelectOptions", 1, 0);
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
