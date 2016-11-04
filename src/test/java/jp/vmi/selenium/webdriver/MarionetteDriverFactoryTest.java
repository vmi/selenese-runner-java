package jp.vmi.selenium.webdriver;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

import static jp.vmi.selenium.webdriver.FirefoxDriverFactory.*;

public class MarionetteDriverFactoryTest {
    /**
     * Temporary directory.
     */
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * backup and restore System property.
     */
    @Rule
    public ExternalResource systemProperty = new ExternalResource() {

        private static final String key = WEBDRIVER_FIREFOX_BIN;

        String original;

        @Override
        protected void before() throws Throwable {
            super.before();
            original = System.getProperty(key);
            folder.create(); // why need?
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

    /**
     * Test of not finding Firefox binary.
     *
     * @throws IOException exception.
     * @throws IllegalArgumentException exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void firefoxNotFoundIn_webdriver_firefox_bin() throws IOException, IllegalArgumentException {
        System.setProperty(WEBDRIVER_FIREFOX_BIN, folder.newFolder().getAbsolutePath());
        new MarionetteDriverFactory().newInstance(new DriverOptions());
    }
}
