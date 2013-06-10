package jp.vmi.selenium.selenese;

import org.apache.commons.io.FilenameUtils;

import jp.vmi.junit.result.ITestSuite;
import jp.vmi.selenium.selenese.inject.ExecuteTestSuite;
import jp.vmi.selenium.selenese.result.Result;

/**
 *
 */
public class ErrorTestSuite implements Selenese, ITestSuite {

    private String filename;
    private InvalidSeleneseException e;

    /**
     * Initialize.
     *
     * @param filename Selenese script file.
     * @param e InvalidSeleneseException or null.
     * @return this.
     */
    public ErrorTestSuite initialize(String filename, InvalidSeleneseException e) {
        this.filename = filename;
        this.e = e;
        return this;
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public String getName() {
        return FilenameUtils.getBaseName(filename);
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
