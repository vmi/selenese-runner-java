package jp.vmi.selenium.selenese.cmdproc;

import jp.vmi.selenium.selenese.Context;

/**
 * "getSpeed".
 */
public class GetSpeed extends WDCommand {

    /**
     * Constructor.
     */
    public GetSpeed() {
        super(null, "getSpeed");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T execute(Context context, String... args) {
        return (T) Long.valueOf(context.getSpeed());
    }
}
