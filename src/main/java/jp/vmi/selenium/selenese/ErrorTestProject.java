package jp.vmi.selenium.selenese;

/**
 * Test-suite with errors.
 */
public class ErrorTestProject extends ErrorTestSuite {

    @Override
    public ErrorTestProject initialize(String filename, InvalidSeleneseException e) {
        return (ErrorTestProject) super.initialize(filename, e);
    }

    @Override
    public Type getType() {
        return Type.TEST_PROJECT;
    }
}
