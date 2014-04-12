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
        File executable;
        if (driverOptions.has(CHROMEDRIVER)) {
            executable = new File(driverOptions.get(CHROMEDRIVER));
            if (!executable.canExecute())
                throw new IllegalArgumentException("Missing ChromeDriver: " + executable);
        } else {
            executable = PathUtils.searchExecutableFile("chromedriver");
            if (executable == null)
                throw new IllegalStateException("Missing ChromeDriver in PATH");
        }
        ChromeDriverService service = new ChromeDriverService.Builder()
            .usingDriverExecutable(executable)
            .usingAnyFreePort()
            .withEnvironment(getEnvironmentVariables())
            .build();
        DesiredCapabilities caps = DesiredCapabilities.chrome();
        ChromeOptions options = new ChromeOptions();
        if (driverOptions.has(PROXY))
            options.addArguments("--proxy-server=http://" + driverOptions.get(PROXY));
        caps.setCapability(ChromeOptions.CAPABILITY, options);
        caps.merge(driverOptions.getCapabilities());
        ChromeDriver driver = new ChromeDriver(service, caps);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }
}
