package jp.vmi.selenium.selenese.cmdproc;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.Runner;

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
        long result = 0;
        if (context instanceof Runner)
            result = ((Runner) context).getSpeed();
        return (T) Long.valueOf(result);
    }
}
