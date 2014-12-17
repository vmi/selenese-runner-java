package jp.vmi.selenium.selenese.locator;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("javadoc")
public class CSSHandlerTest {

    @Test
    public void testCSSSelector() {
        assertThat(
            getFixedCssSelector("css=div.tag-1. > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type=\"text\"]"),
            is(equalTo("css=div.tag-1 > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type=\"text\"]")));

        assertThat(
            getFixedCssSelector("css=div.tag-1 > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type=\"text\"]"),
            is(equalTo("css=div.tag-1 > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type=\"text\"]")));
    }

    private static class DummyDriver extends HtmlUnitDriver {

        public By by;

        @Override
        public List<WebElement> findElements(By by) {
            this.by = by;
            return null;
        }
    }

    private String getFixedCssSelector(String css) {
        CSSHandler cssHandler = new CSSHandler();
        DummyDriver dummyDriver = new DummyDriver();
        cssHandler.handle(dummyDriver, css);
        String byString = dummyDriver.by.toString();
        return byString.substring(byString.indexOf(':') + 2);
    }
}
