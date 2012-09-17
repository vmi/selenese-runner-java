package jp.vmi.selenium.webdriver;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Factory of {@link ChromeDriver}.
 *
 * @see <a href="http://code.google.com/p/chromedriver/">chromedriver - WebDriver for Google Chrome</a>
 */
public class ChromeDriverFactory extends WebDriverFactory {

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        ChromeDriverService.Builder builder = new ChromeDriverService.Builder();
        if (driverOptions.has(CHROMEDRIVER)) {
            builder = builder.usingDriverExecutable(new File(driverOptions.get(CHROMEDRIVER)));
            //        } else if (StringUtils.isBlank(System.getProperty(CHROME_DRIVER_EXE_PROPERTY))) {
            //            String[] classPaths = System.getProperty("java.class.path").split(Pattern.quote(File.pathSeparator));
            //            for (String classPath : classPaths) {
            //                File dir = new File(classPath);
            //                if (dir.isFile())
            //                    dir = dir.getAbsoluteFile().getParentFile();
            //                File file = new File(dir, "");
            //                if (file.isFile() && file.canExecute()) {
            //                    path = file.getPath();
            //                    break;
            //                }
            //            }
        }
        builder = builder.usingAnyFreePort().withEnvironment(getEnvironmentVariables());
        ChromeDriverService service = builder.build();

        // new ChromeDriver(Capabilities) is deprecated...
        ChromeOptions options = new ChromeOptions();
        if (driverOptions.has(PROXY))
            options.addArguments("--proxy-server=http://" + driverOptions.get(PROXY));
        return new ChromeDriver(service, options);
    }
}
