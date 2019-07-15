package jp.vmi.selenium.selenese;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Map of test-case.
 */
public class TestCaseMap extends AbstractMap<String, TestCase> {

    /**
     * Empty map of test-case.
     */
    public static final TestCaseMap EMPTY = new TestCaseMap(false);

    private final Map<String, TestCase> nameMap;
    private final Map<String, TestCase> idMap;

    /**
     * Constructor.
     */
    public TestCaseMap() {
        this(true);
    }

    private TestCaseMap(boolean isWritable) {
        if (isWritable) {
            this.nameMap = new LinkedHashMap<>();
            this.idMap = new LinkedHashMap<>();
        } else {
            this.nameMap = Collections.emptyMap();
            this.idMap = Collections.emptyMap();
        }
    }

    /**
     * Add test-case.
     *
     * @param testCase test-case.
     */
    public void put(TestCase testCase) {
        String name = testCase.getName();
        String id = testCase.getId();
        nameMap.put(name, testCase);
        idMap.put(id, testCase);
    }

    @Override
    public TestCase put(String name, TestCase testCase) {
        TestCase result = nameMap.put(name, testCase);
        idMap.put(testCase.getId(), testCase);
        return result;
    }

    /**
     * Get test-case by id.
     *
     * @param id test-case id.
     * @return test-case or null.
     */
    public TestCase getById(String id) {
        return idMap.get(id);
    }

    @Override
    public Set<Entry<String, TestCase>> entrySet() {
        return nameMap.entrySet();
    }
}
