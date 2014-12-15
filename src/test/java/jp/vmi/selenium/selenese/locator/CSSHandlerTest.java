package jp.vmi.selenium.selenese.locator;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("javadoc")
public class CSSHandlerTest {

    @Test
    public void testCSSSelector() {
        CSSHandlerExt cssHandlerExt = new CSSHandlerExt();

        assertThat(
                cssHandlerExt.getFixedCssSelector("css=div.tag-1. > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type=\"text\"]"),
                is(equalTo("css=div.tag-1 > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type=\"text\"]"))
        );

        assertThat(
                cssHandlerExt.getFixedCssSelector("css=div.tag-1 > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type=\"text\"]"),
                is(equalTo("css=div.tag-1 > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type=\"text\"]"))
        );

    }
}

class CSSHandlerExt extends CSSHandler{
    public String getFixedCssSelector(String cssSelector){
        return this.fixCssSelector(cssSelector);
    }
}
