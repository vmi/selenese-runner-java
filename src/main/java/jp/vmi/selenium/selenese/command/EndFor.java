package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "endFor".
 */
public class EndFor extends AbstractCommand implements EndLoop {

    private StoreFor startLoop;

    EndFor(int index, String name, String... args) {
        super(index, name, args);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    public void setStartLoop(StartLoop startLoop) {
        this.startLoop = (StoreFor) startLoop;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.getCommandListIterator().jumpTo(startLoop);
        return SUCCESS;
    }
}
