package jp.vmi.selenium.selenese.command;


/**
 * <!-- comment -->
 */
public class Comment extends Command {

    private static final String COMMENT_PREFIX = "### ";

    private static final String COMMENT_SUFFIX = " ###";

    private static final int MESSAGE = 0;

    Comment(int index, String name, String[] args, String realName, boolean andWait) {
        super(-1, name, args, 1);
    }

    @Override
    public boolean hasResult() {
        return false;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public String toString() {
        return COMMENT_PREFIX + args[MESSAGE] + COMMENT_SUFFIX;
    }
}
