package jp.vmi.selenium.selenese.subcommand;

import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 *
 */
public class GetCssCount extends AbstractSubCommand<Number> {

    private static final int ARG_CSS_LOCATOR = 0;

    private static final String CSS_EQ = "css=";

    /**
     * Constructor.
     */
    public GetCssCount() {
        super(CSS_LOCATOR);
    }

    @Override
    public Number execute(Context context, String... args) {
        String cssLocator = args[ARG_CSS_LOCATOR];
        if (!cssLocator.startsWith(CSS_EQ))
            cssLocator = CSS_EQ + cssLocator;
        try {
            List<WebElement> elements = context.findElements(cssLocator);
            return elements.size();
        } catch (WebDriverException e) {
            if (e instanceof NoSuchElementException || e.getCause() instanceof NoSuchElementException)
                return 0;
            throw e;
        }
    }
}
