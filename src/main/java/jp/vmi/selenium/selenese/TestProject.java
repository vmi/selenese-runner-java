package jp.vmi.selenium.selenese;

import java.util.List;

import jp.vmi.selenium.selenese.inject.ExecuteTestSuite;
import jp.vmi.selenium.selenese.result.Result;

/**
 * test-project object for execution.
 */
public class TestProject extends TestSuite {

    private TestCaseMap testCaseMap = TestCaseMap.EMPTY;

    @Override
    public TestProject initialize(String filename, String name) {
        super.initialize(filename, name);
        return this;
    }

    @Override
    public Type getType() {
        return Type.TEST_PROJECT;
    }

    /**
     * Pull out the first element from the selenese list and unlink the parent from that element.
     *
     * @return selenese.
     */
    public Selenese pullOutFirstFromSeleneseList() {
        List<Selenese> list = getSeleneseList();
        if (list.isEmpty())
            return null;
        Selenese first = list.remove(0);
        if (first instanceof TestSuite)
            ((TestSuite) first).setParent(null);
        return first;
    }

    /**
     * Set test-case map.
     *
     * @param testCaseMap test-case map.
     */
    public void setTestCaseMap(TestCaseMap testCaseMap) {
        this.testCaseMap = testCaseMap;
    }

    /**
     * Get test-case map.
     *
     * @return test-case map.
     */
    public TestCaseMap getTestCaseMap() {
        return testCaseMap;
    }

    @ExecuteTestSuite
    @Override
    public Result execute(Selenese parent, Context context) {
        TestCaseMap prevTestCaseMap = context.getTestCaseMap();
        context.setTestCaseMap(testCaseMap);
        try {
            return super.execute(parent, context);
        } finally {
            context.setTestCaseMap(prevTestCaseMap);
        }
    }

    @Override
    public String toString() {
        return toStringImpl("TestProject");
    }
}
