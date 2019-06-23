package jp.vmi.selenium.selenese.command;

import java.util.Set;

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.runner.model.side.SideCommand;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.utils.Wait;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "click".
 */
public class Click extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;

    private static final long RETRY_INTERVAL = 100; // ms

    Click(int index, String name, String... args) {
        super(index, name, args, LOCATOR);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        return ClickHandler.handleClick(context, locator, element -> {
            SideCommand sideCommand = getSideCommand();
            WebDriver driver;
            Set<String> prevWinHandles;
            if (sideCommand != null && sideCommand.isOpensWindow()) {
                driver = context.getWrappedDriver();
                prevWinHandles = driver.getWindowHandles();
            } else {
                driver = null;
                prevWinHandles = null;
            }
            Result result;
            try {
                element.click();
                result = SUCCESS;
            } catch (ElementNotInteractableException e) {
                context.executeScript("arguments[0].click()", element);
                result = new Success("Success (the element is not visible)");
            }
            if (prevWinHandles != null) {
                long windowTimeout = sideCommand.getWindowTimeout() * 1000L * 1000L; // ns
                long start = System.nanoTime();
                loop: do {
                    Set<String> curWinHandles = driver.getWindowHandles();
                    for (String h : curWinHandles) {
                        if (!prevWinHandles.contains(h)) {
                            context.getVarsMap().put(sideCommand.getWindowHandleName(), h);
                            break loop;
                        }
                    }
                    Wait.sleep(RETRY_INTERVAL);
                } while (System.nanoTime() - start <= windowTimeout);
            }
            return result;
        });
    }
}
