package jp.vmi.selenium.selenese.result;

import java.util.regex.Pattern;

/**
 * Result of error.
 */
public class Error extends Result {

    private static final Pattern SKIP_RE = Pattern.compile("ByGuice"
        + "|^com\\.google\\.inject\\."
        + "|^java\\.lang\\.reflect\\."
        + "|^sun\\.reflect\\."
        + "|^org\\.junit\\."
        + "|^org\\.eclipse\\.jdt\\.");

    private static String getExceptionMessage(Exception e) {
        StringBuilder result = new StringBuilder("Error: ");
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
     * Constructor.
     *
     * @param message error message.
     */
    public Error(String message) {
        super("Error: " + message);
    }

    /**
     * Constructor.
     *
     * @param e Exception.
     */
    public Error(Exception e) {
        super(getExceptionMessage(e));
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }
}
