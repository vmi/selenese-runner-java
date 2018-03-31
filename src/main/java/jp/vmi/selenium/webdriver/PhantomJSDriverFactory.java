package jp.vmi.selenium.webdriver;

import java.io.File;

import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.CustomPhantomJSDriverServiceFactory;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;
import static org.openqa.selenium.phantomjs.PhantomJSDriverService.*;

/**
 * Factory of {@link PhantomJSDriver}.
 *
 * @deprecated PhantomJS is no longer actively developed, and support will eventually be dropped.
 */
@Deprecated
public class PhantomJSDriverFactory extends WebDriverFactory {

    @SuppressWarnings("javadoc")
    public static final String BROWSER_NAME = "phantomjs";

    @Override
    public String getBrowserName() {
        return BROWSER_NAME;
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities caps = setupProxy(DesiredCapabilities.phantomjs(), driverOptions);
        if (driverOptions.has(PHANTOMJS)) {
            File binary = new File(driverOptions.get(PHANTOMJS));
            if (!binary.canExecute())
                throw new IllegalArgumentException("Missing PhantomJS binary: " + binary);
            caps.setCapability(PHANTOMJS_EXECUTABLE_PATH_PROPERTY, binary.getPath());
        }
        caps.merge(driverOptions.getCapabilities());
        if (driverOptions.has(CLI_ARGS)) {
            Object cliArgs = caps.getCapability(PHANTOMJS_CLI_ARGS);
            if (cliArgs == null) {
                cliArgs = ArrayUtils.EMPTY_STRING_ARRAY;
            } else {
                if (cliArgs instanceof String)
                    cliArgs = new String[] { (String) cliArgs };
                else if (!(cliArgs instanceof String[]))
                    throw new IllegalArgumentException("Invalid " + PHANTOMJS_CLI_ARGS + ": " + cliArgs);
            }
            cliArgs = ArrayUtils.addAll((String[]) cliArgs, driverOptions.getCliArgs());
            caps.setCapability(PHANTOMJS_CLI_ARGS, cliArgs);
        }
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
