package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.runner.model.ArgTypes;

/**
 * Type of command arguments.
 */
public enum ArgumentType {
    /** Value not locator */
    VALUE(ArgTypes.VALUE),

    /** Locator */
    LOCATOR(ArgTypes.LOCATOR),

    /** Attribute locator (with '@attribute_name') */
    ATTRIBUTE_LOCATOR(ArgTypes.ATTRIBUTE_LOCATOR),

    /** CSS locator (implies "css=") */
    CSS_LOCATOR(ArgTypes.CSS_LOCATOR),

    /** Option locator */
    OPTION_LOCATOR(ArgTypes.OPTION_LOCATOR),

    /* end of enum list */;

    /** Argument type of Selenium IDE TNG */
    public final ArgTypes argType;

    ArgumentType(ArgTypes argType) {
        this.argType = argType;
    }
}
