package jp.vmi.html.result;

import com.floreysoft.jmte.NamedRenderer;
import com.floreysoft.jmte.RenderFormatInfo;

import java.util.Locale;
import java.util.Map;

/**
 * Index renderer for JMTE.
 */
public class IndexRenderer implements NamedRenderer {

    @Override
    public RenderFormatInfo getFormatInfo() {
        return null;
    }

    @Override
    public String getName() {
        return "i";
    }

    @Override
    public Class<?>[] getSupportedClasses() {
        return new Class[] { Number.class, int.class };
    }

    @Override
    public String render(Object o, String format, Locale locale, Map<String, Object> model) {
        if (((Number) o).intValue() >= 0)
            return o.toString();
        else
            return "-";
    }
}
