package jp.vmi.selenium.webdriver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.common.io.Files;
import com.google.gson.Gson;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Factory of {@link ChromeDriver}.
 *
 * @see <a href="http://code.google.com/p/chromedriver/">chromedriver - WebDriver for Google Chrome</a>
 */
public class ChromeDriverFactory extends WebDriverFactory {

    @SuppressWarnings("javadoc")
    public static final String BROWSER_NAME = "chrome";

    @Override
    public String getBrowserName() {
        return BROWSER_NAME;
    }

    /**
     * Create ChromeOptions.
     *
     * @param driverOptions driver options.
     * @return ChromeOptions
     */
    public static ChromeOptions newChromeOptions(DriverOptions driverOptions) {
        ChromeOptions options = new ChromeOptions();
        if (driverOptions.has(HEADLESS))
            options.setHeadless(driverOptions.getBoolean(HEADLESS));
        Proxy proxy = newProxy(driverOptions);
        if (proxy != null)
            options.setProxy(proxy);
        if (driverOptions.has(CLI_ARGS))
            options.addArguments(driverOptions.getCliArgs());
        if (driverOptions.has(CHROME_EXTENSION))
            options.addExtensions(driverOptions.getChromeExtensions());
        String experimentalOptions = driverOptions.get(CHROME_EXPERIMENTAL_OPTIONS);
        if (experimentalOptions != null) {
            String json = "{}";
            try {
                json = Files.asCharSource(new File(experimentalOptions), Charset.defaultCharset()).read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> jsonObject = new Gson().fromJson(json, Map.class);
            for (Entry<String, Object> entry : jsonObject.entrySet())
                options.setExperimentalOption(entry.getKey(), entry.getValue());
        }
        return options;
    }

    /**
     * set driver specific capabilities.
     *
     * @param caps desired capabilities.
     * @param driverOptions driver options.
     */
    public static void setDriverSpecificCapabilities(DesiredCapabilities caps, DriverOptions driverOptions) {
        ChromeOptions options = newChromeOptions(driverOptions);
        caps.setCapability(ChromeOptions.CAPABILITY, options); // TODO need test it works.
        caps.merge(driverOptions.getCapabilities());
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        ChromeDriverService service = setupBuilder(new ChromeDriverService.Builder(), driverOptions, CHROMEDRIVER).build();
        ChromeOptions options = newChromeOptions(driverOptions);
        options.merge(driverOptions.getCapabilities());
        ChromeDriver driver = new ChromeDriver(service, options);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }
}
