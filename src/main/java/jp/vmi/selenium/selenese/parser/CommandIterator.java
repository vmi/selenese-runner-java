package jp.vmi.selenium.selenese.parser;

/**
 * Test case iterator with iterable.
 */
public interface CommandIterator extends TestElementIterator<CommandEntry> {

    /**
     * Get test case base URL.
     *
     * @return base URL.
     */
    String getBaseURL();
}
