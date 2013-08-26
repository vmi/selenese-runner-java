package jp.vmi.html.result;

import java.util.Locale;

import com.floreysoft.jmte.Renderer;

import jp.vmi.selenium.selenese.result.Result;

/**
 * Command result renderer for JMTE.
 */
public class ResultRenderer implements Renderer<Result> {

    @Override
    public String render(Result result, Locale locale) {
        return result.getClass().getSimpleName().toLowerCase();
    }
}
