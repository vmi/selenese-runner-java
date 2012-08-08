package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.FileInputStream;
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

public abstract class Parser {

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

    public static Selenese parse(File file, Runner runner) throws InvalidSeleneseException {
        InputStream is = null;
        Parser p;
        try {
            DOMParser dp = new DOMParser();
            dp.setEntityResolver(null);
            dp.setFeature("http://xml.org/sax/features/namespaces", false);
            is = new FileInputStream(file);
            dp.parse(new InputSource(is));
            Document document = dp.getDocument();
            try {
                String baseURL = XPathAPI.selectSingleNode(document, "/HTML/HEAD/LINK[@rel='selenium.base']/@href").getNodeValue();
                p = new TestCaseParser(file, document, baseURL);
            } catch (NullPointerException e) {
                try {
                    XPathAPI.selectSingleNode(document, "/HTML/BODY/TABLE[@id='suiteTable']");
                    p = new TestSuiteParser(file, document);
                } catch (NullPointerException e2) {
                    throw new InvalidSeleneseException("neither 'selenium.base' link nor table with 'suiteTable' id");
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidSeleneseException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return p.parse(runner);
    }

    protected final File file;
    protected final Document docucment;

    protected Parser(File file, Document document) {
        this.file = file;
        this.docucment = document;
    }

    protected abstract Selenese parse(Runner runner) throws InvalidSeleneseException;
}
