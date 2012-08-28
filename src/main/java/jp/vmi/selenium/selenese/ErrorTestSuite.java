package jp.vmi.selenium.selenese;

import jp.vmi.junit.result.ITestSuite;
import jp.vmi.selenium.selenese.inject.ExecuteTestSuite;
import jp.vmi.selenium.selenese.result.Result;

/**
 *
 */
public class ErrorTestSuite implements Selenese, ITestSuite {

    private String name;
    private InvalidSeleneseException e;

    /**
     * Initialize.
     *
     * @param name test-case name.
     * @param e InvalidSeleneseException or null.
     * @return this.
     */
    public ErrorTestSuite initialize(String name, InvalidSeleneseException e) {
        this.name = name;
        this.e = e;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Runner getRunner() {
        return null;
    }

    @ExecuteTestSuite
    @Override
    public Result execute(Selenese parent) throws InvalidSeleneseException {
        throw e;
    }
}
