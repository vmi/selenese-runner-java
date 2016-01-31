package jp.vmi.selenium.webdriver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.common.io.Files;
import com.google.gson.Gson;

import jp.vmi.selenium.selenese.utils.PathUtils;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Factory of {@link ChromeDriver}.
 *
 * @see <a href="http://code.google.com/p/chromedriver/">chromedriver - WebDriver for Google Chrome</a>
 */
public class ChromeDriverFactory extends WebDriverFactory {

    /**
     * set driver specific capabilities.
     *
     * @param caps desired capabilities.
     * @param driverOptions driver options.
     */
    public static void setDriverSpecificCapabilities(DesiredCapabilities caps, DriverOptions driverOptions) {
        ChromeOptions options = new ChromeOptions();
        if (driverOptions.has(PROXY))
            options.addArguments("--proxy-server=http://" + driverOptions.get(PROXY));
        if (driverOptions.has(CLI_ARGS))
            options.addArguments(driverOptions.getCliArgs());
        if (driverOptions.has(CHROME_EXTENSION))
            options.addExtensions(driverOptions.getChromeExtensions());
        String experimentalOptions = driverOptions.get(CHROME_EXPERIMENTAL_OPTIONS);
        if (experimentalOptions != null) {
            String json = "{}";
            try {
                json = Files.toString(new File(experimentalOptions), Charset.defaultCharset());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> jsonObject = new Gson().fromJson(json, Map.class);
            for (Entry<String, Object> entry : jsonObject.entrySet())
                options.setExperimentalOption(entry.getKey(), entry.getValue());
        }
        caps.setCapability(ChromeOptions.CAPABILITY, options);
        caps.merge(driverOptions.getCapabilities());
    }

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
        setDriverSpecificCapabilities(caps, driverOptions);
        ChromeDriver driver = new ChromeDriver(service, caps);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }
}
