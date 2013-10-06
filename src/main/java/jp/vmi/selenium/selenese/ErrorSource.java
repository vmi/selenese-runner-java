package jp.vmi.selenium.selenese;

import org.apache.commons.io.FilenameUtils;

import jp.vmi.junit.result.ITestTarget;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.StopWatch;

/**
 *
 */
public abstract class ErrorSource implements Selenese, ITestTarget {

    private String filename;
    private InvalidSeleneseException e;

    private final StopWatch stopWatch = new StopWatch();

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

    /**
     * Get filename.
     *
     * @return filename.
     */
    public String getFilename() {
        return filename;
    }

    @Override
    public StopWatch getStopWatch() {
        return stopWatch;
    }

    @Override
    public Result execute(Selenese parent, Runner runner) throws InvalidSeleneseException {
        throw e;
    }

    /**
     * Get result.
     *
     * @return result.
     */
    public Result getResult() {
        return new Error(e);
    }

    @Override
    public String toString() {
        return String.format("%s[%s] %s", getClass().getSimpleName(), filename, e);
    }
}
