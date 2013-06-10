package jp.vmi.selenium.selenese;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.junit.result.ITestSuite;
import jp.vmi.selenium.selenese.inject.ExecuteTestSuite;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * test-suite object for execution.
 */
public class TestSuite implements Selenese, ITestSuite {

    private static final Logger log = LoggerFactory.getLogger(TestSuite.class);

    private String filename;
    private String parentDir = null;
    private String name;
    private Runner runner;
    private final List<String> filenames = new ArrayList<String>();

    /**
     * Initialize after constructed.
     *
     * @param filename Selenese script file.
     * @param name test-case name.
     * @param runner Runner instance.
     * @return this.
     */
    public TestSuite initialize(String filename, String name, Runner runner) {
        this.filename = filename;
        if (filename != null)
            this.parentDir = FilenameUtils.getFullPathNoEndSeparator(filename);
        if (name != null)
            this.name = name;
        else if (filename != null)
            this.name = FilenameUtils.getBaseName(filename);
        this.runner = runner;
        return this;
    }

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Runner getRunner() {
        return runner;
    }

    /**
     * Add test-case filename.
     *
     * @param filename test-case filename.
     */
    public void addTestCase(String filename) {
        if (FilenameUtils.getPrefixLength(filename) == 0)
            filename = FilenameUtils.concat(parentDir, filename);
        filenames.add(filename);
    }

    @ExecuteTestSuite
    @Override
    public Result execute(Selenese parent) {
        Result totalResult = SUCCESS;
        for (String filename : filenames) {
            Selenese selenese = Parser.parse(filename, runner);
            Result result;
            try {
                result = selenese.execute(this);
            } catch (RuntimeException e) {
                log.error(e.getMessage());
                throw e;
            } catch (InvalidSeleneseException e) {
                result = new Error(e);
            }
            totalResult = totalResult.update(result);
        }
        return totalResult;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("TestSuite[").append(name).append("]");
        if (filename != null)
            s.append(" (").append(filename).append(")");
        return s.toString();
    }
}
