package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;

/**
 * Re-implementation of GetLocation.
 */
public class GetLocation extends AbstractSubCommand<String> {

    /**
     * Constructor.
     */
    public GetLocation() {
        super();
    }

    @Override
    public String execute(Context context, String... args) {
        return context.getWrappedDriver().getCurrentUrl();
    }
}
