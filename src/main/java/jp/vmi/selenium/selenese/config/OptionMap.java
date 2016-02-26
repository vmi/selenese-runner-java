package jp.vmi.selenium.selenese.config;

import java.util.HashMap;

/**
 * Option Map.
 */
public class OptionMap extends HashMap<String, OptionEntry> {

    private static final long serialVersionUID = 1L;

    private final Object bean;

    /**
     * Constructor.
     *
     * @param bean Option bean.
     */
    public OptionMap(Object bean) {
        this.bean = bean;
    }

    @Override
    public OptionEntry get(Object key) {
        OptionEntry value = super.get(key);
        if (value == null) {
            value = OptionEntry.newInstance(bean.getClass(), (String) key);
            put((String) key, value);
        }
        return value;
    }
}
