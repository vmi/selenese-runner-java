package jp.vmi.selenium.selenese;

import java.io.File;
import java.util.Iterator;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class TestSuiteParser extends Parser {

    private final File parentDir;

    protected TestSuiteParser(Document document, File parentDir) {
        super(document);
        this.parentDir = parentDir;
    }

    public Iterable<File> getTestCaseFiles() throws InvalidSeleneseException {
        NodeList nodeList;
        try {
            nodeList = XPathAPI.selectNodeList(docucment, "//TBODY/TR/TD/A/@href");
        } catch (TransformerException e) {
            throw new InvalidSeleneseException(e);
        }
        final NodeIterator iter = new NodeIterator(nodeList);
        return new Iterable<File>() {
            @Override
            public Iterator<File> iterator() {
                return new Iterator<File>() {
                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @Override
                    public File next() {
                        String name = iter.next().getNodeValue();
                        File file = new File(name);
                        if (!file.isAbsolute())
                            file = new File(parentDir, name);
                        return file;
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
