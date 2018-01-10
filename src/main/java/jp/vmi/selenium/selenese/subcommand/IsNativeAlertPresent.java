package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;

public class IsNativeAlertPresent extends AbstractSubCommand<Boolean> {
    @Override
    public Boolean execute(Context context, String... args) {
        WebDriver driver = context.getWrappedDriver();
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {

        }
        return false;
    }
}
