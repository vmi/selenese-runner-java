package jp.vmi.selenium.webdriver;

import java.lang.reflect.Field;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebConsole;

/**
 * Log handler for HtmlUnitDriver.
 */
public class HtmlUnitConsole implements com.gargoylesoftware.htmlunit.WebConsole.Logger {

    private static final Logger log = LoggerFactory.getLogger(HtmlUnitConsole.class);

    private static final HtmlUnitConsole INSTANCE = new HtmlUnitConsole();

    private HtmlUnitConsole() {
        // no operation.
    }

    /**
     * Set console handler for HtmlUnitDriver.
     *
     * @param driver HtmlUnitDriver.
     */
    public static void setHtmlUnitConsole(WebDriver driver) {
        try {
            Field f = driver.getClass().getDeclaredField("webClient");
            f.setAccessible(true);
            WebClient webClient = (WebClient) f.get(driver);
            WebConsole webConsole = webClient.getWebConsole();
            com.gargoylesoftware.htmlunit.WebConsole.Logger logger = webConsole.getLogger();
            if (logger != null && logger instanceof HtmlUnitConsole)
                return;
            webConsole.setLogger(INSTANCE);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            log.warn("Cannot set htmlunit console.", e);
            return;
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public void trace(Object message) {
        log.trace("### {}", message);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public void debug(Object message) {
        log.debug("### {}", message);
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void info(Object message) {
        log.info("### {}", message);
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public void warn(Object message) {
        log.warn("### {}", message);
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public void error(Object message) {
        log.error("### {}", message);
    }
}
