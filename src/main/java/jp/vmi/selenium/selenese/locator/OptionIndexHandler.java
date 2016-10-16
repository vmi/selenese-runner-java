package jp.vmi.selenium.selenese.locator;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.SeleneseRunnerRuntimeException;

class OptionIndexHandler implements OptionLocatorHandler {

    @Override
    public String optionLocatorType() {
        return "index";
    }

    @Override
    public List<WebElement> handle(WebElement element, String arg) {
        try {
            int index = Integer.parseInt(arg);
            List<WebElement> options = element.findElements(By.tagName("option"));
            List<WebElement> result = new ArrayList<>();
            if (index < options.size())
                result.add(options.get(index));
            return result;
        } catch (NumberFormatException e) {
            throw new SeleneseRunnerRuntimeException("Invalid index: " + arg);
        }
    }
}
