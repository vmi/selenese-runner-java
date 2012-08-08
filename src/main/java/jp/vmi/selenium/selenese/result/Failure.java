package jp.vmi.selenium.selenese.result;

public class Failure extends Result {

    public Failure(String message) {
        super(false, message);
    }

    public Failure(Exception e) {
        super(false, e.getMessage());
    }

    @Override
    public boolean isSuccess() {
        return false;
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
