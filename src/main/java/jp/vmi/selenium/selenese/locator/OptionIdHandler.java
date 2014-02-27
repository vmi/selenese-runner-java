package jp.vmi.selenium.selenese.locator;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

class OptionIdHandler implements OptionLocatorHandler {

    @Override
    public String optionLocatorType() {
        return "id";
    }

    @Override
    public List<WebElement> handle(WebElement element, String arg) {
        return element.findElements(By.id(arg));
    }
}
