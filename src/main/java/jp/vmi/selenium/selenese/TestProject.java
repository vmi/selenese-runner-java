package jp.vmi.selenium.selenese;

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

    @Override
    public String toString() {
        return toStringImpl("TestProject");
    }
}
