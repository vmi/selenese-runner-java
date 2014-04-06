package jp.vmi.selenium.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.thoughtworks.selenium.SeleniumException;

import io.selendroid.SelendroidCapabilities;
import io.selendroid.SelendroidDriver;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Factory of {@link HtmlUnitDriver}.
 */
public class SelendroidDriverFactory extends WebDriverFactory {

    // Usage: --selendroid AUT,andoridNN[,device|emulator]
    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        String aut = null;
        String platform = "ANDROID19";
        boolean isEmulator = true;
        if (driverOptions.has(DriverOption.SELENDROID)) {
            String[] opts = driverOptions.get(DriverOption.SELENDROID).split("\\s*,\\s*");
            aut = opts[0];
            for (int i = 1; i < opts.length; i++) {
                String opt = opts[i];
                if (startsWithIgnoreCase(opt, "android"))
                    platform = opt.toUpperCase();
                else if (equalsIgnoreCase(opt, "emulator"))
                    isEmulator = true;
                else if (equalsIgnoreCase(opt, "device"))
                    isEmulator = false;
            }
        }
        SelendroidCapabilities caps = new SelendroidCapabilities(aut);
        caps.setAndroidTarget(platform);
        caps.setEmulator(isEmulator);
        caps.merge(driverOptions.getCapabilities());
        try {
            return new SelendroidDriver(caps);
        } catch (Exception e) {
            throw new SeleniumException(e);
        }
    }
}
