package jp.vmi.selenium.selenese.highlight;

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
     * @param locator locator.
     * @param highlightStyle highlight style.
     */
    void highlight(String locator, HighlightStyle highlightStyle);

    /**
     * Unhighlight backed up styles.
     */
    void unhighlight();
}
