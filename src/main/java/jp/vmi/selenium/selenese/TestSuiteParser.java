package jp.vmi.selenium.selenese;

import java.io.File;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FilenameUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jp.vmi.selenium.selenese.inject.Binder;

/**
 * Parse Selenese script of test-suite.
 */
public class TestSuiteParser extends Parser {

    protected TestSuiteParser(File file, Document document) {
        super(file, document);
    }

    @Override
    protected Selenese parse(Runner runner) {
        try {
            TestSuite testSuite = Binder.newTestSuite(file, null, runner);
            NodeList nodeList = XPathAPI.selectNodeList(docucment, "//TBODY/TR/TD/A/@href");
            for (Node node : each(nodeList)) {
                String tcFilename = node.getNodeValue();
                testSuite.addTestCase(tcFilename);
            }
            return testSuite;
        } catch (TransformerException e) {
            return Binder.newErrorTestSuite(FilenameUtils.getBaseName(file.getName()), new InvalidSeleneseException(e));
        }
    }
}
