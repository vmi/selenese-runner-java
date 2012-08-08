package jp.vmi.selenium.webdriver;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

import static jp.vmi.selenium.webdriver.FirefoxDriverFactory.*;

/**
 * Test for {@link FirefoxDriverFactory}.
 */
public class FirefoxDriverFactoryTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * backup Systemproperty and restore.
     */
    @Rule
    public ExternalResource systemproperty = new ExternalResource() {
        String original;
        final String key = WEBDRIVER_FIREFOX_BIN;

        @Override
        protected void before() throws Throwable {
            super.before();
            original = System.getProperty(key);
            System.setProperty(key, folder.newFolder().getAbsolutePath());
        }

        @Override
        protected void after() {
            if (original == null) {
                System.clearProperty(key);
            } else {
                System.setProperty(key, original);
            }
            super.after();
        }
    };

    @Test(expected = IllegalArgumentException.class)
    public void firefoxNotFoundIn_webdriver_firefox_bin() throws IOException, IllegalArgumentException {
        //TODO 定数化
        System.setProperty(WEBDRIVER_FIREFOX_BIN, folder.newFolder().getAbsolutePath());
        new FirefoxDriverFactory().newInstance(new DriverOptions());
    }
}
