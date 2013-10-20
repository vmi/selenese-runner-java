package jp.vmi.selenium.testutils;

import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.webdriver.WebDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.junit.Assume.*;

/**
 * Test base with multiple driver.
 */
@RunWith(Parameterized.class)
@SuppressWarnings("javadoc")
public class TestBaseWithMultiDriver extends TestBase {

    @Parameters(name = "{index}: {0}")
    public static List<Object[]> getWebDriverFactories() {
        return TestUtils.getWebDriverFactories();
    }

    @Parameter
    public WebDriverFactory factory;

    public Runner runner;

    @Before
    public void initRunner() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(factory);
        WebDriver driver;
        try {
            driver = manager.get();
        } catch (IllegalStateException e) {
            assumeNoException("Initialization failed.", e);
            return;
        } catch (UnsupportedOperationException e) {
            assumeNoException("Unsupported platform.", e);
            return;
        }
        runner = new Runner();
        runner.setDriver(driver);
        runner.setBaseURL(wsr.getBaseURL());
    }
}
