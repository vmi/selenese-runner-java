package jp.vmi.selenium.selenese.parser;

/**
 * Test case entry in test suite.
 */
public abstract class TestElementEntry {

    /** element id */
    public final String id;

    /** element name */
    public final String name;

    /**
     * Constructor.
     *
     * @param id element id.
     * @param name element name.
     */
    public TestElementEntry(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
