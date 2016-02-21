package jp.vmi.selenium.selenese;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;

/**
 * Parse Selenese script of test-case.
 */
public class TestCaseParser extends Parser {

    private final String baseURL;

    protected TestCaseParser(String filename, Document document, String baseURL) throws InvalidSeleneseException {
        super(filename, document);
        this.baseURL = baseURL;
    }

    private String getTdString(Node td) {
        StringBuilder value = new StringBuilder();
        for (Node node : each(td.getChildNodes())) {
            if ("BR".equals(node.getNodeName()))
                value.append('\n');
            else
                value.append(node.getTextContent());
        }
        return value.toString();
    }

    @Override
    public Selenese parse(ICommandFactory commandFactory) {
        String name = null;
        try {
            name = XPathAPI.selectSingleNode(docucment, "//THEAD/TR/TD").getTextContent();
            TestCase testCase = Binder.newTestCase(filename, name, baseURL);
            Node tbody = XPathAPI.selectSingleNode(docucment, "//TBODY");
            NodeList trList = tbody.getChildNodes();
            for (Node tr : each(trList)) {
                List<String> cmdWithArgs;
                switch (tr.getNodeType()) {
                case Node.ELEMENT_NODE: // TD
                    cmdWithArgs = new ArrayList<>(3);
                    for (Node td : each(tr.getChildNodes())) {
                        if ("TD".equals(td.getNodeName()))
                            cmdWithArgs.add(getTdString(td));
                    }
                    break;
                case Node.COMMENT_NODE:
                    cmdWithArgs = new ArrayList<>(2);
                    cmdWithArgs.add("comment");
                    cmdWithArgs.add(tr.getNodeValue().trim());
                    break;
                default: // skip whitespace text.
                    continue;
                }
                String cmdName = cmdWithArgs.remove(0);
                String[] cmdArgs = cmdWithArgs.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
                testCase.addCommand(commandFactory, cmdName, cmdArgs);
            }
            return testCase;
        } catch (TransformerException e) {
            if (name == null)
                name = FilenameUtils.getBaseName(filename);
            return Binder.newErrorTestCase(name, new InvalidSeleneseException(e));
        }
    }
}
