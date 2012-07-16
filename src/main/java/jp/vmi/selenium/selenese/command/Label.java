package jp.vmi.selenium.selenese.command;

//https://github.com/davehunt/selenium-ide-flowcontrol
public class Label extends Command {

    private static final int LABEL = 0;

    Label(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args);
    }

    public String getLabel() {
        return args[LABEL];
    }
}
