package jp.vmi.html.result;

import java.util.Locale;

import com.floreysoft.jmte.NamedRenderer;
import com.floreysoft.jmte.RenderFormatInfo;

import jp.vmi.selenium.selenese.utils.PathUtils;

/**
 * Index renderer for JMTE.
 */
public class RelativePathRenderer implements NamedRenderer {

    private final HtmlResult htmlResult;

    /**
     * Constructor.
     *
     * @param htmlResult HtmlResult object.
     */
    public RelativePathRenderer(HtmlResult htmlResult) {
        this.htmlResult = htmlResult;
    }

    @Override
    public RenderFormatInfo getFormatInfo() {
        return null;
    }

    @Override
    public String getName() {
        return "relpath";
    }

    @Override
    public Class<?>[] getSupportedClasses() {
        return new Class[] { Object.class };
    }

    @Override
    public String render(Object o, String format, Locale locale) {
        // NB: Relativize has URI escaped the path
        return PathUtils.relativize(htmlResult.getDir(), o.toString());
    }
}
