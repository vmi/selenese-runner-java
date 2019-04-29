package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.command.ArgumentType;
import jp.vmi.selenium.selenese.utils.LangUtils;

/**
 * Base implementation of sub-command.
 *
 * @param <T> the result type of sub-command.
 */
public abstract class AbstractSubCommand<T> implements ISubCommand<T> {

    private final ArgumentType[] argTypes;

    /**
     * Constructor.
     *
     * @param argTypes argument types of this sub-command.
     */
    public AbstractSubCommand(ArgumentType... argTypes) {
        this.argTypes = argTypes;
    }

    @Override
    public String getName() {
        return LangUtils.uncapitalize(getClass().getSimpleName());
    }

    @Override
    public ArgumentType[] getArgumentTypes() {
        return argTypes;
    }
}
