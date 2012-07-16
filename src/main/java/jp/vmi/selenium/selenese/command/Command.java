package jp.vmi.selenium.selenese.command;

import org.apache.commons.lang.StringUtils;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.utils.LoggerUtils;

// https://github.com/davehunt/selenium-ide-flowcontrol
public class Command {

    public static class Result {

        private final boolean isSuccess;
        private final String message;

        public Result(boolean isSuccess, String message) {
            this.isSuccess = isSuccess;
            this.message = message;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "[" + message + "]";
        }
    }

    public static final Result SUCCESS = new Result(true, "OK");
    public static final Result FAILURE = new Result(false, "NG");

    private final int index;
    protected final String name;
    protected final String[] args;
    protected Command next = null;

    Command(int index, String name, String... args) {
        this.index = index;
        this.name = name;
        this.args = args;
    }

    /**
     * Seleneseコマンドを実行する
     *
     * @param context
     * @return true: コマンド実行成功, false: コマンド実行失敗(verify系コマンドで検証に失敗したときなど)
     */
    public Result doCommand(Context context) {
        return SUCCESS;
    }

    public Command setNext(Command next) {
        return this.next = next;
    }

    public Command next(Context context) {
        return next;
    }

    @Override
    public String toString() {
        return "Command#" + index + ": " + name + "(" + StringUtils.join(LoggerUtils.quote(args), ", ") + ")";
    }

    public int getIndex() {
        return index;
    }
}
