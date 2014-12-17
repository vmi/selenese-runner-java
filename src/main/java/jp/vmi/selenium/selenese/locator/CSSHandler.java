package jp.vmi.selenium.selenese.locator;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class CSSHandler implements LocatorHandler {

    @Override
    public String locatorType() {
        return "css";
    }

    @Override
    public List<WebElement> handle(WebDriver driver, String arg) {
        return driver.findElements(By.cssSelector(fixCssSelector(arg)));
    }

    private String fixCssSelector(String cssSelector) {
        /*
         * Selenium IDE records in some cases invalid css selectors which gets fixed once executed by the IDE
         * Example:
         * css=div.tag-1. > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type="text"]
         * needs to be
         * css=div.tag-1 > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type="text"]
         */
        return cssSelector.replace(". ", " ");
    }
}
