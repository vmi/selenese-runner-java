package jp.vmi.selenium.webdriver;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;
import org.openqa.selenium.SessionNotCreatedException;

import static org.openqa.selenium.firefox.FirefoxDriver.SystemProperty.*;

/**
 * Test for {@link FirefoxDriverFactory}.
 */
public class FirefoxDriverFactoryTest {

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

        String original;

        @Override
        protected void before() throws Throwable {
            super.before();
            original = System.getProperty(BROWSER_BINARY);
            folder.create(); // why need?
            System.setProperty(BROWSER_BINARY, folder.newFolder().getAbsolutePath());
        }

        @Override
        protected void after() {
            if (original == null) {
                System.clearProperty(BROWSER_BINARY);
            } else {
                System.setProperty(BROWSER_BINARY, original);
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
    @Test(expected = IllegalStateException.class)
    public void firefoxNotFoundIn_webdriver_firefox_bin() throws IOException, IllegalArgumentException {
        try {
            new FirefoxDriverFactory().newInstance(new DriverOptions());
        } catch (SessionNotCreatedException e) {
            throw new IllegalStateException(e);
        }
    }
}
