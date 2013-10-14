package jp.vmi.selenium.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Factory of {@link HtmlUnitDriver}.
 */
public class HtmlUnitDriverFactory extends WebDriverFactory {

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities caps = setupProxy(DesiredCapabilities.htmlUnit(), driverOptions);
        caps.setJavascriptEnabled(true);
        return new HtmlUnitDriver(caps);
    }
}
