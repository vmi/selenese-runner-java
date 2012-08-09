package jp.vmi.selenium.selenese.result;

public abstract class Result {

    public static final Result SUCCESS = new Success("Success");

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
            return new Failure(this.getMessage() + "\n" + newResult.getMessage());
        else if (newResult.isFailed())
            return new Warning(this.getMessage() + "\n" + newResult.getMessage());
        else
            return this;
    }

    @Override
    public String toString() {
        return "[" + message + "]";
    }
}
