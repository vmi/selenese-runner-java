package jp.vmi.selenium.runner.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Argument types.
 *
 * This definition is generated from ArgTypes.js by "tools/convert-ide-command.rb".
 */
public enum ArgTypes {

    /** no argument */
    NO_ARG("noArg", "no argument",
        "There is no argument."),

    /** css locator */
    CSS_LOCATOR("cssLocator", "css locator",
        "An element locator specified by css."),

    // BEGINNING OF ArgTypes (*** DO NOT EDIT FROM HERE TO "END OF ArgTypes" ***)

    /** alert text */
    ALERT_TEXT("alertText", "alert text",
        "text to check"),

    /** answer */
    ANSWER("answer", "answer",
        "The answer to give in response to the prompt pop-up."),

    /** attribute locator */
    ATTRIBUTE_LOCATOR("attributeLocator", "attribute locator",
        "An element locator followed by an @ sign and then the name of the attribute, e.g. \"foo@bar\"."),

    /** array variable name */
    ARRAY_VARIABLE_NAME("arrayVariableName", "array variable name",
        "The name of a variable containing a JavaScript array."),

    /** conditional expression */
    CONDITIONAL_EXPRESSION("conditionalExpression", "conditional expression",
        "JavaScript expression that returns a boolean result for use in control flow commands."),

    /** coord string */
    COORD("coord", "coord string",
        "Specifies the x,y position (e.g., - 10,20) of the mouse event relative to the element found from a locator."),

    /** expected value */
    EXPECTED_VALUE("expectedValue", "expected value",
        "The result you expect a variable to contain (e.g., true, false, or some other value)."),

    /** expression */
    EXPRESSION("expression", "expression",
        "The value you'd like to store."),

    /** form locator */
    FORM_LOCATOR("formLocator", "form locator",
        "An element locator for the form you want to submit."),

    /** window handle */
    HANDLE("handle", "window handle",
        "A handle representing a specific page (tab, or window)."),

    /** iterator variable name */
    ITERATOR_VARIABLE_NAME("iteratorVariableName", "iterator variable name",
        "The name of the variable used when iterating over a collection in a looping control flow command (e.g., for each)."),

    /** json */
    JSON("json", "json",
        "A string representation of a JavaScript object."),

    /** key sequence */
    KEY_SEQUENCE("keySequence", "key sequence",
        "A sequence of keys to type, can be used to send key strokes (e.g. ${KEY_ENTER})."),

    /** locator */
    LOCATOR("locator", "locator",
        "An element locator."),

    /** locator of drag destination object */
    LOCATOR_OF_DRAG_DESTINATION_OBJECT("locatorOfDragDestinationObject", "locator of drag destination object",
        "The locator of an element whose location (e.g., the center-most pixel within it) will be the point where locator of object to be dragged is dropped."),

    /** locator of object to be dragged */
    LOCATOR_OF_OBJECT_TO_BE_DRAGGED("locatorOfObjectToBeDragged", "locator of object to be dragged",
        "The locator of element to be dragged."),

    /** loop limit */
    LOOP_LIMIT("loopLimit", "loop limit",
        "An optional argument that specifies the maximum number of times a looping control flow command can execute. This protects against infinite loops. The defaults value is set to 1000."),

    /** message */
    MESSAGE("message", "message",
        "The message to print."),

    /** option */
    OPTION_LOCATOR("optionLocator", "option",
        "An option locator, typically just an option label (e.g. \"John Smith\")."),

    /** text */
    PATTERN("pattern", "text",
        "An exact string match. Support for pattern matching is in the works. See https://github.com/SeleniumHQ/selenium-ide/issues/141 for details."),

    /** region */
    REGION("region", "region",
        "Specify a rectangle with coordinates and lengths (e.g., \"x: 257, y: 300, width: 462, height: 280\")."),

    /** resolution */
    RESOLUTION("resolution", "resolution",
        "Specify a window resolution using WidthxHeight. (e.g., 1280x800)."),

    /** script */
    SCRIPT("script", "script",
        "The JavaScript snippet to run."),

    /** select locator */
    SELECT_LOCATOR("selectLocator", "select locator",
        "An element locator identifying a drop-down menu."),

    /** test case */
    TEST_CASE("testCase", "test case",
        "Test case name from the project."),

    /** text */
    TEXT("text", "text",
        "The text to use."),

    /** times */
    TIMES("times", "times",
        "The number of attempts a times control flow loop will execute the commands within its block."),

    /** url */
    URL("url", "url",
        "The URL to open (may be relative or absolute)."),

    /** value */
    VALUE("value", "value",
        "The value to input."),

    /** variable name */
    VARIABLE_NAME("variableName", "variable name",
        "The name of a variable without brackets."),

    /** wait time */
    WAIT_TIME("waitTime", "wait time",
        "The amount of time to wait (in milliseconds)."),

    /** xpath */
    XPATH("xpath", "xpath",
        "The xpath expression to evaluate."),

    // END OF ArgTypes
    ;

    private static final String PREFIX = "ArgTypes.";
    private static final Map<String, ArgTypes> lookupMap = new HashMap<>();

    static {
        for (ArgTypes value : values())
            lookupMap.put(value.id, value);
    }

    /**
     * Lookup ArgTypes from string.
     *
     * @param id id.
     * @return ArgTypes.
     */
    public static ArgTypes lookup(String id) {
        if (id == null)
            return NO_ARG;
        if (id.startsWith(PREFIX))
            id = id.substring(PREFIX.length());
        if ("option".equals(id))
            id = "optionLocator";
        ArgTypes type = lookupMap.get(id);
        if (type == null)
            throw new IllegalArgumentException("No such argument type: " + PREFIX + id);
        return type;
    }

    private final String id;
    private final String name;
    private final String description;

    ArgTypes(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Get id.
     *
     * @return id.
     */
    public String getId() {
        return id;
    }

    /**
     * Get name.
     *
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get description.
     *
     * @return description.
     */
    public String getDescription() {
        return description;
    }
}
