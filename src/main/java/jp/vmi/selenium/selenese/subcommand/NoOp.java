package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * No operation.
 */
public class NoOp extends AbstractSubCommand<Void> {

    private final String name;

    /**
     * Constructor.
     *
     * @param name sub-command name.
     * @param argTypes argument types of this sub-command.
     */
    public NoOp(String name, ArgumentType... argTypes) {
        super(argTypes);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Void execute(Context context, String... curArgs) {
        return null;
    }
}
