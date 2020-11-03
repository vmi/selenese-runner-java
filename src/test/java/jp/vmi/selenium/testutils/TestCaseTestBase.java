package jp.vmi.selenium.testutils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.Selenese;
import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Unexecuted;

import static jp.vmi.selenium.selenese.config.IConfig.*;

@SuppressWarnings("javadoc")
public abstract class TestCaseTestBase extends TestBase {

    @Rule
    public final TemporaryFolder screenshotDir = new TemporaryFolder();

    @Rule
    public final TemporaryFolder screenshotOnFailDir = new TemporaryFolder();

    @Rule
    public final TemporaryFolder xmlResultDir = new TemporaryFolder();

    public WebDriver driver;

    public Runner runner;

    public List<TestSuite> testSuites;

    public Result result;

    public String xmlResult;

    protected abstract void initDriver();

    @Before
    public void initialize() {
        initDriver();

        testSuites = new ArrayList<>();
        runner = new Runner() {
            @Override
            public Result execute(Selenese testSuite) {
                if (!(testSuite instanceof TestSuite))
                    throw new RuntimeException("The parameter is not TestSuite instance: " + testSuite);
                testSuites.add((TestSuite) testSuite);
                return super.execute(testSuite);
            }
        };
        runner.setDriver(driver);
        runner.setOverridingBaseURL(wsr.getBaseURL());
        runner.setScreenshotDir(screenshotDir.getRoot().getPath());
        runner.setScreenshotOnFailDir(screenshotOnFailDir.getRoot().getPath());
    }

    protected void setOptions(Object... options) {
        runner.setMaxRetries(0);
        for (int i = 0; i < options.length; i++) {
            switch ((String) options[i]) {
            case MAX_RETRIES:
                Integer maxRetries = (Integer) options[++i];
                runner.setMaxRetries(maxRetries);
                break;
            default:
                break;
            }
        }

    }

    protected void execute(String scriptName, Object... options) {
        setOptions(options);
        result = Unexecuted.UNEXECUTED;
        xmlResult = null;
        try {
            String scriptFile = TestUtils.getScriptFile(scriptName);
            runner.setJUnitResultDir(xmlResultDir.getRoot().getPath());
            result = runner.run(scriptFile);
            if (testSuites.isEmpty()) {
                xmlResult = null;
            } else {
                TestSuite testSuite = testSuites.get(0);
                Selenese child = testSuite.getSeleneseList().get(0);
                if (child instanceof TestSuite)
                    testSuite = (TestSuite) child;
                String xmlFile = testSuite.getFileBaseName("xml");
                xmlResult = FileUtils.readFileToString(new File(xmlResultDir.getRoot(), xmlFile), "UTF-8");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            runner.setJUnitResultDir(null);
        }
    }

    protected static interface Filter {
        String filter(String line);
    }

    protected List<String> getSystemOut(Filter filter) {
        List<String> list = new ArrayList<>();
        Pattern re = Pattern.compile("<system-out>(.*?)</system-out>", Pattern.DOTALL);
        Matcher m = re.matcher(xmlResult);
        if (!m.find())
            return list;
        String systemOut = StringEscapeUtils.unescapeXml(m.group(1));
        for (String line : systemOut.split("\r?\n|\r")) {
            String filtered = filter.filter(line);
            if (filtered != null)
                list.add(filtered);
        }
        return list;
    }

    protected List<String> listFilter(Filter filter, List<String> src) {
        List<String> dst = new ArrayList<>();
        for (String line : src) {
            String filtered = filter.filter(line);
            if (filtered != null)
                dst.add(filtered);
        }
        return dst;
    }
}
