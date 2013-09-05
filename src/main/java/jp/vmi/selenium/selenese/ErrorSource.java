package jp.vmi.selenium.selenese;

import org.apache.commons.io.FilenameUtils;

import jp.vmi.junit.result.ITestTarget;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.StopWatch;

/**
 *
 */
public abstract class ErrorSource implements Selenese, ITestTarget {

    private String filename;
    private InvalidSeleneseException e;

    /**
     * Initialize.
     *
     * @param filename Selenese script file.
     * @param e InvalidSeleneseException or null.
     * @return this.
     */
    public ErrorSource initialize(String filename, InvalidSeleneseException e) {
        this.filename = filename;
        this.e = e;
        return this;
    }

    /**
     * Is error object?
     * 
     * @return true.
     */
    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public String getName() {
        return FilenameUtils.getBaseName(filename);
    }

    @Override
    public StopWatch getStopWatch() {
        return null;
    }

    @Override
    public Result execute(Selenese parent, Runner runner) throws InvalidSeleneseException {
        throw e;
    }

    @Override
    public String toString() {
        return String.format("%s[%s] %s", getClass().getSimpleName(), filename, e);
    }
}
