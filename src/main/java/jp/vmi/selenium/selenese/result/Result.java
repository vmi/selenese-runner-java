package jp.vmi.selenium.selenese.result;

public abstract class Result {

    public static final Result SUCCESS = new Success("Success");
    public static final Result WARNING = new Warning("Warning");
    public static final Result FAILURE = new Failure("Failure");

    private final String message;

    public Result(boolean isSuccess, String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public abstract boolean isSuccess();

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
