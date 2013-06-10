package jp.vmi.selenium.selenese;

import org.apache.commons.io.FilenameUtils;

import jp.vmi.junit.result.ITestCase;
import jp.vmi.selenium.selenese.inject.ExecuteTestCase;
import jp.vmi.selenium.selenese.result.Result;

/**
 *
 */
public class ErrorTestCase implements Selenese, ITestCase {

    private String filename;
    private InvalidSeleneseException e;

    /**
     * Initialize.
     *
     * @param filename Selenese script file.
     * @param e InvalidSeleneseException or null.
     * @return this.
     */
    public ErrorTestCase initialize(String filename, InvalidSeleneseException e) {
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

    @ExecuteTestCase
    @Override
    public Result execute(Selenese parent) throws InvalidSeleneseException {
        throw e;
    }
}
