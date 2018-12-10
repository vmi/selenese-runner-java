package jp.vmi.selenium.runner.model.side;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jp.vmi.selenium.runner.model.ICommand;

/**
 * "command" element of side format.
 */
@SuppressWarnings("javadoc")
public class SideCommand implements ICommand {

    private String id;
    private String command;
    private String target;
    private List<List<String>> targets = Collections.emptyList();
    private String value;
    private String comment;

    public SideCommand(boolean isGen) {
        if (isGen)
            id = UUID.randomUUID().toString();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<List<String>> getTargets() {
        return targets;
    }

    public void setTargets(List<List<String>> targets) {
        this.targets = targets;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
