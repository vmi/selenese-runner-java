package jp.vmi.selenium.selenese.result;

public class Success extends Result {

    public Success(String message) {
        super(true, message);
    }

    @Override
    public boolean isSuccess() {
        return true;
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
