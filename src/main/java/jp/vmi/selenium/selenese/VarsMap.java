package jp.vmi.selenium.selenese;

import java.util.HashMap;

import org.apache.commons.text.StrLookup;
import org.apache.commons.text.StrSubstitutor;
import org.openqa.selenium.Keys;

import jp.vmi.selenium.selenese.utils.SeleniumUtils;

import static org.openqa.selenium.Keys.*;

/**
 * Variable Map.
 */
public class VarsMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

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
     */
    public String replaceVars(String expr) {
        StrSubstitutor s = new StrSubstitutor(new StrLookup<Object>() {
            @Override
            public String lookup(String key) {
                return SeleniumUtils.convertToString(get(key));
            }
        });
        return s.replace(expr);
    }

    /**
     * Replace variable reference to value for each strings.
     *
     * @param exprs expression strings.
     * @return replaced strings.
     */
    public String[] replaceVarsForArray(String[] exprs) {
        String[] result = new String[exprs.length];
        for (int i = 0; i < exprs.length; i++)
            result[i] = replaceVars(exprs[i]);
        return result;
    }
}
