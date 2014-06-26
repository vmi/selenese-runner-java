package jp.vmi.selenium.selenese;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.junit.result.ITestSuite;
import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.inject.ExecuteTestSuite;
import jp.vmi.selenium.selenese.log.PageInformation;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.PathUtils;
import jp.vmi.selenium.selenese.utils.StopWatch;

import static jp.vmi.selenium.selenese.result.Unexecuted.*;

/**
 * test-suite object for execution.
 */
public class TestSuite implements Selenese, ITestSuite {

    private static final Logger log = LoggerFactory.getLogger(TestSuite.class);

    private String filename;
    private String parentDir = null;
    private String name;
    private final List<Selenese> seleneseList = new ArrayList<Selenese>();

    private final StopWatch stopWatch = new StopWatch();
    private Result result = UNEXECUTED;

    /**
     * Initialize after constructed.
     *
     * @param filename Selenese script file.
     * @param name test-case name.
     * @return this.
     */
    public TestSuite initialize(String filename, String name) {
        this.filename = filename = PathUtils.normalize(filename);
        if (filename != null)
            this.parentDir = FilenameUtils.getFullPathNoEndSeparator(filename);
        if (name != null)
            this.name = name;
        else if (filename != null)
            this.name = FilenameUtils.getBaseName(filename);
        return this;
    }

    @Override
    public Type getType() {
        return Type.TEST_SUITE;
    }

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Add Selenese (test-suite/test-case) instance.
     *
     * @param selenese Selenese instance.
     */
    public void addSelenese(Selenese selenese) {
        seleneseList.add(selenese);
    }

    /**
     * Add Selenese file. (test-suite or test-case)
     *
     * @param filename Selenese file name.
     * @param commandFactory command factory.
     */
    public void addSeleneseFile(String filename, ICommandFactory commandFactory) {
        if (FilenameUtils.getPrefixLength(filename) == 0 && parentDir != null)
            filename = PathUtils.concat(parentDir, filename);
        else
            filename = PathUtils.normalize(filename);
        addSelenese(Parser.parse(filename, commandFactory));
    }

    /**
     * Get Selenese list.
     *
     * @return Selenese list.
     */
    public List<Selenese> getSeleneseList() {
        return seleneseList;
    }

    /**
     * Get stop watch.
     *
     * @return stop watch.
     */
    @Override
    public StopWatch getStopWatch() {
        return stopWatch;
    }

    /**
     * Get test-suite result.
     *
     * @return test-suite result.
     */
    public Result getResult() {
        return result;
    }

    @ExecuteTestSuite
    @Override
    public Result execute(Selenese parent, Context context) {
        context.prepareWebDriver();
        context.setLatestPageInformation(PageInformation.EMPTY);
        context.resetSpeed();
        for (Selenese selenese : seleneseList) {
            Result r;
            try {
                r = selenese.execute(this, context);
            } catch (RuntimeException e) {
                String msg = e.getMessage();
                result = new Error(msg);
                log.error(msg);
                throw e;
            } catch (InvalidSeleneseException e) {
                r = new Error(e);
            }
            result = result.update(r);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("TestSuite[").append(name).append("]");
        if (filename != null)
            s.append(" (").append(filename).append(")");
        return s.toString();
    }
}
