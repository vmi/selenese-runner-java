package jp.vmi.selenium.selenese.locator;

/**
 * Parsed locator for option.
 */
public class OptionLocator {

    /** locator. */
    public final String locator;

    /** locator type. */
    public final String type;

    /** argument for locator type. */
    public final String arg;

    /**
     * Constructor.
     *
     * @param optionLocator option locator.
     */
    public OptionLocator(String optionLocator) {
        this.locator = optionLocator;
        int i = optionLocator.indexOf('=');
        if (i < 0) {
            this.type = "label";
            this.arg = optionLocator;
        } else {
            this.type = optionLocator.substring(0, i);
            this.arg = optionLocator.substring(i + 1);
        }
    }

    @Override
    public String toString() {
        return locator;
    }
}
