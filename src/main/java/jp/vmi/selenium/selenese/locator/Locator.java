package jp.vmi.selenium.selenese.locator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.selenium.SeleniumException;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Parsed locator.
 */
@SuppressWarnings("javadoc")
public class Locator {

    // locators.
    public static final String IDENTIFIER = "identifier";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DOM = "dom";
    public static final String XPATH = "xpath";
    public static final String LINK = "link";
    public static final String CSS = "css";

    // locators for frame.
    public static final String RELATIVE = "relative";
    public static final String INDEX = "index";
    public static final String RELATIVE_TOP = RELATIVE + "=top";
    public static final String RELATIVE_PARENT = RELATIVE + "=parent";

    /** Separator between locator and option locator. */
    public static final String OPTION_LOCATOR_SEPARATOR = "\0";

    private static final Pattern LOCATORS_RE = Pattern.compile("(\\w+)=(.*)|(document\\..*)|(//.*)");

    private static final int LOCATOR_TYPE = 1;
    private static final int LOCATOR_ARG = 2;
    private static final int DOM_LOCATOR = 3;
    private static final int XPATH_LOCATOR = 4;

    /** locator. */
    public final String locator;

    /** locator type. */
    public final String type;

    /** argument for locator type. */
    public final String arg;

    /** option locator. */
    public final String option;

    /** frame list. */
    public final Deque<Integer> frameIndexList = new ArrayDeque<>();

    private static String formatLocator(String locator, String option) {
        return (option == null) ? locator : locator + " (" + option + ")";
    }

    /**
     * Constructor.
     *
     * @param locator locator with option. (separated by OPTION_LOCATOR_SEPARATOR)
     */
    public Locator(String locator) {
        String[] pair = locator.split(OPTION_LOCATOR_SEPARATOR, 2);
        this.locator = pair[0];
        this.option = pair.length == 2 ? pair[1] : null;
        Matcher matcher = LOCATORS_RE.matcher(this.locator);
        if (matcher.matches()) {
            String type = matcher.group(LOCATOR_TYPE);
            String arg = matcher.group(LOCATOR_ARG);
            if (isNotEmpty(type)) {
                try {
                    this.type = type.toLowerCase();
                } catch (IllegalArgumentException e) {
                    throw new UnsupportedOperationException("Unknown locator type: " + formatLocator(this.locator, this.option), e);
                }
                this.arg = arg;
            } else if (isNotEmpty(matcher.group(DOM_LOCATOR))) {
                // start with "document."
                this.type = DOM;
                this.arg = this.locator;
            } else if (isNotEmpty(matcher.group(XPATH_LOCATOR))) {
                // start with "//"
                this.type = XPATH;
                this.arg = this.locator;
            } else {
                // not reached?
                throw new UnsupportedOperationException("Unknown locator type: " + formatLocator(this.locator, this.option));
            }
        } else {
            this.type = IDENTIFIER;
            this.arg = this.locator;
        }
    }

    public boolean isTypeRelative() {
        return RELATIVE.equals(type);
    }

    public boolean isTypeIndex() {
        return INDEX.equals(type);
    }

    public boolean isRelativeTop() {
        return RELATIVE_TOP.equals(locator);
    }

    public boolean isRelativeParent() {
        return RELATIVE_PARENT.equals(locator);
    }

    public int getIndex() {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new SeleniumException("Invalid \"" + type + "\" locator argument: " + arg, e);
        }
    }

    @Override
    public String toString() {
        return formatLocator(locator, option);
    }
}
