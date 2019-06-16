package jp.vmi.selenium.selenese.parser;

import java.util.Iterator;

import jp.vmi.selenium.runner.model.side.SideCommand;
import jp.vmi.selenium.runner.model.side.SideProject;
import jp.vmi.selenium.runner.model.side.SideTest;

/**
 * Iterator and iterable of test case of SideFile format.
 */
public class SideCommandIterator extends AbstractTestElementIterator<CommandEntry> implements CommandIterator {

    private final String baseURL;
    private final Iterator<SideCommand> iter;

    /**
     * Constructor.
     *
     * @param sideProject side format data.
     * @param testCaseId test case id.
     */
    public SideCommandIterator(SideProject sideProject, String testCaseId) {
        super(sideProject.getFilename());
        SideTest test = sideProject.getTestMap().get(testCaseId);
        setName(test.getName());
        setId(test.getId());
        this.baseURL = sideProject.getUrl();
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
        return CommandEntry.newInstance(command);
    }
}
