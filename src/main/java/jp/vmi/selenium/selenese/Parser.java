package jp.vmi.selenium.selenese;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.cyberneko.html.parsers.DOMParser;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.command.DummyHead;
import jp.vmi.selenium.selenese.command.EndLoop;
import jp.vmi.selenium.selenese.command.Label;
import jp.vmi.selenium.selenese.command.StartLoop;

public class Parser {

    private final Document docucment;
    private final String baseURI;
    private final String name;

    public static Iterable<Node> iterable(final NodeList n) {
        return new Iterable<Node>() {
            @Override
            public Iterator<Node> iterator() {
                return new Iterator<Node>() {
                    int index = 0;

                    @Override
                    public boolean hasNext() {
                        return index < n.getLength();
                    }

                    @Override
                    public Node next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return n.item(index++);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Parser(String input) throws InvalidSeleneseException {
        try {
            if (input.matches("[A-Za-z]:[/\\\\].*"))
                input = "file:///" + input;
            DOMParser parser = new DOMParser();
            parser.setEntityResolver(null);
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.parse(input);
            docucment = parser.getDocument();
            try {
                baseURI = XPathAPI.selectSingleNode(docucment, "/HTML/HEAD/LINK[@rel='selenium.base']/@href").getNodeValue();
            } catch (NullPointerException e) {
                throw new InvalidSeleneseException("no selenium.base link", e);
            }
            name = XPathAPI.selectSingleNode(docucment, "//THEAD/TR/TD").getTextContent();
        } catch (RuntimeException e) {
            throw e;
        } catch (TransformerException e) {
            throw new InvalidSeleneseException(e);
        } catch (SAXNotRecognizedException e) {
            throw new RuntimeException(e);
        } catch (SAXNotSupportedException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new InvalidSeleneseException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            for (Node tr : iterable(trList)) {
                tri++;
                List<String> cmdWithArgs = new ArrayList<String>(3);
                for (Node td : iterable(tr.getChildNodes())) {
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
