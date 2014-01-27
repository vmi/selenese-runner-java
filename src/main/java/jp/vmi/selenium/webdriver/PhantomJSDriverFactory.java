package jp.vmi.selenium.webdriver;

import java.io.File;

import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Factory of {@link PhantomJSDriver}.
 */
public class PhantomJSDriverFactory extends WebDriverFactory {

    private static final Logger log = LoggerFactory.getLogger(PhantomJSDriverFactory.class);

    private static final int DEFAULT_WIDTH = 1024;
    private static final int DEFAULT_HEIGHT = 768;

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities caps = setupProxy(DesiredCapabilities.phantomjs(), driverOptions);
        if (driverOptions.has(PHANTOMJS)) {
            File binary = new File(driverOptions.get(PHANTOMJS));
            if (!binary.canExecute())
                throw new IllegalArgumentException("Missing PhantomJS binary: " + binary);
            caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, binary.getPath());
        }
        PhantomJSDriver driver = new PhantomJSDriver(driverOptions.addCapabilityDefinitions(caps));
        int width = NumberUtils.toInt(driverOptions.get(DriverOption.WIDTH), DEFAULT_WIDTH);
        int height = NumberUtils.toInt(driverOptions.get(DriverOption.HEIGHT), DEFAULT_HEIGHT);
        driver.manage().window().setSize(new Dimension(width, height));
        log.info("phantomjs screen size: {}x{}", width, height);
        return driver;
    }
}
