package jp.vmi.selenium.selenese;

import java.io.File;
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

    protected TestCaseParser(File file, Document document, String baseURL) throws InvalidSeleneseException {
        super(file, document);
        this.baseURL = baseURL;
    }

    @Override
    public Selenese parse(Runner runner) {
        String name = null;
        try {
            String baseURL = runner.getEffectiveBaseURL(this.baseURL);
            name = XPathAPI.selectSingleNode(docucment, "//THEAD/TR/TD").getTextContent();
            TestCase testCase = Binder.newTestCase(file, name, runner, baseURL);
            CommandFactory commandFactory = new CommandFactory(testCase.getProc());
            NodeList trList = XPathAPI.selectNodeList(docucment, "//TBODY/TR");
            Deque<StartLoop> loopCommandStack = new ArrayDeque<StartLoop>();
            int tri = 0;
            for (Node tr : each(trList)) {
                tri++;
                List<String> cmdWithArgs = new ArrayList<String>(3);
                for (Node td : each(tr.getChildNodes())) {
                    if ("TD".equals(td.getNodeName())) {
                        String value = td.getTextContent();
                        if (value.length() > 0)
                            cmdWithArgs.add(value);
                    }
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
                name = FilenameUtils.getBaseName(file.getName());
            return Binder.newErrorTestCase(name, new InvalidSeleneseException(e));
        }
    }
}
