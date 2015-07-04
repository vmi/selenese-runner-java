package jp.vmi.selenium.webdriver;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import jp.vmi.selenium.selenese.utils.SeleniumUtils;

/**
 * Factory of {@link HtmlUnitDriver}.
 */
public class HtmlUnitDriverFactory extends WebDriverFactory {

    /**
     * Constructor.
     */
    public HtmlUnitDriverFactory() {
        super();
        if (!SeleniumUtils.isJava7orLater())
            throw new UnsupportedOperationException("HtmlUnit requires Java 7 or later.");
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities caps = setupProxy(DesiredCapabilities.htmlUnit(), driverOptions);
        caps.setJavascriptEnabled(true);
        caps.merge(driverOptions.getCapabilities());
        HtmlUnitDriver driver = new HtmlUnitDriver(caps);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }

    @Override
    protected Dimension getDefaultWindowSize(WebDriver driver) {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
