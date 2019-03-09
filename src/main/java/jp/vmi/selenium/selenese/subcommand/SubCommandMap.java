package jp.vmi.selenium.selenese.subcommand;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jp.vmi.selenium.selenese.subcommand.MouseEventHandler.MouseEventType;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Replacement of WebDriverCommandProcessor for extention.
 */
public class SubCommandMap {

    private final Map<String, ISubCommand<?>> subCommands = new HashMap<>();

    /**
     * Constructor.
     */
    public SubCommandMap() {
        register(new GetAlert());
        register(new GetAllButtons());
        register(new GetAllFields());
        register(new GetAllLinks());
        register(new GetAllWindowNames());
        register(new GetAllWindowTitles());
        register(new GetAttribute());
        register(new GetAttributeFromAllWindows());
        register(new GetBodyText());
        register(new GetConfirmation());
        register(new GetCookie());
        register(new GetCookieByName());
        register(new GetCssCount());
        register(new GetCursorPosition());
        register(new GetElementHeight());
        register(new GetElementIndex());
        register(new GetElementPositionLeft());
        register(new GetElementPositionTop());
        register(new GetElementWidth());
        register(new GetEval());
        register(new GetExpression());
        register(new GetHtmlSource());
        register(new GetLocation());
        register(new GetNativeAlert());
        register(new GetPrompt());
        register(new GetSelectOptions());
        register(new GetSpeed());
        register(new GetTable());
        register(new GetText());
        register(new GetTitle());
        register(new GetValue());
        register(new GetWindowHandle());
        register(new GetXpathCount());
        register(new IsAlertPresent());
        register(new IsChecked());
        register(new IsConfirmationPresent());
        register(new IsCookiePresent());
        IsEditable isEditable = new IsEditable();
        register(isEditable);
        register(isEditable, "isElementEditable");
        register(new IsElementPresent());
        register(new IsNativeAlertPresent());
        register(new IsOrdered());
        register(new IsPromptPresent());
        register(new IsSomethingSelected());
        register(new IsTextPresent());
        IsVisible isVisible = new IsVisible();
        register(isVisible);
        register(isVisible, "isElementVisible");

        for (MouseEventType type : MouseEventType.values())
            register(new MouseEventHandler(type));

        register(new GetSelected(GetSelected.Type.LABEL, false));
        register(new GetSelected(GetSelected.Type.LABEL, true));
        register(new GetSelected(GetSelected.Type.VALUE, false));
        register(new GetSelected(GetSelected.Type.VALUE, true));
        register(new GetSelected(GetSelected.Type.INDEX, false));
        register(new GetSelected(GetSelected.Type.INDEX, true));
        register(new GetSelected(GetSelected.Type.ID, false));
        register(new GetSelected(GetSelected.Type.ID, true));

        register(new NoOp("setBrowserLogLevel", VALUE));
        register(new NoOp("waitForFrameToLoad", VALUE, VALUE));
    }

    /**
     * Register sub-command.
     *
     * @param subCommand ISubCommand object.
     */
    public void register(ISubCommand<?> subCommand) {
        register(subCommand, subCommand.getName());
    }

    /**
     * Register sub-command.
     *
     * @param subCommand ISubCommand object.
     * @param name command name.
     */
    public void register(ISubCommand<?> subCommand, String name) {
        subCommands.put(name, subCommand);
    }

    /**
     * Get sub-command.
     *
     * @param commandName sub-command name.
     * @return sub-command, or null if sub-command does not exist.
     */
    public ISubCommand<?> get(String commandName) {
        return subCommands.get(commandName);
    }

    /**
     * Get read-only sub-command map.
     * @return map.
     */
    public Map<String, ISubCommand<?>> getMap() {
        return Collections.unmodifiableMap(subCommands);
    }
}
