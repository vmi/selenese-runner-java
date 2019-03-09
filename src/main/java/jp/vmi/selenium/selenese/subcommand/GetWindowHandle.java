package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;

/**
 * Get window handle.
 */
public class GetWindowHandle extends AbstractSubCommand<String> {

    /**
     * Constructor.
     */
    public GetWindowHandle() {
        super();
    }

    @Override
    public String execute(Context context, String... args) {
        return context.getWrappedDriver().getWindowHandle();
    }
}
