package jp.vmi.html.result;

import java.util.Locale;

import com.floreysoft.jmte.NamedRenderer;
import com.floreysoft.jmte.RenderFormatInfo;

import jp.vmi.selenium.selenese.utils.EscapeUtils;

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
    public String render(Object o, String format, Locale locale) {
        return EscapeUtils.escapeHtml(o.toString(), true);
    }
}
