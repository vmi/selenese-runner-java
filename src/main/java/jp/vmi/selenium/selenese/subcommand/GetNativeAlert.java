package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import org.openqa.selenium.*;

public class GetNativeAlert extends AbstractSubCommand<String> {

    @Override
    public String execute(Context context, String... args) {
        Alert alert = context.getWrappedDriver().switchTo().alert();
        String result = alert.getText();
        context.getNextNativeAlertAction().perform(alert);
        return result;
    }
}
