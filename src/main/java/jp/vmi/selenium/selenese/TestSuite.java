package jp.vmi.selenium.selenese;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.html.result.IHtmlResultTestSuite;
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
public class TestSuite implements Selenese, ITestSuite, IHtmlResultTestSuite {

    private static final Logger log = LoggerFactory.getLogger(TestSuite.class);

    private String filename = null;
    private String baseName = null;
    private String name = null;

    private String parentDir = null;
    private String webDriverName = null;
    private final List<Selenese> seleneseList = new ArrayList<>();

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
        filename = PathUtils.normalize(filename);
        String baseName;
        if (filename != null) {
            baseName = FilenameUtils.getBaseName(filename);
            this.parentDir = FilenameUtils.getFullPathNoEndSeparator(filename);
        } else {
            baseName = name;
        }
        if (name == null)
            name = baseName;
        this.filename = filename;
        this.baseName = baseName;
        this.name = name;
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

    @Override
    public String getBaseName() {
        return baseName;
    }

    /**
     * Get WebDriver name.
     *
     * @return WebDriver name.
     */
    public String getWebDriverName() {
        return webDriverName;
    }

    /**
     * Set WebDriver name.
     *
     * @param webDriverName WebDriver name.
     */
    public void setWebDriverName(String webDriverName) {
        this.webDriverName = webDriverName;
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

    @Override
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
