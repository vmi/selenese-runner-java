package jp.vmi.selenium.webdriver;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

/**
 * Factory of {@link PhantomJSDriver}.
 */
public class PhantomJSDriverFactory extends WebDriverFactory {
    private static final Logger log = LoggerFactory.getLogger(PhantomJSDriverFactory.class);

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities caps = setupProxy(DesiredCapabilities.phantomjs(), driverOptions);
        PhantomJSDriver d = new PhantomJSDriver(caps);

        // set window size
        try {
            int width = Integer.parseInt(driverOptions.get(DriverOption.WIDTH, "1024"));
            int height = Integer.parseInt(driverOptions.get(DriverOption.HEIGHT, "768"));
            log.info("phantomjs screen size: {}x{}", width, height);
            d.manage().window().setSize(new Dimension(width, height));
        } catch (NumberFormatException e) {
            log.warn("width or height is not set by integer, execute by default value.");
            d.manage().window().setSize(new Dimension(1024, 768));
        }

        return d;
    }
}
