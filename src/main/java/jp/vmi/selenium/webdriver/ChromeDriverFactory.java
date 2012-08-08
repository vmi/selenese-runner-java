package jp.vmi.selenium.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;
import static org.openqa.selenium.chrome.ChromeDriverService.*;

/**
 * Factory of {@link ChromeDriver}.
 *
 * @see <a href="http://code.google.com/p/chromedriver/">chromedriver - WebDriver for Google Chrome</a>
 */
public class ChromeDriverFactory extends WebDriverFactory {

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        String path = null;
        if (driverOptions.has(CHROMEDRIVER)) {
            path = driverOptions.get(CHROMEDRIVER);
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
        if (path != null)
            System.setProperty(CHROME_DRIVER_EXE_PROPERTY, path);
        // new ChromeDriver(Capabilities) is deprecated...
        ChromeOptions options = new ChromeOptions();
        if (driverOptions.has(PROXY))
            options.addArguments("--proxy-server=http://" + driverOptions.get(PROXY));
        return new ChromeDriver(options);
    }
}
