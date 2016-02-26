package jp.vmi.selenium.selenese.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * OptionEntry accessor.
 */
public class OptionEntry {

    @SuppressWarnings("javadoc")
    public final String optionName;
    @SuppressWarnings("javadoc")
    public final Class<?> type;
    private final Method getter;
    private final Method setter;
    private final Method adder;

    /**
     * Convert option name to capitalized property name.
     *
     * @param name option name such as "word-word-word".
     * @return capitalized property name such as "WordWordWord".
     */
    private static String propName(String name) {
        StringBuilder methodName = new StringBuilder(name.length());
        for (String word : name.split("-"))
            methodName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        return methodName.toString();
    }

    /**
     * Create OptionEntry object from bean class.
     *
     * @param beanClass bean class.
     * @param optionName option name.
     * @return OptionEntry object.
     */
    public static OptionEntry newInstance(Class<?> beanClass, String optionName) {
        if (optionName.toLowerCase().equals("class"))
            throw new IllegalArgumentException("Invalid option name: " + optionName);
        String propName = propName(optionName);
        try {
            Method getter;
            try {
                getter = beanClass.getMethod("get" + propName);
            } catch (NoSuchMethodException e) {
                getter = beanClass.getMethod("is" + propName);
            }
            Class<?> type = getter.getReturnType();
            Method setter = null;
            try {
                setter = beanClass.getMethod("set" + propName, type);
            } catch (NoSuchMethodException e) {
                // no operation.
            }
            Method adder = null;
            if (type.isArray()) {
                try {
                    adder = beanClass.getMethod("add" + propName, type.getComponentType());
                } catch (NoSuchMethodException e) {
                    // no operation.
                }
            }
            return new OptionEntry(optionName, type, getter, setter, adder);
        } catch (SecurityException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Invalid option name: " + optionName);
        }
    }

    private OptionEntry(String optionName, Class<?> type, Method getter, Method setter, Method adder) {
        this.optionName = optionName;
        this.type = type;
        this.getter = getter;
        this.setter = setter;
        this.adder = adder;
    }

    /**
     * Get option value.
     *
     * @param <T> type of option value.
     * @param obj object.
     * @return option value.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Object obj) {
        try {
            return (T) getter.invoke(obj);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("Invalid option value", e);
        }
    }

    /**
     * Set option value.
     *
     * @param obj object.
     * @param value option value.
     */
    public void set(Object obj, Object value) {
        if (setter == null)
            throw new UnsupportedOperationException("set " + optionName);
        try {
            setter.invoke(obj, value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException(String.format("Cannot assign: (%s) %s <- (%s) %s", type, optionName, value.getClass(), value), e);
        }
    }

    /**
     * Add option value.
     *
     * @param obj object.
     * @param value option value item.
     */
    public void add(Object obj, Object value) {
        if (adder == null)
            throw new UnsupportedOperationException("add " + optionName);
        try {
            adder.invoke(obj, value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException(String.format("Cannot assign: %s", value), e);
        }
    }
}
