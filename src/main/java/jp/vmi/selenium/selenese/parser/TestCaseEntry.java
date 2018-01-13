package jp.vmi.selenium.selenese.parser;

/**
 * Test case entry in test suite.
 */
public class TestCaseEntry extends TestElementEntry {

    /** Is the test case embedded in test suite? */
    public final boolean isEmbedded;

    /**
     * Constructor.
     *
     * @param isEmbedded Is the test case embedded in test suite?
     * @param id test case id.
     * @param name test case nam.
     */
    public TestCaseEntry(boolean isEmbedded, String id, String name) {
        super(id, name);
        this.isEmbedded = isEmbedded;
    }
}
