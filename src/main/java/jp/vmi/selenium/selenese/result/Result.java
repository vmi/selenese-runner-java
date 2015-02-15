package jp.vmi.selenium.selenese.result;

import java.util.regex.Pattern;

/**
 * Result of command execution.
 */
public abstract class Result implements Comparable<Result> {

    /**
     * Result Level.
     */
    @SuppressWarnings("javadoc")
    public static enum Level {
        UNEXECUTED(-1, 0),
        SUCCESS(0, 0),
        WARNING(1, 0),
        FAILURE(2, 3),
        ERROR(3, 3);

        public final int value;
        public final int exitCode;

        private Level(int value, int exitCode) {
            this.value = value;
            this.exitCode = exitCode;
        }
    }

    private static final Pattern SKIP_RE = Pattern.compile("ByGuice"
        + "|^com\\.google\\.inject\\."
        + "|^java\\.lang\\.reflect\\."
        + "|^sun\\.reflect\\."
        + "|^org\\.junit\\."
        + "|^org\\.eclipse\\.jdt\\.");

    private final String message;

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
     * Get exception message for result.
     *
     * @param prefix prefix of message.
     * @param e exception.
     * @return message.
     */
    protected String generateExceptionMessage(String prefix, Exception e) {
        StringBuilder result = new StringBuilder(prefix).append(": ");
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
     */
    public Result update(Result newResult) {
        return newResult.getLevel().value > this.getLevel().value ? newResult : this;
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
