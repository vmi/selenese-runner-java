package jp.vmi.selenium.selenese.locator;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.utils.SeleniumUtils;

class LinkHandler implements LocatorHandler {

    @Override
    public String locatorType() {
        return "link";
    }

    @Override
    public List<WebElement> handle(WebDriver driver, String arg) {
        List<WebElement> result = new ArrayList<WebElement>();
        List<WebElement> as = driver.findElements(By.tagName("a"));
        for (WebElement a : as) {
            String text;
            try {
                text = a.getText().trim();
            } catch (StaleElementReferenceException e) {
                continue;
            }
            if (SeleniumUtils.patternMatches(arg, text))
                result.add(a);
        }
        return result;
    }
}
