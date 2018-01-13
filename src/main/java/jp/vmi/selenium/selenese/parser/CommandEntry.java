package jp.vmi.selenium.selenese.parser;

/**
 * Command entry in test case.
 */
public class CommandEntry extends TestElementEntry {

    /** Command arguments. */
    public final String[] args;

    /**
     * Constructor.
     *
     * @param id command id.
     * @param name command name.
     * @param args command arguments.
     */
    public CommandEntry(String id, String name, String... args) {
        super(id, name);
        for (int i = 0; i < args.length; i++)
            if (args[i] == null)
                args[i] = "";
        this.args = args;
    }
}
