package jp.vmi.selenium.selenese.command;

/**
 * Type of command arguments.
 */
public enum ArgumentType {
    /** Value not locator */
    VALUE,

    /** Locator */
    LOCATOR,

    /** Attribute locator (with '@attribute_name') */
    ATTRIBUTE_LOCATOR,

    /** CSS locator (implies "css=") */
    CSS_LOCATOR,

    /** Option locator */
    OPTION_LOCATOR
}
