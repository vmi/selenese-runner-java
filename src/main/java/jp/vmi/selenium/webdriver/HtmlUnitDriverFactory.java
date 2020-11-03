package jp.vmi.selenium.webdriver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Factory of HtmlUnitDriver.
 */
public class HtmlUnitDriverFactory extends WebDriverFactory {

    @SuppressWarnings("javadoc")
    public static final String BROWSER_NAME = "htmlunit";

    private static final String HTML_UNIT_DRIVER = "org.openqa.selenium.htmlunit.HtmlUnitDriver";
    private static final String HTML_UNIT_CONSOLE = "jp.vmi.selenium.webdriver.HtmlUnitConsole";

    /**
     * Constructor.
     */
    public HtmlUnitDriverFactory() {
        super();
    }

    @Override
    public String getBrowserName() {
        return BROWSER_NAME;
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities caps = setupProxy(DesiredCapabilities.htmlUnit(), driverOptions);
        caps.setJavascriptEnabled(true);
        caps.merge(driverOptions.getCapabilities());
        try {
            WebDriver driver = (WebDriver) Class.forName(HTML_UNIT_DRIVER).getConstructor(Capabilities.class).newInstance(caps);
            // HtmlUnitDriver driver = new HtmlUnitDriver(caps);
            Class.forName(HTML_UNIT_CONSOLE).getMethod("setHtmlUnitConsole", WebDriver.class).invoke(null, driver);
            setInitialWindowSize(driver, driverOptions);
            return driver;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Dimension getDefaultWindowSize(WebDriver driver) {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
