package jp.vmi.selenium.selenese.result;

public class Warning extends Result {

    public Warning(String message) {
        super(false, message);
    }

    @Override
    public boolean isSuccess() {
        return false;
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
