package jp.vmi.selenium.selenese;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.junit.Assume;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeDriverService;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

public class CommandRunnerChromeTest extends CommandRunnerTest {

    protected DriverOptions driverOptions = new DriverOptions();

    @Override
    protected void setupWebDriverManager() {
        File exe;
        switch (Platform.getCurrent()) {
        case WINDOWS:
        case VISTA:
        case XP:
            exe = new File("tmp/chromedriver.exe");
            break;
        default:
            exe = new File("tmp/chromedriver");
            break;
        }
        if (exe.canExecute()) {
            CommandLine cli;
            try {
                cli = new Main().parseCommandLine(new String[] { "--chromedriver", exe.getCanonicalPath() });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            driverOptions.set(DriverOption.CHROMEDRIVER, cli.getOptionValue("chromedriver"));
        } else {
            Assume.assumeNotNull(System.getProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY));
        }
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.CHROME);
        manager.setDriverOptions(driverOptions);
    }
}
