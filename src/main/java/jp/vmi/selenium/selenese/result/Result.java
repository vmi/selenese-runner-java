package jp.vmi.selenium.selenese.result;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import jp.vmi.selenium.selenese.Selenese;

/**
 * Result of command execution.
 */
public abstract class Result implements Comparable<Result> {

    /**
     * Result Level.
     */
    @SuppressWarnings("javadoc")
    public static enum Level {
        UNEXECUTED(-1, 0, 5),
        SUCCESS(0, 0, 0),
        WARNING(1, 0, 2),
        FAILURE(2, 3, 3),
        ERROR(3, 3, 4),
        MAX_TIME_EXCEEDED(4, 3, 6),
        FATAL(5, 70, 70), // EX_SOFTWARE in sysexits.h
        USAGE(6, 64, 64), // EX_USAGE in sysexits.h
        ;

        public final int value;
        public final int exitCode;
        public final int strictExitCode;

        private Level(int value, int exitCode, int strictExitCode) {
            this.value = value;
            this.exitCode = exitCode;
            this.strictExitCode = strictExitCode;
        }
    }

    private static final Pattern SKIP_RE = Pattern.compile("ByGuice"
        + "|^com\\.google\\.inject\\."
        + "|^java\\.lang\\.reflect\\."
        + "|^sun\\.reflect\\."
        + "|^org\\.junit\\."
        + "|^org\\.eclipse\\.jdt\\.");

    private final String message;
    private List<Entry<Selenese, Result>> childResults = null;

    /**
     * Constructor.
     *
     * @param message result message.
     */
    public Result(String message) {
        this.message = message;
    }

    /**
     * Constructor.
     *
     * @param prefix prefix of message.
     * @param message result massage.
     */
    public Result(String prefix, String message) {
        this.message = prefix + ": " + message;
    }

    /**
     * Constructor.
     *
     * @param prefix prefix of message.
     * @param e Exception.
     */
    public Result(String prefix, Exception e) {
        this.message = generateExceptionMessage(prefix, e);
    }

    /**
     * Constructor.
     *
     * @param prefix prefix of message.
     * @param message result massage.
     * @param e Exception.
     */
    public Result(String prefix, String message, Exception e) {
        StringBuilder result = new StringBuilder(prefix).append(": ").append(message).append(" - ");
        this.message = generateExceptionMessage(result, e);
    }

    protected Result(Result childResult) {
        this.message = childResult.message;
    }

    protected Result newUpdatedResult(Result targetResult) {
        try {
            Constructor<? extends Result> c = targetResult.getClass().getConstructor(Result.class);
            Result newResult = c.newInstance(targetResult);
            newResult.childResults = childResults;
            return newResult;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException("Cannot construct " + getClass() + " with Result", e);
        }
    }

    /**
     * Get exception message for result.
     *
     * @param prefix prefix of message.
     * @param e exception.
     * @return message.
     */
    protected String generateExceptionMessage(String prefix, Exception e) {
        return generateExceptionMessage(new StringBuilder(prefix).append(": "), e);
    }

    /**
     * Generate exception message.
     *
     * @param result message buffer.
     * @param e exception.
     * @return message.
     */
    protected String generateExceptionMessage(StringBuilder result, Exception e) {
        String msg = e.getMessage();
        if (msg != null)
            result.append(e.getClass().getSimpleName()).append(" - ").append(msg);
        else
            result.append(e.getClass().getName());
        result.append(" (");
        boolean sep = false;
        for (StackTraceElement stackTrace : e.getStackTrace()) {
            String className = stackTrace.getClassName();
            if (className == null || SKIP_RE.matcher(className).find())
                continue;
            if (sep)
                result.append(" / ");
            result.append(className.replaceFirst("^.*\\.", ""))
                .append('.')
                .append(stackTrace.getMethodName());
            String fileName = stackTrace.getFileName();
            if (fileName != null) {
                result.append('(').append(fileName);
                int lineNumber = stackTrace.getLineNumber();
                if (lineNumber >= 0)
                    result.append(':').append(lineNumber);
                result.append(')');
            }
            sep = true;
        }
        result.append(")");
        return result.toString();
    }

    /**
     * Get result level.
     *
     * @return result level.
     */
    public abstract Level getLevel();

    /**
     * Get result message.
     *
     * @return result message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Is result of success?
     *
     * @return true if this is Success.
     */
    public boolean isSuccess() {
        return getLevel() == Level.SUCCESS;
    }

    /**
     * Is command interrupted?
     *
     * @return true if command is interrupted.
     */
    public boolean isAborted() {
        return getLevel().value >= Level.FAILURE.value;
    }

    /**
     * Is command failed?
     *
     * @return true if command is failed.
     */
    public boolean isFailed() {
        return getLevel().value >= Level.WARNING.value;
    }

    /**
     * Update total result.
     *
     * @param newResult new result.
     * @return updated total result.
     *
     * @deprecated use {@code #updateWithChildResult(Selenese, Result)} instead.
     */
    @Deprecated
    public Result update(Result newResult) {
        return newResult.getLevel().value > this.getLevel().value ? newResult : this;
    }

    /**
     * Update total result with child result.
     *
     * @param childSource child result source.
     * @param childResult child result.
     * @return updated total result.
     */
    public Result updateWithChildResult(Selenese childSource, Result childResult) {
        Result newResult;
        if (childResult.getLevel().value > getLevel().value)
            newResult = newUpdatedResult(childResult);
        else
            newResult = this;
        if (newResult.childResults == null)
            newResult.childResults = new ArrayList<>();
        newResult.childResults.add(new SimpleEntry<Selenese, Result>(childSource, childResult));
        return newResult;
    }

    /**
     * Get child results.
     *
     * @return child results.
     */
    public List<Entry<Selenese, Result>> getChildResults() {
        if (childResults == null)
            return Collections.emptyList();
        else
            return Collections.unmodifiableList(childResults);
    }

    private List<Entry<Selenese, Result>> collectChildResults(Selenese.Type type, List<Entry<Selenese, Result>> results) {
        if (childResults != null) {
            for (Entry<Selenese, Result> entry : childResults) {
                if (entry.getKey().getType() == type)
                    results.add(entry);
                else
                    entry.getValue().collectChildResults(type, results);
            }
        }
        return results;
    }

    /**
     * Collect child results specified Selenese.Type.
     *
     * @param type Selenese type.
     * @return child results.
     */
    public List<Entry<Selenese, Result>> collectChildResults(Selenese.Type type) {
        return collectChildResults(type, new ArrayList<>());
    }

    @Override
    public int compareTo(Result result) {
        return getLevel().compareTo(result.getLevel());
    }

    @Override
    public String toString() {
        return "[" + message + "]";
    }
}
