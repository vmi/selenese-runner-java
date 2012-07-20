package jp.vmi.selenium.selenese;

public class InvalidOptionException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidOptionException(String string, Exception e) {
        super(string, e);
    }

    public InvalidOptionException(String string) {
        super(string);
    }

    public InvalidOptionException() {
        super();
    }

}
