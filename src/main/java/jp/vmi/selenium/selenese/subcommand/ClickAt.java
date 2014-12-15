package jp.vmi.selenium.selenese.subcommand;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import com.thoughtworks.selenium.webdriven.ElementFinder;
import com.thoughtworks.selenium.webdriven.commands.AlertOverride;

/**
 * Command "clickAt" which allow no coordinates parameter.
 */
public class ClickAt extends com.thoughtworks.selenium.webdriven.commands.ClickAt {

    /**
     * Constructor.
     *
     * @param alertOverride alert override.
     * @param elementFinder element finder.
     */
    public ClickAt(AlertOverride alertOverride, ElementFinder elementFinder) {
        super(alertOverride, elementFinder);
    }

    @Override
    protected Void handleSeleneseCommand(WebDriver driver, String locator, String value) {
        if (StringUtils.isEmpty(value))
            value = "0,0";
        return super.handleSeleneseCommand(driver, locator, value);
    }
}
