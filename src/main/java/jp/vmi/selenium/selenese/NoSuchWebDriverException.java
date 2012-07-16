package jp.vmi.selenium.selenese;

public class NoSuchWebDriverException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoSuchWebDriverException(String string) {
        super(string);
    }
}
