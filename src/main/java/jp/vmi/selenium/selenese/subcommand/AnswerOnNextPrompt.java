package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Command "answerOnNextPrompt".
 */
public class AnswerOnNextPrompt extends AbstractSubCommand<Void> {

    /**
     * Constructor.
     */
    public AnswerOnNextPrompt() {
        super(ArgumentType.VALUE);
    }

    @Override
    public Void execute(Context context, String... args) {
        context.getDialogOverride().answerOnNextPrompt(context.getWrappedDriver(), args[0]);
        return null;
    }
}
