package jp.vmi.selenium.selenese;

import jp.vmi.html.result.IHtmlResultTestSuite;
import jp.vmi.junit.result.ITestSuite;
import jp.vmi.selenium.selenese.inject.ExecuteTestSuite;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Test-suite with errors.
 */
public class ErrorTestSuite extends ErrorSource implements ITestSuite, IHtmlResultTestSuite {

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
    public Result execute(Selenese parent, Context context) throws InvalidSeleneseException {
        return super.execute(parent, context);
    }
}
