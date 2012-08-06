package org.apache.tools.ant.taskdefs.optional.junit;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class XMLJUnitResultFormatterTest {

    XMLJUnitResultFormatter formatter;

    @Before
    public void setup() {
        formatter = new XMLJUnitResultFormatter();
    }

    @Test
    public void test() {
        JUnitTest suite = new JUnitTest("testsuite");
        formatter.setOutput(System.out);
        formatter.startTestSuite(suite);

        TestCase test = new TestCase("testcase") {
        };
        formatter.startTest(test);
        formatter.addError(test, new NullPointerException());
        formatter.endTest(test);
        test = new TestCase("testcase2") {
        };
        formatter.startTest(test);
        formatter.endTest(test);

        formatter.endTestSuite(suite);

    }
}
