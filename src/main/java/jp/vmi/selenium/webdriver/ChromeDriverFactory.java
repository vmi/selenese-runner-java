package jp.vmi.selenium.webdriver;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import jp.vmi.selenium.selenese.utils.PathUtils;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Factory of {@link ChromeDriver}.
 *
 * @see <a href="http://code.google.com/p/chromedriver/">chromedriver - WebDriver for Google Chrome</a>
 */
public class ChromeDriverFactory extends WebDriverFactory {

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities caps = DesiredCapabilities.chrome();
        if (driverOptions.has(CHROMEDRIVER)) {
            String executable = PathUtils.normalize(driverOptions.get(CHROMEDRIVER));
            if (!new File(executable).canExecute())
                throw new IllegalArgumentException("Missing ChromeDriver: " + executable);
            System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, executable);
        }
        ChromeDriverService service = CustomChromeDriverService.createService(driverOptions.getEnvVars());
        ChromeOptions options = new ChromeOptions();
        if (driverOptions.has(PROXY))
            options.addArguments("--proxy-server=http://" + driverOptions.get(PROXY));
        if (driverOptions.has(CLI_ARGS))
            options.addArguments(driverOptions.getCliArgs());
        caps.setCapability(ChromeOptions.CAPABILITY, options);
        caps.merge(driverOptions.getCapabilities());
        ChromeDriver driver = new ChromeDriver(service, caps);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }
}
