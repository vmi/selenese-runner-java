package jp.vmi.html.result;

/**
 * Test-suite summary.
 */
@SuppressWarnings("javadoc")
public class TestSuiteSummary {

    public int numTestTotal = 0;
    public int numTestPasses = 0;
    public int numTestFailures = 0;
    public int numCommandPasses = 0;
    public int numCommandFailures = 0;
    public int numCommandErrors = 0;

    public TestSuiteSummary merge(TestSuiteSummary summary) {
        numTestTotal += summary.numTestTotal;
        numTestPasses += summary.numTestPasses;
        numTestFailures += summary.numTestFailures;
        numCommandPasses += summary.numCommandPasses;
        numCommandFailures += summary.numCommandFailures;
        numCommandErrors += summary.numCommandErrors;
        return this;
    }
}
