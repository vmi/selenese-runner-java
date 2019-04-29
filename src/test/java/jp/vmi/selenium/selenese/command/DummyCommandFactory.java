package jp.vmi.selenium.selenese.command;

@SuppressWarnings("javadoc")
public class DummyCommandFactory implements ICommandFactory {

    @Override
    public ICommand newCommand(int index, String name, String... args) {
        System.err.printf("[DummyCommandFactory] %d: %s(%s)\n", index, name, String.join(", ", args));
        return null;
    }
}
