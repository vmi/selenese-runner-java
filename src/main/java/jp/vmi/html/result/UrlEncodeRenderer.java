package jp.vmi.html.result;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.floreysoft.jmte.NamedRenderer;
import com.floreysoft.jmte.RenderFormatInfo;

/**
 * URL encode renderer for JMTE.
 */
public class UrlEncodeRenderer implements NamedRenderer {

    // This pattern equivalent to JavaScript encodeURI().
    // see: https://developer.mozilla.org/en/docs/Web/JavaScript/Reference/Global_Objects/encodeURI
    private static Pattern RE = Pattern.compile("[^;,/?:@&=+$A-Za-z0-9\\-_.!~*'()#]+");

    @Override
    public RenderFormatInfo getFormatInfo() {
        return null;
    }

    @Override
    public String getName() {
        return "u";
    }

    @Override
    public Class<?>[] getSupportedClasses() {
        return new Class[] { Object.class };
    }

    private StringBuilder encode(StringBuilder sb, String s) {
        for (byte b : s.getBytes(StandardCharsets.UTF_8))
            sb.append(String.format("%%%02x", b));
        return sb;
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
            encode(result, matcher.group());
            index = matcher.end();
        }
        if (index < s.length())
            result.append(s, index, s.length());
        return result.toString();
    }
}
