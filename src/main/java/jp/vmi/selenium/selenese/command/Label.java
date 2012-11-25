package jp.vmi.selenium.selenese.command;

/**
 * Command "label".
 */
public class Label extends Command {

    private static final int LABEL = 0;

    Label(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 1);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    /**
     * Get label string.
     *
     * @return label.
     */
    public String getLabel() {
        return args[LABEL];
    }
}
