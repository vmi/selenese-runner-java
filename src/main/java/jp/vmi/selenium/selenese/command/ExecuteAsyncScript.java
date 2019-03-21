package jp.vmi.selenium.selenese.command;

import java.util.Map;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.utils.SeleniumUtils;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "executeScript".
 */
public class ExecuteAsyncScript extends AbstractCommand {

    private static final int ARG_SCRIPT = 0;
    private static final int ARG_VAR_NAME = 1;

    ExecuteAsyncScript(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return true;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String script = "(function(callback) {"
            + "  var promise = (function() {" + curArgs[ARG_SCRIPT] + "})();"
            + "  if (Promise.resolve(promise) === promise) {"
            + "    promise.then(res => callback({ isPromise: true, isResolved: true,  value: res }),"
            + "                 rej => callback({ isPromise: true, isResolved: false, value: rej }));"
            + "  } else {"
            + "    callback({ isPromise: false, isResolved: false, value: promise });"
            + "  }"
            + "})(arguments[0])";
        Map<String, Object> result = context.executeAsyncScript(script);
        boolean isPromise = (Boolean) result.get("isPromise");
        boolean isResolved = (Boolean) result.get("isResolved");
        Object value = result.get("value");
        String valueStr = SeleniumUtils.convertToString(value);
        if (isResolved) {
            if (curArgs.length == 2) {
                String varName = curArgs[ARG_VAR_NAME];
                if (!varName.isEmpty()) {
                    context.getVarsMap().put(varName, value);
                }
            }
            return new Success(valueStr);
        } else if (isPromise) {
            return new Failure(valueStr);
        } else { // is not promise
            return new Error("Expected async operation, instead received: " + valueStr);
        }
    }
}
