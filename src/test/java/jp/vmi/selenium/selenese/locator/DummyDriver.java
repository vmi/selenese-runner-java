package jp.vmi.selenium.selenese.locator;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

class DummyDriver extends HtmlUnitDriver {

    public By by;

    @Override
    public List<WebElement> findElements(By by) {
        this.by = by;
        return null;
    }
}