package jp.vmi.selenium.selenese.result;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Test for Result.
 */
public class ResultTest {

    /**
     * update method test.
     */
    @Test
    public void updateResult() {
        Result result = new Success("test message");
        result.addErrorLog("test log");
        result = result.update(new Failure("new test message"));
        result.addErrorLog("new test log");

        assertThat(result.getMessage(), is("test message\nnew test message"));
        assertThat(result.getErrorLogs().get(0), is("test log"));
        assertThat(result.getErrorLogs().get(1), is("new test log"));
        assertThat(result.getErrorLogs().size(), is(2));
    }
}
