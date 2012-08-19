package hudson.tasks.junit;

import java.io.File;
import java.util.List;

/**
 * Bridge to SuiteResult.parse.
 */
public final class JenkinsSuiteResult {

    /**
     * Parse test-suite.
     *
     * @param file XML JUnit result file.
     * @return parsed data.
     */
    public static List<SuiteResult> parse(File file) {
        try {
            return SuiteResult.parse(file, true);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
