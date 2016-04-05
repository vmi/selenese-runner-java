package jp.vmi.selenium.selenese.utils;

import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.thoughtworks.selenium.SeleniumException;
import com.thoughtworks.selenium.webdriven.commands.AlertOverride;

/**
 * Add prompt handler.
 */
public class DialogOverride extends AlertOverride {

    private static final String replaceAlertMethod;
    private static final String answerOnNextPrompt;
    private static final String getNextPrompt;
    private static final String isPromptPresent;

    static {
        Map<String, String> jsMap = SeleniumUtils.loadJS(DialogOverride.class.getResourceAsStream("DialogOverride.js"));
        replaceAlertMethod = jsMap.get("replaceAlertMethod");
        answerOnNextPrompt = jsMap.get("answerOnNextPrompt");
        getNextPrompt = jsMap.get("getNextPrompt");
        isPromptPresent = jsMap.get("isPromptPresent");
    }

    /**
     * Constructor.
     */
    public DialogOverride() {
        super(true);
    }

    @Override
    public void replaceAlertMethod(WebDriver driver) {
        super.replaceAlertMethod(driver);
        ((JavascriptExecutor) driver).executeScript(replaceAlertMethod);
    }

    /**
     * Set answer message for prompt.
     *
     * @param driver WebDriver object.
     * @param msg answer message used on next prompt.
     */
    public void answerOnNextPrompt(WebDriver driver, String msg) {
        ((JavascriptExecutor) driver).executeScript(answerOnNextPrompt, msg);
    }

    /**
     * Get next prompt.
     *
     * @param driver WebDriver object.
     * @return prompt.
     */
    public String getNextPrompt(WebDriver driver) {
        String result = (String) ((JavascriptExecutor) driver).executeScript(getNextPrompt);
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
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(isPromptPresent));
    }
}
