package jp.vmi.html.result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.floreysoft.jmte.AnnotationProcessor;
import com.floreysoft.jmte.TemplateContext;
import com.floreysoft.jmte.token.AnnotationToken;

/**
 * write debug message to log for JMTE.
 */
public class JmteDebug implements AnnotationProcessor<String> {

    private static final Logger log = LoggerFactory.getLogger(JmteDebug.class);

    @Override
    public String getType() {
        return "debug";
    }

    @Override
    public String eval(AnnotationToken token, TemplateContext context) {
        String[] args = token.getArguments().split("\\s+");
        StringBuilder m = new StringBuilder("###");
        for (String arg : args) {
            if (arg.matches("\".*\"")) {
                arg = arg.substring(1, arg.length() - 1);
                m.append(' ').append(arg);
            } else {
                Object value = context.model.get(arg);
                String cname = value != null ? value.getClass().getSimpleName() : "null";
                m.append(" [").append(arg).append("=[").append(value).append("] (").append(cname).append(")");
            }
        }
        log.info(m.toString());
        return null;
    }
}
