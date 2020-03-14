package jp.vmi.html.result;

import com.floreysoft.jmte.NamedRenderer;
import com.floreysoft.jmte.RenderFormatInfo;
import jp.vmi.selenium.selenese.utils.EscapeUtils;

import java.util.Locale;
import java.util.Map;

/**
 * HTML escape renderer for JMTE.
 */
public class HtmlEscapeRenderer implements NamedRenderer {

    @Override
    public RenderFormatInfo getFormatInfo() {
        return null;
    }

    @Override
    public String getName() {
        return "h";
    }

    @Override
    public Class<?>[] getSupportedClasses() {
        return new Class[] { Object.class };
    }

    @Override
    public String render(Object o, String format, Locale locale, Map<String, Object> model) {
        return EscapeUtils.escapeHtml(o.toString(), true);
    }
}
