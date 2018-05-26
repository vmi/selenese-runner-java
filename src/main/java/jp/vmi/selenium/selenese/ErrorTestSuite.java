package jp.vmi.selenium.selenese;

import jp.vmi.html.result.IHtmlResultTestSuite;
import jp.vmi.junit.result.ITestSuite;
import jp.vmi.selenium.selenese.inject.ExecuteTestSuite;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Test-suite with errors.
 */
public class ErrorTestSuite extends ErrorSource implements ITreedFileGenerator, ITestSuite, IHtmlResultTestSuite {

    private ITreedFileGenerator parent = null;
    private int index = 0;

    @Override
    public ErrorTestSuite initialize(String filename, InvalidSeleneseException e) {
        return (ErrorTestSuite) super.initialize(filename, e);
    }

    @Override
    public Type getType() {
        return Type.TEST_SUITE;
    }

    @Override
    public ITreedFileGenerator getParent() {
        return parent;
    }

    @Override
    public void setParent(ITreedFileGenerator parent) {
        this.parent = parent;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @ExecuteTestSuite
    @Override
    public Result execute(Selenese parent, Context context) throws InvalidSeleneseException {
        return super.execute(parent, context);
    }
}
