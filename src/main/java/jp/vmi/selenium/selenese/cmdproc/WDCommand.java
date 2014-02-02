package jp.vmi.selenium.selenese.cmdproc;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.Context;

import static jp.vmi.selenium.selenese.cmdproc.ArgType.*;

/**
 * WDCP command with the information.
 */
@SuppressWarnings("javadoc")
public class WDCommand {

    private final SeleneseCommand<?> seleneseCommand;

    public final String name;
    public final int argumentCount;
    public final int[] locatorIndexes;

    public WDCommand(SeleneseCommand<?> seleneseCommand, String name, ArgType... argTypes) {
        this.seleneseCommand = seleneseCommand;
        this.name = name;
        this.argumentCount = argTypes.length;
        int count = 0;
        for (ArgType argType : argTypes)
            if (argType == LOCATOR || argType == CSS_LOCATOR)
                count++;
        locatorIndexes = new int[count];
        for (int n = 0, i = 0; i < argTypes.length; i++)
            if (argTypes[i] == LOCATOR || argTypes[i] == CSS_LOCATOR)
                locatorIndexes[n++] = i;
    }

    /**
     * Execute command.
     *
     * @param context Selenese Runner context.
     * @param args arguments.
     * @return command result.
     */
    public <T> T execute(Context context, String... args) {
        try {
            @SuppressWarnings("unchecked")
            T result = (T) seleneseCommand.apply(context.getWrappedDriver(), context.getVarsMap().replaceVarsForArray(args));
            return result;
        } catch (RuntimeException e) {
            // for HtmlUnit
            if (!e.getClass().getSimpleName().contains("Script"))
                throw e;
            String message = e.getMessage().replaceFirst("\\s*\\([^()]+\\)$", "");
            throw new SeleniumException(message, e);
        }
    }

    /**
     * Convert to String from the result of execute().
     *
     * @param result the result of execute().
     * @return converted string.
     */
    public <T> String convertToString(T result) {
        if (result == null)
            return "";
        else if (result instanceof Object[])
            return StringUtils.join((Object[]) result, ',');
        else if (result instanceof Iterable)
            return StringUtils.join((Iterable<?>) result, ',');
        else if (result instanceof Iterator)
            return StringUtils.join((Iterator<?>) result, ',');
        else
            return result.toString();
    }
}
