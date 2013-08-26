package jp.vmi.html.result;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.floreysoft.jmte.NamedRenderer;
import com.floreysoft.jmte.RenderFormatInfo;

/**
 * HTML escape renderer for JMTE.
 */
public class HtmlEscapeRenderer implements NamedRenderer {

    private static final Pattern RE = Pattern.compile("[<>&\"'\\\\]|\r?\n|\r");
    private static final Map<String, String> ESC_MAP = new HashMap<String, String>();

    static {
        ESC_MAP.put("<", "&lt;");
        ESC_MAP.put(">", "&gt;");
        ESC_MAP.put("&", "&amp;");
        ESC_MAP.put("\"", "&quot;");
        ESC_MAP.put("'", "&#39;");
        ESC_MAP.put("\\", "&#92;");
        ESC_MAP.put("\r\n", "<br>\r\n");
        ESC_MAP.put("\n", "<br>\n");
        ESC_MAP.put("\r", "<br>\r");
    }

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
        StringBuilder result = new StringBuilder();
        String s = o.toString();
        Matcher matcher = RE.matcher(s);
        int index = 0;
        while (matcher.find(index)) {
            int start = matcher.start();
            if (index < start)
                result.append(s, index, start);
            result.append(ESC_MAP.get(matcher.group()));
            index = matcher.end();
        }
        if (index < s.length())
            result.append(s, index, s.length());
        return result.toString();
    }
}
