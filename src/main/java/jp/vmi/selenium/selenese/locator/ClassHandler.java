package jp.vmi.selenium.selenese.locator;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class ClassHandler implements LocatorHandler {

    @Override
    public String locatorType() {
        return "class";
    }

    @Override
    public List<WebElement> handle(WebDriver driver, String arg) {
        By by;
        if (arg != null && arg.indexOf(' ') >= 0) {
            //avoid InvalidSelectorError: Compound class names not permitted
            by = By.cssSelector("." + arg.replace(' ', '.'));
        } else {
            by = By.className(arg);
        }
        return driver.findElements(by);
    }
}
