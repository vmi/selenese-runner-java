package jp.vmi.selenium.selenese.parser;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOM node list wrapper for iteration.
 */
public class NodeIterator implements Iterator<Node> {
    private final NodeList nodeList;
    private int index = 0;

    /**
     * Constructor.
     *
     * @param nodeList DOM node list.
     */
    public NodeIterator(NodeList nodeList) {
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

    /**
     * Wrap DOM the node list with Iterable.
     *
     * @param nodeList DOM node list.
     * @return Iterable.
     */
    public static Iterable<Node> each(final NodeList nodeList) {
        return new Iterable<Node>() {
            @Override
            public Iterator<Node> iterator() {
                return new NodeIterator(nodeList);
            }
        };
    }
}
