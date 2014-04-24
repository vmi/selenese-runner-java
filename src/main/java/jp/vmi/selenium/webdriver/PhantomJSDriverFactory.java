package jp.vmi.selenium.webdriver;

import java.io.File;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.CustomPhantomJSDriverServiceFactory;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Factory of {@link PhantomJSDriver}.
 */
public class PhantomJSDriverFactory extends WebDriverFactory {

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities caps = setupProxy(DesiredCapabilities.phantomjs(), driverOptions);
        if (driverOptions.has(PHANTOMJS)) {
            File binary = new File(driverOptions.get(PHANTOMJS));
            if (!binary.canExecute())
                throw new IllegalArgumentException("Missing PhantomJS binary: " + binary);
            caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, binary.getPath());
        }
        caps.merge(driverOptions.getCapabilities());
        PhantomJSDriverService service = CustomPhantomJSDriverServiceFactory.createDefaultService(caps);
        PhantomJSDriver driver = new PhantomJSDriver(service, caps);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }

    @Override
    protected Dimension getDefaultWindowSize(WebDriver driver) {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
