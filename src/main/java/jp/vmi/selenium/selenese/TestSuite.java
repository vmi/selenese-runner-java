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

    private static interface Child {
        Selenese getSelenese(Runner runner);
    }

    private static class ChildFilename implements Child {
        private final String filename;

        public ChildFilename(String filename) {
            this.filename = filename;
        }

        @Override
        public Selenese getSelenese(Runner runner) {
            return Parser.parse(filename, runner);
        }
    }

    private static class ChildTestCase implements Child {
        public final TestCase testCase;

        public ChildTestCase(TestCase testCase) {
            this.testCase = testCase;
        }

        @Override
        public Selenese getSelenese(Runner runner) {
            return testCase;
        }
    }

    private String filename;
    private String parentDir = null;
    private String name;
    private Runner runner;
    private final List<Child> children = new ArrayList<Child>();

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
        children.add(new ChildFilename(filename));
    }

    /**
     * Add test-case instance.
     *
     * @param testCase test-case instance.
     */
    public void addTestCase(TestCase testCase) {
        children.add(new ChildTestCase(testCase));
    }

    @ExecuteTestSuite
    @Override
    public Result execute(Selenese parent) {
        Result totalResult = SUCCESS;
        for (Child child : children) {
            Selenese selenese = child.getSelenese(runner);
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
