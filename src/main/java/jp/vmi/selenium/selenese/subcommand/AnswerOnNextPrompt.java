package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;
import jp.vmi.selenium.selenese.utils.DialogOverride;

/**
 * Command "answerOnNextPrompt".
 */
public class AnswerOnNextPrompt extends AbstractSubCommand<Void> {

    private final DialogOverride dialogOverride;

    /**
     * Constructor.
     *
     * @param dialogOverride dialog override.
     */
    public AnswerOnNextPrompt(DialogOverride dialogOverride) {
        super(ArgumentType.VALUE);
        this.dialogOverride = dialogOverride;
    }

    @Override
    public Void execute(Context context, String... args) {
        dialogOverride.answerOnNextPrompt(context.getWrappedDriver(), args[0]);
        return null;
    }
}
