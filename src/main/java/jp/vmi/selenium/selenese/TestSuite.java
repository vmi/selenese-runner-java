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
    private final List<TestCase> testCaseList = new ArrayList<TestCase>();

    private final StopWatch stopWatch = new StopWatch();
    private Result result = UNEXECUTED;

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

    /**
     * Add test-case instance.
     *
     * @param testCase test-case instance.
     */
    public void addTestCase(TestCase testCase) {
        testCaseList.add(testCase);
    }

    /**
     * Add test-case filename.
     *
     * @param filename test-case filename.
     * @param runner Runner object.
     */
    public void addTestCase(String filename, Runner runner) {
        if (FilenameUtils.getPrefixLength(filename) == 0 && parentDir != null)
            filename = FilenameUtils.concat(parentDir, filename);
        addTestCase((TestCase) Parser.parse(filename, runner));
    }

    /**
     * Get list of test-case instances. 
     * 
     * @return list of test-case instances.
     */
    public List<TestCase> getTestCaseList() {
        return testCaseList;
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
    public Result execute(Selenese parent, Runner runner) {
        for (TestCase testCase : testCaseList) {
            Result r;
            try {
                r = testCase.execute(this, runner);
            } catch (RuntimeException e) {
                String msg = e.getMessage();
                result = new Error(msg);
                log.error(msg);
                throw e;
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
