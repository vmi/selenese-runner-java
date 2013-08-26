package jp.vmi.html.result;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.floreysoft.jmte.Engine;

import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.result.Result;

/**
 * HTML result generator.
 */
public class HtmlResult {

    private final String htmlResultDir;
    private String template = null;
    private Engine engine = null;

    /**
     * Constructor.
     *
     * @param dir output html result directory.
     */
    public HtmlResult(String dir) {
        this.htmlResultDir = dir;
    }

    private String getTemplate() {
        if (template == null) {
            InputStream is = null;
            try {
                is = getClass().getResourceAsStream("result.html");
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
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("testSuite", testSuite);
        model.put("testCaseList", testSuite.getTestCaseList());
        String html = getEngine().transform(getTemplate(), model);
        File file = new File(htmlResultDir, "TEST-" + testSuite.getName() + ".html");
        try {
            FileUtils.write(file, html);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
