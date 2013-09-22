package jp.vmi.selenium.selenese;

import jp.vmi.junit.result.ITestSuite;
import jp.vmi.selenium.selenese.inject.ExecuteTestSuite;
import jp.vmi.selenium.selenese.result.Result;

/**
 *
 */
public class ErrorTestSuite extends ErrorSource implements ITestSuite {

    @Override
    public ErrorTestSuite initialize(String filename, InvalidSeleneseException e) {
        return (ErrorTestSuite) super.initialize(filename, e);
    }

    @Override
    public Type getType() {
        return Type.TEST_SUITE;
    }

    @ExecuteTestSuite
    @Override
    public Result execute(Selenese parent, Runner runner) throws InvalidSeleneseException {
        return super.execute(parent, runner);
    }
}
