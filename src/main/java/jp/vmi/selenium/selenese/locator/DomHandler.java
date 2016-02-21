package jp.vmi.selenium.selenese.locator;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class DomHandler implements LocatorHandler {

    @Override
    public String locatorType() {
        return "dom";
    }

    @Override
    public List<WebElement> handle(WebDriver driver, String arg) {
        List<WebElement> result = new ArrayList<>();
        String script = "return (" + arg + ");";
        Object element = ((JavascriptExecutor) driver).executeScript(script);
        if (element instanceof WebElement)
            result.add((WebElement) element);
        return result;
    }
}
