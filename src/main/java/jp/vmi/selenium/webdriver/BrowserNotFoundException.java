package jp.vmi.selenium.webdriver;

import com.thoughtworks.selenium.SeleniumException;

public class BrowserNotFoundException extends SeleniumException {

    private static final long serialVersionUID = 1L;

    public BrowserNotFoundException(String message) {
        super(message);
    }

}
