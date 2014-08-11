package jp.vmi.html.result;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.floreysoft.jmte.AnnotationProcessor;
import com.floreysoft.jmte.Engine;
import com.floreysoft.jmte.TemplateContext;
import com.floreysoft.jmte.token.AnnotationToken;

import jp.vmi.html.result.TestSuiteTree.Node;
import jp.vmi.selenium.selenese.Selenese;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.SeleniumUtils;

/**
 * HTML result generator.
 */
public class HtmlResult {

    private static final Logger log = LoggerFactory.getLogger(HtmlResult.class);

    private static class AnnoSet implements AnnotationProcessor<String> {

        @Override
        public String getType() {
            return "set";
        }

        @Override
        public String eval(AnnotationToken token, TemplateContext context) {
            String[] args = token.getArguments().split("\\s+");
            context.model.put(args[0], args[1]);
            return null;
        }
    }

    private static class AnnoInc implements AnnotationProcessor<String> {

        @Override
        public String getType() {
            return "inc";
        }

        @Override
        public String eval(AnnotationToken token, TemplateContext context) {
            String key = token.getArguments();
            int value = NumberUtils.toInt(context.model.get(key).toString());
            context.model.put(key, value + 1);
            return null;
        }
    }

    private String htmlResultDir = null;
    private Engine engine = null;

    private final TestSuiteTree tree = new TestSuiteTree();

    /**
     * Get HTML result directory.
     *
     * @return HTML result directory.
     */
    public String getDir() {
        return htmlResultDir;
    }

    /**
     * Set HTML result directory.
     *
     * @param dir HTML result directory.
     */
    public void setDir(String dir) {
        this.htmlResultDir = dir;
    }

    private String getTemplate(String filename) {
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(filename);
            return IOUtils.toString(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private Engine getEngine() {
        if (engine == null) {
            engine = Engine.createCompilingEngine();
            engine.registerNamedRenderer(new HtmlEscapeRenderer());
            engine.registerNamedRenderer(new LogRenderer(this));
            engine.registerNamedRenderer(new IndexRenderer());
            engine.registerNamedRenderer(new RelativePathRenderer(this));
            engine.registerRenderer(Result.class, new ResultRenderer());
            engine.registerRenderer(Node.class, new NodeRenderer(engine, getTemplate("index-node.html")));
            engine.registerAnnotationProcessor(new AnnoSet());
            engine.registerAnnotationProcessor(new AnnoInc());
        }
        return engine;
    }

    /**
     * Generate HTML result.
     *
     * @param testSuite test-suite instance.
     * @return test-suite summary.
     */
    public TestSuiteSummary generate(TestSuite testSuite) {
        if (htmlResultDir == null)
            return null;
        TestSuiteSummary summary = tree.getSummary(testSuite);
        if (summary != null)
            return summary;
        List<Selenese> seleneseList = testSuite.getSeleneseList();
        summary = new TestSuiteSummary();
        for (Selenese selenese : seleneseList) {
            switch (selenese.getType()) {
            case TEST_SUITE:
                if (selenese.isError())
                    break;
                summary.merge(generate((TestSuite) selenese));
                break;
            case TEST_CASE:
                summary.numTestTotal++;
                if (selenese.isError()) {
                    summary.numTestFailures++;
                    break;
                }
                TestCase testCase = (TestCase) selenese;
                switch (testCase.getResult().getLevel()) {
                case UNEXECUTED:
                    // no count.
                    break;
                case SUCCESS:
                case WARNING:
                    summary.numTestPasses++;
                    break;
                case FAILURE:
                case ERROR:
                    summary.numTestFailures++;
                    break;
                }
                for (ICommand command : testCase.getCommandList()) {
                    switch (command.getResult().getLevel()) {
                    case UNEXECUTED:
                        // no count
                        break;
                    case SUCCESS:
                    case WARNING:
                        summary.numCommandPasses++;
                        break;
                    case FAILURE:
                        summary.numCommandFailures++;
                        break;
                    case ERROR:
                        summary.numCommandErrors++;
                        break;
                    }
                }
                break;
            }
        }
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("title", testSuite.getName() + " results");
        model.put("seleniumVersion", SeleniumUtils.getVersion());
        model.put("testSuite", testSuite);
        model.put("seleneseList", seleneseList);
        model.put("numTestTotal", summary.numTestTotal);
        model.put("numTestPasses", summary.numTestPasses);
        model.put("numTestFailures", summary.numTestFailures);
        model.put("numCommandPasses", summary.numCommandPasses);
        model.put("numCommandFailures", summary.numCommandFailures);
        model.put("numCommandErrors", summary.numCommandErrors);
        String html = getEngine().transform(getTemplate("result.html"), model);
        File file = new File(htmlResultDir, "TEST-" + testSuite.getName() + ".html");
        try {
            FileUtils.write(file, html);
            log.info("Generated HTML result: {}", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tree.add(testSuite, summary);
        return summary;
    }

    /**
     * Generate index for HTML result.
     */
    public void generateIndex() {
        if (htmlResultDir == null)
            return;
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("title", "Index of test-suite results.");
        model.put("tree", tree);
        String html = getEngine().transform(getTemplate("index.html"), model);
        File file = new File(htmlResultDir, "index.html");
        try {
            FileUtils.write(file, html);
            log.info("Generated index of HTML results: {}", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
