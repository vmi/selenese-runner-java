package jp.vmi.html.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.vmi.selenium.selenese.Selenese;
import jp.vmi.selenium.selenese.TestSuite;

/**
 * Test-suite tree for HTML result.
 */
public class TestSuiteTree {

    @SuppressWarnings("javadoc")
    public static class Node {

        public Node parent = null;
        public final TestSuite testSuite;
        public final TestSuiteSummary summary;
        public final List<Node> children = new ArrayList<>();

        private Node(Node parent, TestSuite testSuite, TestSuiteSummary summary) {
            this.parent = parent;
            this.testSuite = testSuite;
            this.summary = summary;
        }
    }

    private final Node root = new Node(null, null, null);
    private final Map<TestSuite, Node> map = new HashMap<>();

    /**
     * Get test-suite result summary.
     *
     * @param testSuite test-suite instance.
     * @return summary.
     */
    public TestSuiteSummary getSummary(TestSuite testSuite) {
        Node node = map.get(testSuite);
        return node != null ? node.summary : null;
    }

    /**
     * Add test-suite and it's summary.
     *
     * @param testSuite test-suite instance.
     * @param summary summary.
     */
    public void add(TestSuite testSuite, TestSuiteSummary summary) {
        Node node = new Node(root, testSuite, summary);
        for (Selenese selenese : testSuite.getSeleneseList()) {
            Node child = map.get(selenese);
            if (child != null) {
                child.parent.children.remove(child);
                child.parent = node;
                node.children.add(child);
            }
        }
        root.children.add(node);
        map.put(testSuite, node);
    }

    /**
     * Get root node list of tree.
     *
     * @return node list.
     */
    public List<Node> getRootNodeList() {
        return root.children;
    }
}
