package jp.vmi.selenium.selenese.subcommand;

import com.thoughtworks.selenium.SeleniumException;
import com.thoughtworks.selenium.webdriven.SeleneseCommand;
import com.thoughtworks.selenium.webdriven.commands.NoOp;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * WDCP command with the information.
 */
public class WDCommand extends AbstractSubCommand<Object> {

    private final SeleneseCommand<?> seleneseCommand;
    private final String name;

    /**
     * Constructor.
     *
     * @param seleneseCommand Selenese command.
     * @param name command name.
     * @param argTypes argument types.
     */
    public WDCommand(SeleneseCommand<?> seleneseCommand, String name, ArgumentType... argTypes) {
        super(argTypes);
        this.seleneseCommand = seleneseCommand;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object execute(Context context, String... args) {
        try {
            return seleneseCommand.apply(context.getWrappedDriver(), args);
        } catch (RuntimeException e) {
            // for HtmlUnit
            if (!e.getClass().getSimpleName().contains("Script"))
                throw e;
            String message = e.getMessage().replaceFirst("\\s*\\([^()]+\\)$", "");
            throw new SeleniumException(message, e);
        }
    }

    /**
     * Test WDCP command is NoOp.
     *
     * @return true if WDCP command is NoOp.
     */
    public boolean isNoOp() {
        return seleneseCommand instanceof NoOp;
    }

    @Override
    public String toString() {
        return "WDCommand[" + name + "]";
    }
}
