package jp.vmi.selenium.runner.model.side;

import java.util.List;

/**
 * "suite" element of side format.
 */
@SuppressWarnings("javadoc")
public class SideSuite extends SideBase {

    private List<String> tests;

    public List<String> getTests() {
        return tests;
    }

    public void setTests(List<String> tests) {
        this.tests = tests;
    }
}
