package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;

/**
 * Re-implementation of GetHtmlSource.
 */
public class GetHtmlSource extends AbstractSubCommand<String> {

    /**
     * Constructor.
     */
    public GetHtmlSource() {
        super();
    }

    @Override
    public String execute(Context context, String... args) {
        return context.getWrappedDriver().getPageSource();
    }
}
