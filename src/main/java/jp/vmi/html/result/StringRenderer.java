package jp.vmi.html.result;

import java.util.Locale;

import com.floreysoft.jmte.NamedRenderer;
import com.floreysoft.jmte.RenderFormatInfo;

/**
 * String renderer for JMTE.
 */
public class StringRenderer implements NamedRenderer {

    @Override
    public RenderFormatInfo getFormatInfo() {
        return null;
    }

    @Override
    public String getName() {
        return "s";
    }

    @Override
    public Class<?>[] getSupportedClasses() {
        return new Class[] { Object.class };
    }

    @Override
    public String render(Object o, String format, Locale locale) {
        return o.toString();
    }
}
