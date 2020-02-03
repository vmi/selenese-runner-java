package jp.vmi.html.result;

import com.floreysoft.jmte.NamedRenderer;
import com.floreysoft.jmte.RenderFormatInfo;
import jp.vmi.selenium.selenese.utils.EscapeUtils;
import jp.vmi.selenium.selenese.utils.PathUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML escape renderer for JMTE.
 */
public class LogRenderer implements NamedRenderer {

    private static final Pattern RE = Pattern.compile("(\\[\\[ATTACHMENT\\|)(.*?)(\\]\\])|(" + EscapeUtils.HTML_RE.pattern() + ")");

    private static final int ATTACHMENT_PREFIX = 1;
    private static final int ATTACHMENT_PATH = 2;
    private static final int ATTACHMENT_SUFFIX = 3;
    private static final int SPECIALS = 4;

    private final HtmlResult htmlResult;

    /**
     * Constructor.
     *
     * @param htmlResult HtmlResult object.
     */
    public LogRenderer(HtmlResult htmlResult) {
        this.htmlResult = htmlResult;
    }

    @Override
    public RenderFormatInfo getFormatInfo() {
        return null;
    }

    @Override
    public String getName() {
        return "log";
    }

    @Override
    public Class<?>[] getSupportedClasses() {
        return new Class[] { Object.class };
    }

    @Override
    public String render(Object o, String format, Locale locale, Map<String, Object> model) {
        StringBuilder result = new StringBuilder();
        String s = o.toString();
        Matcher matcher = RE.matcher(s);
        int index = 0;
        while (matcher.find(index)) {
            int start = matcher.start();
            if (index < start)
                result.append(s, index, start);
            String prefix = matcher.group(ATTACHMENT_PREFIX);
            if (prefix != null) {
                String path = matcher.group(ATTACHMENT_PATH);
                String relPath = PathUtils.relativize(htmlResult.getDir(), path);
                result.append(prefix)
                    .append("<a href=\"").append(EscapeUtils.encodeUri(relPath)).append("\">")
                    .append(StringEscapeUtils.escapeHtml4(path))
                    .append("</a>")
                    .append(matcher.group(ATTACHMENT_SUFFIX));
            } else {
                result.append(EscapeUtils.HTML_ESC_MAP.get(matcher.group(SPECIALS)));
            }
            index = matcher.end();
        }
        if (index < s.length())
            result.append(s, index, s.length());
        return result.toString();
    }
}
