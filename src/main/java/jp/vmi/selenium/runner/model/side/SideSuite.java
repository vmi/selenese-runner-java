package jp.vmi.selenium.runner.model.side;

import java.util.ArrayList;
import java.util.List;

import jp.vmi.selenium.runner.model.ISuite;

/**
 * "suite" element of side format.
 */
@SuppressWarnings("javadoc")
public class SideSuite extends SideBase implements ISuite<SideTest, SideCommand> {

    private boolean isParallel = false;
    private int timeout = 300;
    private boolean isPersistSession = false;
    private List<SideTest> tests = null;

    public SideSuite(boolean isGen) {
        super(isGen);
    }

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

    @Override
    public boolean isPersistSession() {
        return isPersistSession;
    }

    public void setPersistSession(boolean isPersistSession) {
        this.isPersistSession = isPersistSession;
    }

    public void setTests(List<SideTest> tests) {
        this.tests = tests;
    }

    @Override
    public List<SideTest> getTests() {
        return tests;
    }

    public void addTest(SideTest test) {
        if (tests == null)
            tests = new ArrayList<>();
        tests.add(test);
    }
}
