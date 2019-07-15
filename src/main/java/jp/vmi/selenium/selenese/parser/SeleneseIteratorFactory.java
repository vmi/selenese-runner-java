package jp.vmi.selenium.selenese.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import jp.vmi.selenium.selenese.InvalidSeleneseException;

import static org.apache.xerces.impl.Constants.*;

/**
 * Selenese iterator factory.
 */
public class SeleneseIteratorFactory {

    private static final String TEST_CASE_PROFILE = "http://selenium-ide.openqa.org/profiles/test-case";
    private static final String DEFAULT_BASE_URL = "about:blank";

    /**
     * Create test iterator of Selenese format.
     *
     * @param filename selenese script file.
     * @return SeleneseIteratorFactory.
     * @throws InvalidSeleneseException Invalid selenese exception.
     */
    public static SeleneseIteratorFactory newInstance(String filename) throws InvalidSeleneseException {
        try (InputStream is = new FileInputStream(filename)) {
            return newInstance(filename, is);
        } catch (IOException e) {
            throw new InvalidSeleneseException(e, filename, ParserUtils.getNameFromFilename(filename));
        }
    }

    /**
     * Create test iterator of Selenese format.
     *
     * @param filename selenese script file. (Don't use to open a file. It is used as a label and is used to generate filenames based on it)
     * @param is input stream of script file. (test-case or test-suite)
     * @return SeleneseIteratorFactory.
     * @throws InvalidSeleneseException Invalid selenese exception.
     */
    public static SeleneseIteratorFactory newInstance(String filename, InputStream is) throws InvalidSeleneseException {
        try {
            DOMParser dp = new DOMParser();
            dp.setEntityResolver(null);
            dp.setFeature("http://xml.org/sax/features/namespaces", false);
            dp.setFeature(XERCES_FEATURE_PREFIX + INCLUDE_COMMENTS_FEATURE, true);
            dp.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
            dp.parse(new InputSource(is));
            Document document = dp.getDocument();
            return new SeleneseIteratorFactory(filename, document);
        } catch (SAXException | IOException e) {
            throw new InvalidSeleneseException(e, filename, null);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // no operation.
            }
        }
    }

    private final String filename;
    private final Document document;

    private SeleneseIteratorFactory(String filename, Document document) {
        this.filename = filename;
        this.document = document;
    }

    /**
     * Get filename.
     *
     * @return filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Create test iterator of Selenese format.
     *
     * @return TestCase or TestSuite.
     * @throws InvalidSeleneseException Invalid selenese exception.
     */
    public TestElementIterator<?> newIterator() throws InvalidSeleneseException {
        try {
            Node seleniumBase = XPathAPI.selectSingleNode(document, "/HTML/HEAD/LINK[@rel='selenium.base']/@href");
            if (seleniumBase != null) {
                String baseURL = seleniumBase.getNodeValue();
                return new SeleneseCommandIterator(filename, document, baseURL);
            }
            Node profile = XPathAPI.selectSingleNode(document, "/HTML/HEAD/@profile");
            if (profile != null && TEST_CASE_PROFILE.equals(profile.getNodeValue())) {
                return new SeleneseCommandIterator(filename, document, DEFAULT_BASE_URL);
            }
            Node suiteTable = XPathAPI.selectSingleNode(document, "/HTML/BODY/TABLE[@id='suiteTable']");
            if (suiteTable != null) {
                return new SeleneseTestCaseIterator(filename, document);
            }
        } catch (TransformerException e) {
            throw new InvalidSeleneseException(e, filename, null);
        }
        throw new InvalidSeleneseException("Not selenese script. Missing neither 'selenium.base' link nor table with 'suiteTable' id", filename, null);
    }
}
