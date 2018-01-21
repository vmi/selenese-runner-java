package jp.vmi.selenium.selenese.subcommand;

import org.openqa.selenium.Alert;

import jp.vmi.selenium.selenese.Context;

/**
 * "getNativeAlert".
 */
public class GetNativeAlert extends AbstractSubCommand<String> {

    @Override
    public String execute(Context context, String... args) {
        Alert alert = context.getWrappedDriver().switchTo().alert();
        String result = alert.getText();
        context.getNextNativeAlertActionListener().actionPerformed(alert);
        return result;
    }
}
