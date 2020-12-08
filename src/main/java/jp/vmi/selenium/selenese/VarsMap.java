package jp.vmi.selenium.selenese;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.Keys;

import com.google.gson.Gson;

import jp.vmi.selenium.selenese.utils.SeleniumUtils;

import static org.openqa.selenium.Keys.*;

/**
 * Variable Map.
 */
public class VarsMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    private static final Pattern SCRIPT_RE = Pattern.compile("(?<quote>[\"'`]?)\\$\\{(?<name>\\w+)\\}\\k<quote>", Pattern.DOTALL);
    private static final Pattern STRING_RE = Pattern.compile("\\$\\{(?<name>.+?)\\}", Pattern.DOTALL);

    /**
     * Constructor.
     */
    public VarsMap() {
        super();

        // see: ide/main/src/content/selenium-runner.js on Selenium repos.
        put("space", " ");
        put("nbsp", "\u00A0");

        for (Keys key : Keys.values())
            put("KEY_" + key.name(), key.toString());

        sendKeysAliases(BACK_SPACE, "BACKSPACE", "BKSP");
        sendKeysAliases(CONTROL, "CTRL");
        sendKeysAliases(ESCAPE, "ESC");
        sendKeysAliases(PAGE_UP, "PGUP");
        sendKeysAliases(PAGE_DOWN, "PGDN");
        sendKeysAliases(INSERT, "INS");
        sendKeysAliases(DELETE, "DEL");
        sendKeysAliases(NUMPAD0, "N0"); // number pad keys
        sendKeysAliases(NUMPAD1, "N1");
        sendKeysAliases(NUMPAD2, "N2");
        sendKeysAliases(NUMPAD3, "N3");
        sendKeysAliases(NUMPAD4, "N4");
        sendKeysAliases(NUMPAD5, "N5");
        sendKeysAliases(NUMPAD6, "N6");
        sendKeysAliases(NUMPAD7, "N7");
        sendKeysAliases(NUMPAD8, "N8");
        sendKeysAliases(NUMPAD9, "N9");
        sendKeysAliases(MULTIPLY, "MUL");
        sendKeysAliases(ADD, "PLUS");
        sendKeysAliases(SEPARATOR, "SEP");
        sendKeysAliases(SUBTRACT, "MINUS");
        sendKeysAliases(DECIMAL, "PERIOD");
        sendKeysAliases(DIVIDE, "DIV");

    }

    private void sendKeysAliases(Keys keys, String... aliases) {
        for (String alias : aliases)
            put("KEY_" + alias, keys.toString());
    }

    /**
     * Replace variable reference to value.
     *
     * @param expr expression string.
     * @return replaced string.
     *
     * @deprecated use {@link #replaceVars(boolean, String)} instead.
     */
    @Deprecated
    public String replaceVars(String expr) {
        return replaceVars(false, expr);
    }

    /**
     * Replace variable reference to value.
     *
     * @param isScript true if expr is ECMA script string.
     * @param expr expression string.
     * @return replaced string.
     */
    public String replaceVars(boolean isScript, String expr) {
        if (!expr.contains("${"))
            return expr;
        StringBuilder result = new StringBuilder();
        Pattern re = isScript ? SCRIPT_RE : STRING_RE;
        Matcher matcher = re.matcher(expr);
        int prevEnd = 0;
        while (matcher.find(prevEnd)) {
            int nextStart = matcher.start();
            if (prevEnd < nextStart)
                result.append(expr.substring(prevEnd, nextStart));
            String name = matcher.group("name");
            if (containsKey(name)) {
                Object rawValue = get(name);
                if (isScript)
                    result.append(new Gson().toJson(rawValue));
                else
                    result.append(SeleniumUtils.convertToString(rawValue));
            } else {
                result.append(matcher.group());
            }
            prevEnd = matcher.end();
        }
        if (prevEnd < expr.length())
            result.append(expr.substring(prevEnd));
        return result.toString();
    }

    /**
     * Replace variable reference to value for each strings.
     *
     * @param exprs expression strings.
     * @return replaced strings.
     *
     * @deprecated use {@link #replaceVars(boolean, String)} sith {@link java.util.Arrays#stream(Object[])} instaed.
     */
    @Deprecated
    public String[] replaceVarsForArray(String[] exprs) {
        String[] result = new String[exprs.length];
        for (int i = 0; i < exprs.length; i++)
            result[i] = replaceVars(exprs[i]);
        return result;
    }
}
