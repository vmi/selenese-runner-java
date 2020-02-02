package jp.vmi.selenium.webdriver;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import jp.vmi.selenium.testutils.WebServer;

import static jp.vmi.selenium.testutils.TestUtils.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("javadoc")
public class DriverOptionsTest {

    @Test
    @SuppressWarnings("unchecked")
    public void define() {
        DriverOptions driverOptions = new DriverOptions();
        DesiredCapabilities caps = driverOptions.getCapabilities();
        driverOptions.addDefinitions("key1=value1");
        assertThat(caps.getCapability("key1"), equalTo("value1"));
        assertThat(exceptionOf(() -> driverOptions.addDefinitions("key1:bool+=true")), is(instanceOf(IllegalArgumentException.class)));
        driverOptions.addDefinitions("key2:str=value2");
        assertThat(caps.getCapability("key2"), equalTo("value2"));
        driverOptions.addDefinitions("key3:bool=true");
        assertThat(caps.getCapability("key3"), equalTo(Boolean.TRUE));
        driverOptions.addDefinitions("key4:bool=false");
        assertThat(caps.getCapability("key4"), equalTo(Boolean.FALSE));
        driverOptions.addDefinitions("key5:int=123");
        assertThat(caps.getCapability("key5"), equalTo(Integer.valueOf(123)));
        driverOptions.addDefinitions("key1+=value1_2");
        assertThat((String[]) caps.getCapability("key1"), is(array(equalTo("value1"), equalTo("value1_2"))));
        driverOptions.addDefinitions("key2:str+=value2_2");
        assertThat((String[]) caps.getCapability("key2"), is(array(equalTo("value2"), equalTo("value2_2"))));
        driverOptions.addDefinitions("key3:bool+=false");
        assertThat((Boolean[]) caps.getCapability("key3"), is(array(equalTo(Boolean.TRUE), equalTo(Boolean.FALSE))));
        assertThat(exceptionOf(() -> driverOptions.addDefinitions("key3+=value3_3")), is(instanceOf(IllegalArgumentException.class)));
        assertThat(exceptionOf(() -> driverOptions.addDefinitions("key4+=true")), is(instanceOf(IllegalArgumentException.class)));
        assertThat(exceptionOf(() -> driverOptions.addDefinitions("key6:int=abc")), is(instanceOf(IllegalArgumentException.class)));
        assertThat(exceptionOf(() -> driverOptions.addDefinitions("key7:invalid_type=avalue7")), is(instanceOf(IllegalArgumentException.class)));
        assertThat(exceptionOf(() -> driverOptions.addDefinitions("invalid-format")), is(instanceOf(IllegalArgumentException.class)));
    }

    @Test
    public void defineBool() {
        WebDriverManager wdm = WebDriverManager.newInstance();
        wdm.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        DriverOptions driverOptions = new DriverOptions();
        driverOptions.addDefinitions("javascriptEnabled:bool=false");
        wdm.setDriverOptions(driverOptions);
        WebDriver driver = wdm.get();
        WebServer ws = new WebServer();
        ws.start();
        Exception actual = null;
        try {
            driver.get(ws.getBaseURL());
            ((JavascriptExecutor) driver).executeScript("true");
        } catch (Exception e) {
            actual = e;
        } finally {
            ws.stop();
        }
        assertThat(actual, is(instanceOf(UnsupportedOperationException.class)));
        assertThat(actual.getMessage(), equalTo("Javascript is not enabled for this HtmlUnitDriver instance"));
    }
}
