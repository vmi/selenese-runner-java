package jp.vmi.selenium.selenese;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FilenameUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.command.EndLoop;
import jp.vmi.selenium.selenese.command.Label;
import jp.vmi.selenium.selenese.command.StartLoop;
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

    @Override
    public Selenese parse(Runner runner) {
        String name = null;
        try {
            String baseURL = runner.getEffectiveBaseURL(this.baseURL);
            name = XPathAPI.selectSingleNode(docucment, "//THEAD/TR/TD").getTextContent();
            TestCase testCase = Binder.newTestCase(filename, name, runner, baseURL);
            CommandFactory commandFactory = runner.getCommandFactory();
            commandFactory.setProc(testCase.getProc());
            Node tbody = XPathAPI.selectSingleNode(docucment, "//TBODY");
            NodeList trList = tbody.getChildNodes();
            Deque<StartLoop> loopCommandStack = new ArrayDeque<StartLoop>();
            int tri = 0;
            for (Node tr : each(trList)) {
                List<String> cmdWithArgs;
                switch (tr.getNodeType()) {
                case Node.ELEMENT_NODE: // TD
                    tri++;
                    cmdWithArgs = new ArrayList<String>(3);
                    for (Node td : each(tr.getChildNodes())) {
                        if ("TD".equals(td.getNodeName())) {
                            String value = td.getTextContent();
                            cmdWithArgs.add(value);
                        }
                    }
                    break;
                case Node.COMMENT_NODE:
                    cmdWithArgs = new ArrayList<String>(2);
                    cmdWithArgs.add("comment");
                    cmdWithArgs.add(tr.getNodeValue().trim());
                    break;
                default: // skip whitespace text.
                    continue;
                }
                Command command = commandFactory.newCommand(tri, cmdWithArgs);
                if (command instanceof Label) {
                    testCase.setLabelCommand((Label) command);
                } else if (command instanceof StartLoop) {
                    loopCommandStack.push((StartLoop) command);
                } else if (command instanceof EndLoop) {
                    StartLoop startLoop = loopCommandStack.pop();
                    startLoop.setEndLoop((EndLoop) command);
                    ((EndLoop) command).setStartLoop(startLoop);
                }
                testCase.addCommand(command);
            }
            return testCase;
        } catch (TransformerException e) {
            if (name == null)
                name = FilenameUtils.getBaseName(filename);
            return Binder.newErrorTestCase(name, new InvalidSeleneseException(e));
        }
    }
}
