package jp.vmi.selenium.selenese;

import jp.vmi.junit.result.ITestCase;
import jp.vmi.selenium.selenese.inject.ExecuteTestCase;
import jp.vmi.selenium.selenese.result.Result;

/**
 *
 */
public class ErrorTestCase implements Selenese, ITestCase {

    private String name;
    private InvalidSeleneseException e;

    /**
     * Initialize.
     *
     * @param name test-case name.
     * @param e InvalidSeleneseException or null.
     * @return this.
     */
    public ErrorTestCase initialize(String name, InvalidSeleneseException e) {
        this.name = name;
        this.e = e;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @ExecuteTestCase
    @Override
    public Result execute(Selenese parent, Runner runner) throws InvalidSeleneseException {
        throw e;
    }
}
