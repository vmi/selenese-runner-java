package jp.vmi.selenium.runner.model.side;

import java.util.List;

import jp.vmi.selenium.runner.model.ISuite;

/**
 * "suite" element of side format.
 */
@SuppressWarnings("javadoc")
public class SideSuite extends SideBase implements ISuite<SideTest, SideCommand> {

    private boolean isParallel;
    private int timeout;
    private List<SideTest> tests;

    public void setParallel(boolean isParallel) {
        this.isParallel = isParallel;
    }

    @Override
    public boolean isParallel() {
        return isParallel;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    public void setTests(List<SideTest> tests) {
        this.tests = tests;
    }

    @Override
    public List<SideTest> getTests() {
        return tests;
    }
}
