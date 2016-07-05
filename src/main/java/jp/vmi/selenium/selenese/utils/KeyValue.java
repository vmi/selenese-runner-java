package jp.vmi.selenium.selenese.utils;

import java.util.Map.Entry;

/**
 * Key-value object.
 */
public class KeyValue implements Entry<String, String> {

    private final String key;
    private final String value;

    /**
     * Parse {@code key=value} string.
     *
     * @param pair {@code key=value} string or {@code value} string.
     * @param defaultKey default key if pair is {@code value} string.
     * @return key-value object.
     */
    public static KeyValue parse(String pair, String defaultKey) {
        int index = pair.indexOf('=');
        String key, value;
        if (index > 0) {
            key = pair.substring(0, index);
            value = pair.substring(index + 1);
        } else {
            key = defaultKey;
            value = pair;
        }
        return new KeyValue(key, value);
    }

    /**
     * Constructor.
     * @param key key.
     * @param value value.
     */
    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String setValue(String value) {
        throw new UnsupportedOperationException("This object is immutable");
    }
}
