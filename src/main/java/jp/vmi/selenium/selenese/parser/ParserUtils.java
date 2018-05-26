package jp.vmi.selenium.selenese.parser;

import org.apache.commons.io.FilenameUtils;

/**
 * Utilities for parser.
 */
public final class ParserUtils {

    private ParserUtils() {
        // no operation.
    }

    /**
     * Get name from filename.
     *
     * @param filename filename.
     * @return name.
     */
    public static String getNameFromFilename(String filename) {
        return FilenameUtils.getBaseName(filename);
    }
}
