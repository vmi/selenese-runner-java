package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.inject.ExecuteTestSuite;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * test-suite object for execution.
 */
public class TestSuite implements Selenese {

    private static final Logger log = LoggerFactory.getLogger(TestSuite.class);

    private File file;
    private String parentDir = null;
    private String name;
    private final List<File> files = new ArrayList<File>();

    /**
     * Initialize after constructed.
     *
     * @param file Selenese script file.
     * @param name test-case name.
     * @return this.
     */
    public TestSuite initialize(File file, String name) {
        try {
            this.file = file;
            if (file != null)
                this.parentDir = file.getCanonicalFile().getParent();
            if (name != null)
                this.name = name;
            else if (file != null)
                this.name = file.getName().replaceFirst("\\.[^.]+$", "");
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Add test-case filename.
     *
     * @param filename test-case filename.
     */
    public void addTestCase(String filename) {
        File tcFile = new File(filename);
        if (!tcFile.isAbsolute())
            tcFile = new File(parentDir, filename);
        files.add(tcFile);
    }

    @ExecuteTestSuite
    @Override
    public Result execute(Runner runner) {
        Result totalResult = SUCCESS;
        for (File file : files) {
            Result result = null;
            try {
                Selenese selenese = Parser.parse(file, runner);
                result = selenese.execute(runner);
            } catch (RuntimeException e) {
                log.error(e.getMessage());
                throw e;
            } catch (InvalidSeleneseException e) {
                result = new Failure(e.getMessage());
                result.addErrorLog(e.getMessage());
                log.error(e.getMessage());
            }
            totalResult = totalResult.update(result);
        }
        return totalResult;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("TestSuite[").append(name).append("]");
        if (file != null)
            s.append(" (").append(file).append(")");
        return s.toString();
    }
}
