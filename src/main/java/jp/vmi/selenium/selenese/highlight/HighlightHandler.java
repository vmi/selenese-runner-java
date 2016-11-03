package jp.vmi.selenium.selenese.highlight;

import jp.vmi.selenium.selenese.locator.Locator;

/**
 * Handle highlighting.
 */
public interface HighlightHandler {

    /**
     * Get locator highlighting.
     *
     * @return true if use locator highlighting.
     */
    boolean isHighlight();

    /**
     * Highlight and backup specified locator.
     *
     * @param ploc parsed locator.
     * @param highlightStyle highlight style.
     */
    void highlight(Locator ploc, HighlightStyle highlightStyle);

    /**
     * Unhighlight backed up styles.
     */
    void unhighlight();
}
