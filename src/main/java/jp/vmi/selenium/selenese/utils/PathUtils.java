package jp.vmi.selenium.selenese.utils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Path utilities.
 */
public class PathUtils {

    private static final Pattern SEP_REGEX = Pattern.compile("[/\\\\]");
    private static final String SEP_REPL = Matcher.quoteReplacement(File.separator);
    private static final String PARENT_DIR = ".." + File.separator;

    private PathUtils() {
    }

    private static String normalizeInternal(String filename) {
        if (filename.startsWith(PARENT_DIR))
            filename = new File(filename).getAbsolutePath();
        String nfn = FilenameUtils.normalize(filename);
        if (nfn == null) {
            try {
                nfn = new File(filename).getCanonicalPath();
            } catch (IOException e) {
                throw new IllegalArgumentException("Filename normalization failed: " + filename, e);
            }
        }
        return nfn;
    }

    /**
     * Normalize filename separator.
     *
     * @param filename filename.
     * @return separator normalized filename.
     */
    public static String normalizeSeparator(String filename) {
        if (filename == null)
            return null;
        return SEP_REGEX.matcher(filename).replaceAll(SEP_REPL);
    }

    /**
     * Normalize filename.
     *
     * @param filename filename.
     * @return normalized filename.
     */
    public static String normalize(String filename) {
        if (filename == null)
            return null;
        return normalizeInternal(normalizeSeparator(filename));
    }

    /**
     * Concatinate filename.
     *
     * @param parent parent directory.
     * @param child child file or directory.
     * @return concatinated and normalized filename.
     */
    public static String concat(String parent, String child) {
        if (StringUtils.isEmpty(parent))
            return normalize(child);
        StringBuilder s = new StringBuilder(normalizeSeparator(parent));
        if (!parent.endsWith(File.separator))
            s.append(File.separatorChar);
        s.append(normalizeSeparator(child));
        return normalizeInternal(s.toString());
    }

    /**
     * Get relative path.
     *
     * @param from from path.
     * @param to to path.
     * @return relative path.
     */
    public static String relativize(String from, String to) {
        from = new File(normalize(from)).toURI().toASCIIString();
        to = new File(normalize(to)).toURI().toASCIIString();
        int prefixLen = StringUtils.getCommonPrefix(from, to).length();
        int level = StringUtils.countMatches(from.substring(prefixLen), "/");
        return StringUtils.repeat("../", level) + to.substring(prefixLen);
    }
}
