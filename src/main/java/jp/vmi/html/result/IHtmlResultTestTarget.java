package jp.vmi.html.result;

import jp.vmi.selenium.selenese.Selenese.Type;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Test target interface for HTML result.
 */
public interface IHtmlResultTestTarget {

    /**
     * Get selenese name.
     *
     * @return selenese name.
     */
    String getName();

    /**
     * Get selenese type.
     *
     * @return selenese type.
     */
    Type getType();

    /**
     * Get selenese result.
     *
     * @return selenese result.
     */
    Result getResult();
}
