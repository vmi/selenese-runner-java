package jp.vmi.selenium.selenese.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openqa.selenium.Platform.*;

/**
 * Command line utilities.
 */
public final class CommandLineUtils {

    private CommandLineUtils() {
    }

    private static final Pattern RE_W = Pattern.compile("\\G(?:%+|\"|[^%\"]+)");
    private static final Pattern RE_U = Pattern.compile("\\G(?:'|[^']+)");

    // abc      -> "abc"
    // a\b\c    -> "a\b\c"
    // a\b\c\   -> "a\b\c\\"
    // %abc%    -> %"abc"%
    // %a\b\c\% -> %"a\b\c\\"%

    /**
     * Add command line escaped argument to StringBuilder on Windows.
     *
     * <table>
     * <caption>Escape rules on Windows</caption>
     * <thead>
     * <tr> <th>Plain</th>    <th></th>       <th>Escaped</th>               </tr>
     * </thead>
     * <tbody>
     * <tr> <td>abc</td>      <td>&rArr;</td> <td>&quot;abc&quot;</td>       </tr>
     * <tr> <td>a\b\c</td>    <td>&rArr;</td> <td>&quot;a\b\c&quot;</td>     </tr>
     * <tr> <td>a\b\c\</td>   <td>&rArr;</td> <td>&quot;a\b\c\\&quot;</td>   </tr>
     * <tr> <td>%abc%</td>    <td>&rArr;</td> <td>%&quot;abc&quot;%</td>     </tr>
     * <tr> <td>%a\b\c\%</td> <td>&rArr;</td> <td>%&quot;a\b\c\\&quot;%</td> </tr>
     * </tbody>
     * </table>
     *
     * @param buffer StringBuilder.
     * @param arg argument.
     */
    public static void addEscapedArgumentOnWindows(StringBuilder buffer, String arg) {
        if (buffer.length() > 0)
            buffer.append(' ');
        boolean quoted = false;
        Matcher matcher = RE_W.matcher(arg);
        while (matcher.find()) {
            String matched = matcher.group();
            char c = matched.charAt(0);
            if (c == '%') {
                if (quoted) {
                    if (buffer.charAt(buffer.length() - 1) == '\\')
                        buffer.append('\\');
                    buffer.append('"').append(matched);
                    quoted = false;
                } else {
                    buffer.append(matched);
                }
            } else {
                if (!quoted) {
                    buffer.append('"');
                    quoted = true;
                }
                if (c == '"')
                    buffer.append('\\');
                buffer.append(matched);
            }
        }
        if (quoted) {
            if (buffer.charAt(buffer.length() - 1) == '\\')
                buffer.append('\\');
            buffer.append('"');
        }
    }

    /**
     * Add command line escaped argument to StringBuilder on Unix.
     *
     * <table>
     * <caption>Escape rules on Unix</caption>
     * <thead>
     * <tr> <th>Plain</th> <th></th>       <th>Escaped</th>       </tr>
     * </thead>
     * <tbody>
     * <tr> <td>abc</td>   <td>&rArr;</td> <td>'abc'</td>         </tr>
     * <tr> <td>a'b'c</td> <td>&rArr;</td> <td>'a'\''b'\''c'</td> </tr>
     * <tr> <td>'abc'</td> <td>&rArr;</td> <td>\''abc'\'</td>     </tr>
     * </tbody>
     * </table>
     *
     * @param buffer StringBuilder.
     * @param arg argument.
     */
    public static void addEscapedArgumentOnUnix(StringBuilder buffer, String arg) {
        if (buffer.length() > 0)
            buffer.append(' ');
        boolean quoted = false;
        Matcher matcher = RE_U.matcher(arg);
        while (matcher.find()) {
            String matched = matcher.group();
            char c = matched.charAt(0);
            if (c == '\'') {
                if (quoted) {
                    buffer.append('\'');
                    quoted = false;
                }
                buffer.append("\\'");
            } else {
                if (!quoted) {
                    buffer.append('\'');
                    quoted = true;
                }
                buffer.append(matched);
            }
        }
        if (quoted)
            buffer.append('\'');
    }

    /**
     * Escape command line arguments.
     *
     * @param args command line arguments.
     * @return escaped string of command line arguments.
     */
    public static String espaceCommandLineArgs(String[] args) {
        boolean isWindows = getCurrent().is(WINDOWS);
        StringBuilder buffer = new StringBuilder();
        for (String arg : args) {
            if (isWindows)
                addEscapedArgumentOnWindows(buffer, arg);
            else
                addEscapedArgumentOnUnix(buffer, arg);
        }
        return buffer.toString();
    }
}
