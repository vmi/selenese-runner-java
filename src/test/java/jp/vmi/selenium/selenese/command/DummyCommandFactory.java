package jp.vmi.selenium.selenese.command;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("javadoc")
public class DummyCommandFactory implements ICommandFactory {

    @Override
    public ICommand newCommand(int index, String name, String... args) {
        System.err.printf("[DummyCommandFactory] %d: %s(%s)\n", index, name, StringUtils.join(args, ", "));
        return null;
    }
}
