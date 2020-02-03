package jp.vmi.html.result;

import com.floreysoft.jmte.Engine;
import com.floreysoft.jmte.Renderer;
import jp.vmi.html.result.TestSuiteTree.Node;

import java.util.Locale;
import java.util.Map;

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
    public String render(Node node, Locale locale, Map<String, Object> model) {
        model.put("node", node);
        return engine.transform(template, model);
    }
}
