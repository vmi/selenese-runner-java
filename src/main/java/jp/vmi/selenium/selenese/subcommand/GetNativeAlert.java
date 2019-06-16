package jp.vmi.selenium.selenese.subcommand;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.Context;

/**
 * "getNativeAlert".
 */
public class GetNativeAlert extends AbstractSubCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(GetNativeAlert.class);

    @Override
    public String execute(Context context, String... args) {
        try {
            Alert alert = context.getWrappedDriver().switchTo().alert();
            String result = alert.getText();
            context.getNextNativeAlertActionListener().actionPerformed(alert);
            return result;
        } catch (NoAlertPresentException e) {
            log.warn("No alert dialog present.");
            return "";
        }
    }
}
