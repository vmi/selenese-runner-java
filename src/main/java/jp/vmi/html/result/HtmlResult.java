package jp.vmi.html.result;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.floreysoft.jmte.Engine;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.SeleniumUtils;

/**
 * HTML result generator.
 */
public class HtmlResult {

    private final String htmlResultDir;
    private String template = null;
    private Engine engine = null;

    private final List<String> testSuiteNameList = new ArrayList<String>();

    /**
     * Constructor.
     *
     * @param dir output html result directory.
     */
    public HtmlResult(String dir) {
        this.htmlResultDir = dir;
    }

    private String getTemplate(String filename) {
        if (template == null) {
            InputStream is = null;
            try {
                is = getClass().getResourceAsStream(filename);
                template = IOUtils.toString(is);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return template;
    }

    private Engine getEngine() {
        if (engine == null) {
            engine = new Engine();
            engine.registerNamedRenderer(new HtmlEscapeRenderer());
            engine.registerNamedRenderer(new IndexRenderer());
            engine.registerRenderer(Result.class, new ResultRenderer());
        }
        return engine;
    }

    /**
     * Generate HTML result.
     *
     * @param testSuite test-suite instance.
     */
    public void generate(TestSuite testSuite) {
        List<TestCase> testCaseList = testSuite.getTestCaseList();
        int numTestPasses = 0;
        int numTestFailures = 0;
        int numCommandPasses = 0;
        int numCommandFailures = 0;
        int numCommandErrors = 0;
        for (TestCase testCase : testCaseList) {
            switch (testCase.getResult().getLevel()) {
            case UNEXECUTED:
                // no count.
                break;
            case SUCCESS:
            case WARNING:
                numTestPasses++;
                break;
            case FAILURE:
            case ERROR:
                numTestFailures++;
                break;
            }
            for (Command command : testCase.getCommandList()) {
                switch (command.getResult().getLevel()) {
                case UNEXECUTED:
                    // no count
                    break;
                case SUCCESS:
                case WARNING:
                    numCommandPasses++;
                    break;
                case FAILURE:
                    numCommandFailures++;
                    break;
                case ERROR:
                    numCommandErrors++;
                    break;
                }
            }
        }
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("title", testSuite.getName() + " results");
        model.put("seleniumVersion", SeleniumUtils.getVersion());
        model.put("testSuite", testSuite);
        model.put("testCaseList", new WithIndex<TestCase>(testCaseList));
        model.put("numTestTotal", testCaseList.size());
        model.put("numTestPasses", numTestPasses);
        model.put("numTestFailures", numTestFailures);
        model.put("numCommandPasses", numCommandPasses);
        model.put("numCommandFailures", numCommandFailures);
        model.put("numCommandErrors", numCommandErrors);
        String html = getEngine().transform(getTemplate("result.html"), model);
        File file = new File(htmlResultDir, "TEST-" + testSuite.getName() + ".html");
        try {
            FileUtils.write(file, html);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        testSuiteNameList.add(testSuite.getName());
    }

    /**
     * Generate index for HTML result.
     */
    public void generateIndex() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("nameList", testSuiteNameList);
        String html = getEngine().transform(getTemplate("index.html"), model);
        File file = new File(htmlResultDir, "index.html");
        try {
            FileUtils.write(file, html);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
