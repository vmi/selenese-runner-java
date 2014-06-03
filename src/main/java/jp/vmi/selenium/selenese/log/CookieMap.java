package jp.vmi.selenium.selenese.log;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.openqa.selenium.Cookie;

/**
 * Map of Cookies.
 */
@SuppressWarnings("serial")
public class CookieMap extends TreeMap<CookieKey, CookieValue> {

    /**
     * Add cookie information.
     *
     * @param cookie cookie.
     */
    public void add(Cookie cookie) {
        CookieKey key = new CookieKey(
            cookie.getName(),
            cookie.getPath(),
            cookie.getDomain());
        CookieValue value = new CookieValue(
            key,
            cookie.getValue(),
            cookie.getExpiry());
        put(key, value);
    }

    /**
     * Get all cookies as string.
     *
     * @return list of cookie string.
     */
    public List<String> allMessages() {
        List<String> list = new ArrayList<String>();
        for (CookieValue value : values())
            list.add(value.toString());
        return list;
    }

    /**
     * Get differential cookies as string.
     *
     * @param prev previous cookie map.
     * @return list of differential cookie string.
     */
    public List<String> diffMessages(CookieMap prev) {
        List<String> list = new ArrayList<String>();
        for (CookieValue value : values()) {
            CookieValue prevValue = prev.get(value.key);
            if (prevValue == null)
                list.add("[add] " + value);
            else if (!value.equals(prevValue))
                list.add("[mod] " + value);
        }
        for (CookieKey key : prev.keySet()) {
            if (!containsKey(key))
                list.add("[del] " + key);
        }
        return list;
    }
}
