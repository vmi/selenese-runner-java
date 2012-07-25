package jp.vmi.selenium.selenese;

import java.util.Iterator;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class TestSuiteParser extends Parser {

    protected TestSuiteParser(Document document) {
        super(document);
    }

    public Iterable<String> getTestCaseFiles() throws InvalidSeleneseException {
        NodeList nodeList;
        try {
            nodeList = XPathAPI.selectNodeList(docucment, "//TBODY/TR/TD/A/@href");
        } catch (TransformerException e) {
            throw new InvalidSeleneseException(e);
        }
        final NodeIterator iter = new NodeIterator(nodeList);
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @Override
                    public String next() {
                        return iter.next().getNodeValue();
                    }

                    @Override
                    public void remove() {
                        iter.remove();
                    }
                };
            }
        };
    }
}
