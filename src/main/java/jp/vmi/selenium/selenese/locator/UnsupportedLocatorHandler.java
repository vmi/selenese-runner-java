package jp.vmi.selenium.selenese.locator;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class UnsupportedLocatorHandler implements LocatorHandler {

    private static final Logger log = LoggerFactory.getLogger(UnsupportedLocatorHandler.class);

    @Override
    public List<WebElement> handle(WebDriver driver, String arg) {
        log.warn("\"{}\" locator is not supported. It returns an empty list.", locatorType());
        return new ArrayList<>();
    }
}
