package jp.vmi.selenium.selenese.side;

/**
 * "command" element of side format.
 */
@SuppressWarnings("javadoc")
public class SideCommand {

    private String id;
    private String command;
    private String target;
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
