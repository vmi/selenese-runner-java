package jp.vmi.selenium.selenese;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.command.DummyHead;
import jp.vmi.selenium.selenese.command.EndLoop;
import jp.vmi.selenium.selenese.command.Label;
import jp.vmi.selenium.selenese.command.StartLoop;

public class TestCaseParser extends Parser {

    private final String baseURI;
    private final String name;

    public TestCaseParser(Document document, String baseURI) throws InvalidSeleneseException {
        super(document);
        this.baseURI = baseURI;
        try {
            this.name = XPathAPI.selectSingleNode(docucment, "//THEAD/TR/TD").getTextContent();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidSeleneseException(e);
        }

    }

    public Command parse(WebDriverCommandProcessor proc, Context context) {
        try {
            CommandFactory commandFactory = new CommandFactory(proc);
            NodeList trList = XPathAPI.selectNodeList(docucment, "//TBODY/TR");
            Command head = new DummyHead();
            Command prev = head;
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
                    context.setLabelCommand((Label) command);
                } else if (command instanceof StartLoop) {
                    loopCommandStack.push((StartLoop) command);
                } else if (command instanceof EndLoop) {
                    StartLoop startLoop = loopCommandStack.pop();
                    startLoop.setEndLoop((EndLoop) command);
                    ((EndLoop) command).setStartLoop(startLoop);
                }
                prev = prev.setNext(command);
            }
            return head.next(null);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public Document getDocument() {
        return docucment;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public String getName() {
        return name;
    }

}
