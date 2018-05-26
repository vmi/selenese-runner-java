package jp.vmi.selenium.selenese.parser;

import java.util.Iterator;

import jp.vmi.selenium.selenese.side.Side;
import jp.vmi.selenium.selenese.side.SideCommand;
import jp.vmi.selenium.selenese.side.SideTest;

/**
 * Iterator and iterable of test case of SideFile format.
 */
public class SideCommandIterator extends AbstractTestElementIterator<CommandEntry> implements CommandIterator {

    private final String baseURL;
    private final Iterator<SideCommand> iter;

    /**
     * Constructor.
     *
     * @param side side format data.
     * @param testCaseId test case id.
     */
    public SideCommandIterator(Side side, String testCaseId) {
        super(side.getFilename());
        SideTest test = side.getTestMap().get(testCaseId);
        setName(test.getName());
        setId(test.getId());
        this.baseURL = side.getUrl();
        this.iter = test.getCommands().iterator();
    }

    @Override
    public String getBaseURL() {
        return baseURL;
    }

    @Override
    public boolean hasNext() {
        return iter.hasNext();
    }

    @Override
    public CommandEntry next() {
        SideCommand command = iter.next();
        return new CommandEntry(command.getId(), command.getComment(), command.getCommand(), command.getTarget(), command.getValue());
    }
}
