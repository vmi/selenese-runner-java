package jp.vmi.selenium.selenese.locator;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.utils.SeleniumUtils;

class OptionValueHandler implements OptionLocatorHandler {

    @Override
    public String optionLocatorType() {
        return "value";
    }

    @Override
    public List<WebElement> handle(WebElement element, String arg) {
        List<WebElement> options = element.findElements(By.tagName("option"));
        List<WebElement> result = new ArrayList<>(options.size());
        for (WebElement option : options)
            if (SeleniumUtils.patternMatches(arg, option.getAttribute("value")))
                result.add(option);
        return result;
    }
}
