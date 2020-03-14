package jp.vmi.html.result;

import com.floreysoft.jmte.NamedRenderer;
import com.floreysoft.jmte.RenderFormatInfo;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Number with separator renderer for JMTE.
 */
public class NumberRenderer implements NamedRenderer {

    @Override
    public RenderFormatInfo getFormatInfo() {
        return null;
    }

    @Override
    public String getName() {
        return "n";
    }

    @Override
    public Class<?>[] getSupportedClasses() {
        return new Class[] { long.class, Long.class, int.class, Integer.class };
    }

    @Override
    public String render(Object o, String format, Locale locale, Map<String, Object> model) {
        long value = ((Number) o).longValue();
        return NumberFormat.getInstance(locale).format(value);
    }
}
