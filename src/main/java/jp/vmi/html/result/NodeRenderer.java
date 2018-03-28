package jp.vmi.html.result;

import java.util.HashMap;
import java.util.Locale;

import com.floreysoft.jmte.Engine;
import com.floreysoft.jmte.Renderer;

import jp.vmi.html.result.TestSuiteTree.Node;

/**
 * Test-suite tree node renderer for JMTE.
 */
public class NodeRenderer implements Renderer<Node> {

    private final Engine engine;
    private final String template;

    /**
     * Constructor.
     *
     * @param engine JMTE engine.
     * @param template template for node.
     */
    public NodeRenderer(Engine engine, String template) {
        this.engine = engine;
        this.template = template;
    }

    @Override
    public String render(Node node, Locale locale) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("node", node);
        return engine.transform(template, model);
    }
}
