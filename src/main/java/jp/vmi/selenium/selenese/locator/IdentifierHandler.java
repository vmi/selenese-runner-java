package jp.vmi.selenium.selenese.locator;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class IdentifierHandler extends NameHandler {

    @Override
    public String locatorType() {
        return "identifier";
    }

    @Override
    public List<WebElement> handle(WebDriver driver, String arg) {
        List<WebElement> result = driver.findElements(By.id(arg));
        if (!result.isEmpty())
            return result;
        return super.handle(driver, arg);
    }
}
