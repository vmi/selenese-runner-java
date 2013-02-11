package jp.vmi.selenium.selenese;

import org.junit.Assume;
import org.junit.internal.AssumptionViolatedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxBinary;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.testutil.PreCondition;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * check rule to check firefox is available.
 * 
 * @author hayato
 */
public class AssumptionFirefox extends PreCondition {

    @Override
    protected void verify() {
        if (enableFirefox)
            return;

        assumeInstalledFirefox();
        assumeConnectFirefox();
        enableFirefox = true;
    }

    private static boolean enableFirefox = false;

    private static boolean noDisplay = false;

    /**
     * Check Firefox installation.
     */
    public void assumeInstalledFirefox() {
        try {
            new FirefoxBinary();
        } catch (SeleniumException e) {
            Assume.assumeNoException(e);
        } catch (WebDriverException e) {
            Assume.assumeNoException(e);
        }
    }

    /**
     * Check Firefox connected.
     */
    public void assumeConnectFirefox() {
        if (noDisplay)
            throw new AssumptionViolatedException("no display specified");

        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.FIREFOX);
        manager.setDriverOptions(new DriverOptions());
        try {
            WebDriverManager.getInstance().get();
        } catch (WebDriverException e) {
            if (e.getMessage().contains("no display specified")) {
                noDisplay = true;
                Assume.assumeNoException(e);
            }
        }
    }

}
