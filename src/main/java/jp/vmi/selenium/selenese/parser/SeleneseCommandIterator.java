package jp.vmi.selenium.selenese.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jp.vmi.selenium.selenese.InvalidSeleneseException;

/**
 * Iterator and iterable of test case of Selenese format.
 */
public class SeleneseCommandIterator extends AbstractTestElementIterator<CommandEntry> implements CommandIterator {

    private static final AtomicLong sequence = new AtomicLong(0);

    private final String baseURL;
    private final NodeIterator nodeIterator;
    private CommandEntry next;

    private static String nextId() {
        return String.format("%08x", sequence.incrementAndGet());
    }

    /**
     * Constructor.
     *
     * @param filename selenese script file. (Don't use to open a file. It is used as a label and is used to generate filenames based on it)
     * @param document Test case DOM.
     * @param baseURL base URL.
     * @throws InvalidSeleneseException invalid selenese exception.
     */
    public SeleneseCommandIterator(String filename, Document document, String baseURL) throws InvalidSeleneseException {
        super(filename);
        setId(filename);
        this.baseURL = baseURL;
        String name = ParserUtils.getNameFromFilename(filename);
        try {
            name = XPathAPI.selectSingleNode(document, "//THEAD/TR/TD").getTextContent();
            Node tbody = XPathAPI.selectSingleNode(document, "//TBODY");
            NodeList trList = tbody.getChildNodes();
            this.nodeIterator = new NodeIterator(trList);
        } catch (DOMException | TransformerException e) {
            throw new InvalidSeleneseException(e, filename, getName());
        }
        setName(name);
        this.next = nextImpl();
    }

    @Override
    public String getBaseURL() {
        return baseURL;
    }

    private static String getTdString(Node td) {
        StringBuilder value = new StringBuilder();
        for (Node node : NodeIterator.each(td.getChildNodes())) {
            if ("BR".equals(node.getNodeName())) {
                value.append('\n');
            } else {
                short nodeType = node.getNodeType();
                if (nodeType == Node.TEXT_NODE || nodeType == Node.CDATA_SECTION_NODE)
                    value.append(node.getTextContent());
            }
        }
        return value.toString();
    }

    private CommandEntry nextImpl() {
        while (nodeIterator.hasNext()) {
            Node tr = nodeIterator.next();
            switch (tr.getNodeType()) {

            case Node.ELEMENT_NODE:
                List<String> cmdWithArgs = new ArrayList<>(3);
                for (Node td : NodeIterator.each(tr.getChildNodes())) {
                    if ("TD".equals(td.getNodeName()))
                        cmdWithArgs.add(getTdString(td));
                }
                String cmdName = cmdWithArgs.remove(0);
                String[] cmdArgs = cmdWithArgs.toArray(new String[cmdWithArgs.size()]);
                return CommandEntry.newInstance(nextId(), cmdName, cmdArgs);

            case Node.COMMENT_NODE:
                return CommandEntry.newInstance(nextId(), "comment", tr.getNodeValue().trim());

            default: // skip whitespace text.
                continue;
            }
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public CommandEntry next() {
        CommandEntry value = next;
        next = nextImpl();
        return value;
    }
}
