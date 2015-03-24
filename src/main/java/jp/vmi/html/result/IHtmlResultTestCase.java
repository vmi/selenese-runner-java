package jp.vmi.html.result;

import jp.vmi.selenium.selenese.command.CommandList;
import jp.vmi.selenium.selenese.result.CommandResultList;

/**
 * TestCase interface for HTML result.
 */
public interface IHtmlResultTestCase extends IHtmlResultTestTarget {

    /**
     * Get filename of test-case.
     *
     * @return filename.
     */
    String getFilename();

    /**
     * Get command list.
     *
     * @return command list.
     */
    CommandList getCommandList();

    /**
     * Get test-case result list.
     *
     * @return test-case result list.
     */
    CommandResultList getResultList();
}
