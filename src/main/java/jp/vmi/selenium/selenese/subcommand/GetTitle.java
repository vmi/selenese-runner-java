package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;

/**
 * Re-implementation of GetTitle.
 */
public class GetTitle extends AbstractSubCommand<String> {

    /**
     * Constructor.
     */
    public GetTitle() {
        super();
    }

    @Override
    public String execute(Context context, String... args) {
        return context.getWrappedDriver().getTitle();
    }
}
