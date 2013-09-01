package jp.vmi.selenium.selenese;

import org.apache.commons.io.FilenameUtils;

import jp.vmi.selenium.selenese.result.Result;

/**
 *
 */
public abstract class ErrorSource implements Selenese {

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
    public boolean isError() {
        return true;
    }

    @Override
    public String getName() {
        return FilenameUtils.getBaseName(filename);
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
