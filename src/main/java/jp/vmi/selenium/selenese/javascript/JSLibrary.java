package jp.vmi.selenium.selenese.javascript;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.ModifierKeyState;
import jp.vmi.selenium.selenese.SeleneseRunnerRuntimeException;

/**
 * Override dialog interfaces.
 */
public class JSLibrary {

    /**
     * Key event type.
     */
    @SuppressWarnings("javadoc")
    public enum KeyEventType {
        KEYDOWN, KEYPRESS, KEYUP;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private final JSFunction replaceAlertMethod;
    private final JSFunction getNextAlert;
    private final JSFunction isAlertPresent;
    private final JSFunction setNextConfirmationState;
    private final JSFunction getNextConfirmation;
    private final JSFunction isConfirmationPresent;
    private final JSFunction answerOnNextPrompt;
    private final JSFunction getNextPrompt;
    private final JSFunction isPromptPresent;
    private final JSFunction triggerKeyEvent;
    private final JSFunction setCursorPosition;
    private final JSFunction getCursorPosition;
    private final JSFunction getElementIndex;
    private final JSFunction isOrdered;
    private final JSFunction getTable;
    private final JSFunction getText;

    private final JSFunction fireEvent;

    /**
     * Constructor.
     */
    public JSLibrary() {
        Map<String, JSFunction> jsMap = JSFunction.load(JSLibrary.class.getResourceAsStream("JSLibrary.js"));
        replaceAlertMethod = jsMap.get("replaceAlertMethod");
        getNextAlert = jsMap.get("getNextAlert");
        isAlertPresent = jsMap.get("isAlertPresent");
        setNextConfirmationState = jsMap.get("setNextConfirmationState");
        getNextConfirmation = jsMap.get("getNextConfirmation");
        isConfirmationPresent = jsMap.get("isConfirmationPresent");
        answerOnNextPrompt = jsMap.get("answerOnNextPrompt");
        getNextPrompt = jsMap.get("getNextPrompt");
        isPromptPresent = jsMap.get("isPromptPresent");
        triggerKeyEvent = jsMap.get("triggerKeyEvent");
        setCursorPosition = jsMap.get("setCursorPosition");
        getCursorPosition = jsMap.get("getCursorPosition");
        getElementIndex = jsMap.get("getElementIndex");
        isOrdered = jsMap.get("isOrdered");
        getTable = jsMap.get("getTable");
        getText = jsMap.get("getText");
        // fireEvent.js is copied from selenium.
        fireEvent = JSFunction.loadFunction(JSLibrary.class.getResourceAsStream("fireEvent.js"));
    }

    /**
     * Replace alert/confirm/prompt methods.
     *
     * @param driver WebDriver object.
     * @param element target element for detecting frame/iframe.
     */
    public void replaceAlertMethod(WebDriver driver, WebElement element) {
        replaceAlertMethod.call(driver, element);
    }

    /**
     * Get next alert dialog message.
     *
     * @param driver WebDriver object.
     * @return alert dialog message.
     */
    public String getNextAlert(WebDriver driver) {
        String result = getNextAlert.call(driver);
        if (result == null)
            throw new SeleneseRunnerRuntimeException("There were no alerts");
        return result;
    }

    /**
     * Test of whether a alert dialog was displayed.
     *
     * @param driver WebDriver object.
     * @return alert dialog is dislayed if true.
     */
    public boolean isAlertPresent(WebDriver driver) {
        return Boolean.TRUE.equals(isAlertPresent.call(driver));
    }

    /**
     * Choose button on next confirm dialog.
     *
     * @param driver WebDriver object.
     * @param state "OK" if true, otherwise "Cancel".
     */
    public void setNextConfirmationState(WebDriver driver, boolean state) {
        setNextConfirmationState.call(driver, state);
    }

    /**
     * Get next confirmation dialog message.
     *
     * @param driver WebDriver object.
     * @return confirmation dialog message.
     */
    public String getNextConfirmation(WebDriver driver) {
        String result = getNextConfirmation.call(driver);
        if (result == null)
            throw new SeleneseRunnerRuntimeException("There were no confirmations");
        return result;
    }

    /**
     * Test of whether a confirmation dialog was displayed.
     *
     * @param driver WebDriver object.
     * @return confirmation dialog is dislayed if true.
     */
    public boolean isConfirmationPresent(WebDriver driver) {
        return Boolean.TRUE.equals(isConfirmationPresent.call(driver));
    }

    /**
     * Set answer message for prompt.
     *
     * @param driver WebDriver object.
     * @param msg answer message used on next prompt.
     */
    public void answerOnNextPrompt(WebDriver driver, String msg) {
        answerOnNextPrompt.call(driver, msg);
    }

    /**
     * Get next prompt.
     *
     * @param driver WebDriver object.
     * @return prompt.
     */
    public String getNextPrompt(WebDriver driver) {
        String result = getNextPrompt.call(driver);
        if (result == null)
            throw new SeleneseRunnerRuntimeException("There were no prompts");
        return result;
    }

    /**
     * Test of whether a prompt was displayed.
     *
     * @param driver WebDriver object.
     * @return prompt is dislayed if true.
     */
    public boolean isPromptPresent(WebDriver driver) {
        return Boolean.TRUE.equals(isPromptPresent.call(driver));
    }

    /**
     * Trigger key event.
     *
     * @param driver WebDriver object.
     * @param element target element.
     * @param eventType event type.
     * @param keySequence key sequence.
     * @param keyState modifier key state.
     */
    public void triggerKeyEvent(WebDriver driver, WebElement element, KeyEventType eventType, String keySequence, ModifierKeyState keyState) {
        int keyCode;
        if (keySequence.codePointCount(0, keySequence.length()) == 1) {
            keyCode = keySequence.codePointAt(0);
        } else {
            try {
                if (keySequence.startsWith("\\"))
                    keyCode = Integer.parseInt(keySequence.substring(1));
                else
                    keyCode = Integer.parseInt(keySequence);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid keySequence");
            }
        }
        triggerKeyEvent.call(driver, element, eventType.toString(), keyCode,
            keyState.isControlKeyDown(), keyState.isAltKeyDown(), keyState.isShiftKeyDown(), keyState.isMetaKeyDown());
    }

    /**
     * Set cursor poision in text field.
     *
     * @param driver WebDriver object.
     * @param element target element.
     * @param position cusror position.
     *
     */
    public void setCursorPosition(WebDriver driver, WebElement element, int position) {
        setCursorPosition.call(driver, element, position);
    }

    /**
     * Get cursor poision in text field.
     *
     * @param driver WebDriver object.
     * @param element target element.
     * @return cusror position.
     *
     */
    public long getCursorPosition(WebDriver driver, WebElement element) {
        return getCursorPosition.call(driver, element);
    }

    /**
     * Get element index.
     *
     * @param driver WebDriver object.
     * @param element target element.
     * @return element index.
     */
    public long getElementIndex(WebDriver driver, WebElement element) {
        return getElementIndex.call(driver, element);
    }

    /**
     * Is ordered.
     *
     * @param driver WebDriver object.
     * @param element1 target element 1.
     * @param element2 target element 2.
     * @return true if two elements are ordered.
     */
    public boolean isOrdered(WebDriver driver, WebElement element1, WebElement element2) {
        return Boolean.TRUE.equals(isOrdered.call(driver, element1, element2));
    }

    /**
     * Get cell value of table.
     *
     * @param driver WebDriver object.
     * @param table table element.
     * @param row row number. (row start at 0)
     * @param col column number. (column start at 0)
     * @return cell element or error message string.
     */
    public Object getTable(WebDriver driver, WebElement table, long row, long col) {
        return getTable.call(driver, table, row, col);
    }

    /**
     * Get text.
     *
     * @param driver WebDriver object.
     * @param element element.
     * @return text content.
     */
    public String getText(WebDriver driver, WebElement element) {
        return getText.call(driver, element);
    }

    /**
     * Fire event.
     *
     * @param driver WebDriver object.
     * @param element target element.
     * @param eventName event name.
     *
     */
    public void fireEvent(WebDriver driver, WebElement element, String eventName) {
        fireEvent.call(driver, element, eventName);
    }
}
