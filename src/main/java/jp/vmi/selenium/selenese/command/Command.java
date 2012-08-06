package jp.vmi.selenium.selenese.command;

import org.apache.commons.lang.StringUtils;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.utils.LoggerUtils;

// https://github.com/davehunt/selenium-ide-flowcontrol
public class Command {

    public static abstract class Result {

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

        public abstract boolean isInterrupted();

        public abstract boolean isFailed();

        public abstract int exitCode();

        public Result update(Result newResult) {
            if (newResult.isInterrupted())
                return FAILURE;
            else if (newResult.isFailed())
                return WARNING;
            else
                return this;
        }

        @Override
        public String toString() {
            return "[" + message + "]";
        }
    }

    public static class Success extends Result {
        public Success(String message) {
            super(true, message);
        }

        @Override
        public boolean isInterrupted() {
            return false;
        }

        @Override
        public boolean isFailed() {
            return false;
        }

        @Override
        public int exitCode() {
            return 0;
        }
    }

    public static class Warning extends Result {
        public Warning(String message) {
            super(false, message);
        }

        @Override
        public boolean isInterrupted() {
            return false;
        }

        @Override
        public boolean isFailed() {
            return true;
        }

        @Override
        public int exitCode() {
            return 2;
        }
    }

    public static class Failure extends Result {
        public Failure(String message) {
            super(false, message);
        }

        public Failure(Exception e) {
            super(false, e.getMessage());
        }

        @Override
        public boolean isInterrupted() {
            return true;
        }

        @Override
        public boolean isFailed() {
            return true;
        }

        @Override
        public int exitCode() {
            return 3;
        }
    }

    public static final Result SUCCESS = new Success("Success");
    public static final Result WARNING = new Warning("Warning");
    public static final Result FAILURE = new Failure("Failure");

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
