package jp.vmi.selenium.selenese.subcommand;

import org.apache.commons.lang3.StringUtils;

import jp.vmi.selenium.selenese.command.ArgumentType;

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
        return StringUtils.uncapitalize(getClass().getSimpleName());
    }

    @Override
    public ArgumentType[] getArgumentTypes() {
        return argTypes;
    }
}
