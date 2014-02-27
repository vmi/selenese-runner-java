package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;

/**
 * "getSpeed".
 */
public class GetSpeed extends AbstractSubCommand<Number> {

    /**
     * Constructor.
     */
    public GetSpeed() {
        super();
    }

    @Override
    public Number execute(Context context, String... args) {
        return context.getSpeed();
    }
}
