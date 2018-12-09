package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.runner.model.ArgType;

/**
 * Type of command arguments.
 */
public enum ArgumentType {
    /** Value not locator */
    VALUE(ArgType.VALUE),

    /** Locator */
    LOCATOR(ArgType.LOCATOR),

    /** Attribute locator (with '@attribute_name') */
    ATTRIBUTE_LOCATOR(ArgType.ATTRIBUTE_LOCATOR),

    /** CSS locator (implies "css=") */
    CSS_LOCATOR(ArgType.CSS_LOCATOR),

    /** Option locator */
    OPTION_LOCATOR(ArgType.OPTION_LOCATOR),

    /* end of enum list */;

    /** Argument type of Selenium IDE TNG */
    public final ArgType argType;

    ArgumentType(ArgType argType) {
        this.argType = argType;
    }
}
