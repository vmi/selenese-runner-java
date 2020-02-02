package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.Wait;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "selectFrame".
 */
public class SelectFrame extends AbstractCommand {

    private static final Logger log = LoggerFactory.getLogger(SelectFrame.class);

    private static final int ARG_LOCATOR = 0;

    private static long RETRY_INTERVAL = 100L; // ms
    private static long MILLI_TO_NANO = 1000L * 1000L;

    SelectFrame(int index, String name, String... args) {
        super(index, name, args, LOCATOR);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        WebDriver driver = context.getWrappedDriver();
        WebDriverElementFinder elementFinder = context.getElementFinder();
        long timeout = context.getTimeout() * MILLI_TO_NANO; // ns
        long start = System.nanoTime();
        while (true) {
            try {
                elementFinder.selectFrame(driver, locator);
                return SUCCESS;
            } catch (NoSuchFrameException e) {
                if (System.nanoTime() - start > timeout) {
                    log.warn("Timed out select frame: {}", locator);
                    throw e;
                }
            }
            Wait.sleep(RETRY_INTERVAL);
        }
    }
}
