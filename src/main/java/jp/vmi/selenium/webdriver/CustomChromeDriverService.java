package jp.vmi.selenium.webdriver;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeDriverService;

/**
 * Customized ChromeDriverService.
 */
public class CustomChromeDriverService extends ChromeDriverService {

    // Don't use this.
    private CustomChromeDriverService() throws IOException {
        super(null, 0, null, null);
        throw new UnsupportedOperationException("Don't use this.");
    }

    /**
     * Configures and returns a new {@link ChromeDriverService}.
     *
     * @param environment environment variables.
     * @return A new ChromeDriverService.
     */
    public static ChromeDriverService createService(Map<String, String> environment) {
        File exe = findExecutable("chromedriver", CHROME_DRIVER_EXE_PROPERTY,
            "http://code.google.com/p/selenium/wiki/ChromeDriver",
            "http://chromedriver.storage.googleapis.com/index.html");
        return new Builder().usingDriverExecutable(exe).usingAnyFreePort().withEnvironment(environment).build();
    }
}
