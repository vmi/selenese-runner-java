package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.utils.JsonUtils;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "storeJson".
 */
public class StoreJson extends AbstractCommand {

    private static final int ARG_JSON = 0;
    private static final int ARG_VAR_NAME = 1;

    StoreJson(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String jsonStr = curArgs[ARG_JSON];
        Object json = JsonUtils.parse(jsonStr);
        String varName = curArgs[ARG_VAR_NAME];
        context.getVarsMap().put(varName, json);
        return new Success("JSON: " + json);
    }
}
