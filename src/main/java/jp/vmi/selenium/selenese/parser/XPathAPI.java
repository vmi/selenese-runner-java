package jp.vmi.selenium.selenese.parser;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * org.apache.xpath.XPathAPI alternative.
 */
public class XPathAPI {

    /**
     * Select single node.
     *
     * @param document XML document.
     * @param query query
     * @return node
     * @throws TransformerException exception.
     */
    public static Node selectSingleNode(Document document, String query) throws TransformerException {
        XPathFactory f = XPathFactory.newInstance();
        XPath xPath = f.newXPath();
        try {
            XPathExpression expr = xPath.compile(query);
            return (Node) expr.evaluate(document, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new TransformerException(e);
        }
    }

    /**
     * Select node list.
     *
     * @param document XML document.
     * @param query query
     * @return list of node
     * @throws TransformerException exception.
     */
    public static NodeList selectNodeList(Document document, String query) throws TransformerException {
        XPathFactory f = XPathFactory.newInstance();
        XPath xPath = f.newXPath();
        try {
            XPathExpression expr = xPath.compile(query);
            return (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new TransformerException(e);
        }
    }

}
