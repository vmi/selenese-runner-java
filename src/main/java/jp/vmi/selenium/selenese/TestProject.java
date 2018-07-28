package jp.vmi.selenium.selenese;

import java.util.List;

/**
 * test-project object for execution.
 */
public class TestProject extends TestSuite {

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

    @Override
    public String toString() {
        return toStringImpl("TestProject");
    }
}
