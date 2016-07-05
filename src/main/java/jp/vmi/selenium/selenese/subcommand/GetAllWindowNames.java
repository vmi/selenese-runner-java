package jp.vmi.selenium.selenese.subcommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.selenese.Context;

/**
 * "getAllWindowNames".
 */
public class GetAllWindowNames extends AbstractSubCommand<List<String>> {

    /**
     * Constructor.
     */
    public GetAllWindowNames() {
        super();
    }

    private static boolean switchToWindow(WebDriver driver, String handle) {
        try {
            driver.switchTo().window(handle);
            return true;
        } catch (NoSuchWindowException e) {
            return false;
        }
    }

    private static String getNameOrHandle(WebDriver driver, String handle) {
        String name = ((JavascriptExecutor) driver).executeScript("return window.name").toString();
        return (name == null || name.isEmpty()) ? handle : name;
    }

    @Override
    public List<String> execute(Context context, String... args) {
        WebDriver driver = context.getWrappedDriver();
        String initHandle = context.getInitialWindowHandle();
        String currentHandle = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        List<String> result = new ArrayList<String>(handles.size());
        result.add(getNameOrHandle(driver, currentHandle));
        handles.remove(currentHandle);
        if (!currentHandle.equals(initHandle) && handles.contains(initHandle)) {
            switchToWindow(driver, initHandle);
            result.add(0, getNameOrHandle(driver, initHandle));
            handles.remove(initHandle);
        }
        for (String handle : handles) {
            if (switchToWindow(driver, handle))
                result.add(getNameOrHandle(driver, handle));
        }
        switchToWindow(driver, currentHandle);
        return result;
    }
}
