package jp.vmi.selenium.selenese.utils;

/**
 * XPath utilities.
 */
public class XPathUtils {

    /**
     * Append string literal in XPath context.
     *
     * @param xpath StringBuilder for building XPath.
     * @param s string literal.
     */
    public static void appendStringLiteral(StringBuilder xpath, String s) {
        if (s.indexOf('"') >= 0) {
            if (s.indexOf('\'') >= 0)
                xpath.append("concat(\"").append(s.replace("\"", "\",'\"',\"")).append("\")");
            else
                xpath.append('\'').append(s).append('\'');
        } else {
            xpath.append('"').append(s).append('"');
        }
    }
}
