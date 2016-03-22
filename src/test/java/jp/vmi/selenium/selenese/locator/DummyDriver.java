package jp.vmi.selenium.selenese.locator;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.webdriver.NullDriver;

class DummyDriver extends NullDriver {

    public By by;

    @Override
    public List<WebElement> findElements(By by) {
        this.by = by;
        return null;
    }
}
