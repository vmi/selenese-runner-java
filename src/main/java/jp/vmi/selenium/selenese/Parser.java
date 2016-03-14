package jp.vmi.selenium.selenese;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;
import org.apache.xpath.XPathAPI;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.utils.PathUtils;

import static org.apache.xerces.impl.Constants.*;

/**
 * Abstract class of selenese parser.
 */
public abstract class Parser {

    private static final String TEST_CASE_PROFILE = "http://selenium-ide.openqa.org/profiles/test-case";
    private static final String DEFAULT_BASE_URL = "about:blank";

    protected static class NodeIterator implements Iterator<Node> {
        private final NodeList nodeList;
        private int index = 0;

        protected NodeIterator(NodeList nodeList) {
            this.nodeList = nodeList;
        }

        @Override
        public boolean hasNext() {
            return index < nodeList.getLength();
        }

        @Override
        public Node next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return nodeList.item(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    protected static Iterable<Node> each(final NodeList nodeList) {
        return new Iterable<Node>() {
            @Override
            public Iterator<Node> iterator() {
                return new NodeIterator(nodeList);
            }
        };
    }

    /**
     * Parse input stream.
     *
     * @param filename selenese script file. (not open. used for label or generating output filename)
     * @param is input stream of script file. (test-case or test-suite)
     * @param runner Runner object.
     * @return TestCase or TestSuite.
     *
     * @deprecated Replaced by {@link #parse(String, InputStream, ICommandFactory)}
     */
    @Deprecated
    public static Selenese parse(String filename, InputStream is, Runner runner) {
        Selenese selenese = parse(filename, is, runner.getCommandFactory());
        setContextForBackwardCompatibility(selenese, runner);
        return selenese;
    }

    /**
     * Parse input stream.
     *
     * @param filename selenese script file. (not open. used for label or generating output filename)
     * @param is input stream of script file. (test-case or test-suite)
     * @param commandFactory command factory.
     * @return TestCase or TestSuite.
     */
    public static Selenese parse(String filename, InputStream is, ICommandFactory commandFactory) {
        try {
            DOMParser dp = new DOMParser();
            dp.setEntityResolver(null);
            dp.setFeature("http://xml.org/sax/features/namespaces", false);
            dp.setFeature(XERCES_FEATURE_PREFIX + INCLUDE_COMMENTS_FEATURE, true);
            dp.parse(new InputSource(is));
            Document document = dp.getDocument();
            Node seleniumBase = XPathAPI.selectSingleNode(document, "/HTML/HEAD/LINK[@rel='selenium.base']/@href");
            if (seleniumBase != null) {
                String baseURL = seleniumBase.getNodeValue();
                return new TestCaseParser(filename, document, baseURL).parse(commandFactory);
            }
            Node profile = XPathAPI.selectSingleNode(document, "/HTML/HEAD/@profile");
            if (profile != null && TEST_CASE_PROFILE.equals(profile.getNodeValue())) {
                return new TestCaseParser(filename, document, DEFAULT_BASE_URL).parse(commandFactory);
            }
            Node suiteTable = XPathAPI.selectSingleNode(document, "/HTML/BODY/TABLE[@id='suiteTable']");
            if (suiteTable != null) {
                return new TestSuiteParser(filename, document).parse(commandFactory);
            }
            return Binder.newErrorTestCase(filename, new InvalidSeleneseException(
                "Not selenese script. Missing neither 'selenium.base' link nor table with 'suiteTable' id"));
        } catch (Exception e) {
            return Binder.newErrorTestCase(filename, new InvalidSeleneseException(e));
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Parse file.
     *
     * @param filename selenese script file. (test-case or test-suite)
     * @param runner Runner object.
     * @return TestCase or TestSuite.
     *
     * @deprecated {@link #parse(String, ICommandFactory)}
     */
    @Deprecated
    public static Selenese parse(String filename, Runner runner) {
        Selenese selenese = parse(filename, runner.getCommandFactory());
        setContextForBackwardCompatibility(selenese, runner);
        return selenese;
    }

    /**
     * Parse file.
     *
     * @param filename selenese script file. (test-case or test-suite)
     * @param commandFactory command factory.
     * @return TestCase or TestSuite.
     */
    public static Selenese parse(String filename, ICommandFactory commandFactory) {
        try {
            return parse(filename, new FileInputStream(filename), commandFactory);
        } catch (FileNotFoundException e) {
            return Binder.newErrorTestCase(filename, new InvalidSeleneseException(e.getMessage()));
        }
    }

    /**
     * Set context to selenese for backward compatibility.
     *
     * @param selenese Selenese object.
     * @param context Selenese Runner context.
     * @return selenese itself.
     */
    @Deprecated
    public static Selenese setContextForBackwardCompatibility(Selenese selenese, Context context) {
        if (selenese instanceof TestSuite) {
            for (Selenese child : ((TestSuite) selenese).getSeleneseList())
                setContextForBackwardCompatibility(child, context);
        } else if (selenese instanceof TestCase) {
            ((TestCase) selenese).setContext(context);
        }
        return selenese;
    }

    protected final String filename;
    protected final Document docucment;

    protected Parser(String filename, Document document) {
        this.filename = PathUtils.normalize(filename);
        this.docucment = document;
    }

    protected abstract Selenese parse(ICommandFactory commandFactory);
}
