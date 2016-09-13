package jp.vmi.selenium.selenese;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.utils.JSFunction;

/**
 * Override dialog interfaces.
 */
public class DialogOverride {

    private static final JSFunction replaceAlertMethod;
    private static final JSFunction getNextAlert;
    private static final JSFunction isAlertPresent;
    private static final JSFunction getNextConfirmation;
    private static final JSFunction isConfirmationPresent;
    private static final JSFunction answerOnNextPrompt;
    private static final JSFunction getNextPrompt;
    private static final JSFunction isPromptPresent;

    static {
        Map<String, JSFunction> jsMap = JSFunction.load(DialogOverride.class.getResourceAsStream("DialogOverride.js"));
        replaceAlertMethod = jsMap.get("replaceAlertMethod");
        getNextAlert = jsMap.get("getNextAlert");
        isAlertPresent = jsMap.get("isAlertPresent");
        getNextConfirmation = jsMap.get("getNextConfirmation");
        isConfirmationPresent = jsMap.get("isConfirmationPresent");
        answerOnNextPrompt = jsMap.get("answerOnNextPrompt");
        getNextPrompt = jsMap.get("getNextPrompt");
        isPromptPresent = jsMap.get("isPromptPresent");
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
            throw new SeleniumException("There were no alerts");
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
     * Get next confirmation dialog message.
     *
     * @param driver WebDriver object.
     * @return confirmation dialog message.
     */
    public String getNextConfirmation(WebDriver driver) {
        String result = getNextConfirmation.call(driver);
        if (result == null)
            throw new SeleniumException("There were no confirmations");
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
            throw new SeleniumException("There were no prompts");
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
}
