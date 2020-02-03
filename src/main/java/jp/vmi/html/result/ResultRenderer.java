package jp.vmi.html.result;

import com.floreysoft.jmte.Renderer;
import jp.vmi.selenium.selenese.result.Result;

import java.util.Locale;
import java.util.Map;

/**
 * Command result renderer for JMTE.
 */
public class ResultRenderer implements Renderer<Result> {

    @Override
    public String render(Result result, Locale locale, Map<String, Object> model) {
        return result.getClass().getSimpleName().toLowerCase();
    }
}
