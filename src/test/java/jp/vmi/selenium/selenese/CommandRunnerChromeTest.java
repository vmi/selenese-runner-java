package jp.vmi.selenium.selenese;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.junit.Assume;
import org.junit.Before;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeDriverService;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

public class CommandRunnerChromeTest extends CommandRunnerTest {

    private DriverOptions driverOptions = new DriverOptions();

    @Before
    public void checkChromeProperty() {
        String exe;
        switch (Platform.getCurrent()) {
        case WINDOWS:
        case VISTA:
        case XP:
            exe = "tmp/chromedriver.exe";
            break;
        default:
            exe = "tmp/chromedriver";
            break;
        }

        if (new File(exe).canExecute()) {
            CommandLine cli = new Main().parseCommandLine(new String[] { "--chromedriver", exe });
            driverOptions = new DriverOptions(cli);
        } else {
            Assume.assumeNotNull(System.getProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY));
        }
    }

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.CHROME);
        manager.setDriverOptions(driverOptions);
    }
}
