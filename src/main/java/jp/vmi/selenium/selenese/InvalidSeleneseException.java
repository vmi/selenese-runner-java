package jp.vmi.selenium.selenese;

public class InvalidSeleneseException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidSeleneseException(Exception e) {
        super(e);
    }

    public InvalidSeleneseException(String message) {
        super(message);
    }

    public InvalidSeleneseException(String string, NullPointerException e) {
        super(string, e);
    }

}
