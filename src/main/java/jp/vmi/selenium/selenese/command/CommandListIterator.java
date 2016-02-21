package jp.vmi.selenium.selenese.command;

import java.util.ListIterator;

import com.thoughtworks.selenium.SeleniumException;

/**
 * Iterator of CommandList.
 */
public class CommandListIterator implements ListIterator<ICommand> {

    private final CommandList commandList;
    private ListIterator<ICommand> iterator;
    private final CommandSequence commandSequence;

    CommandListIterator(CommandList commandList, CommandListIterator parentIterator) {
        this.commandList = commandList;
        this.iterator = commandList.originalListIterator(0);
        this.commandSequence = new CommandSequence((parentIterator != null) ? parentIterator.getCommandSequence() : null);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public ICommand next() {
        return iterator.next();
    }

    @Override
    public boolean hasPrevious() {
        return iterator.hasPrevious();
    }

    @Override
    public ICommand previous() {
        return iterator.previous();
    }

    @Override
    public int nextIndex() {
        return iterator.nextIndex();
    }

    @Override
    public int previousIndex() {
        return iterator.previousIndex();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(ICommand e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(ICommand e) {
        throw new UnsupportedOperationException();
    }

    /**
     * Jump to label or command.
     *
     * @param key label string or ICommand object.
     */
    public void jumpTo(Object key) {
        int index;
        if (key == null) {
            index = commandList.size();
        } else {
            index = commandList.indexOf(key);
            if (index == -1)
                throw new SeleniumException("Cannot jump to " + key);
        }
        iterator = commandList.originalListIterator(index);
    }

    /**
     * Jump to next of label or command.
     *
     * @param key label string or ICommand object.
     */
    public void jumpToNextOf(Object key) {
        int index;
        if (key == null) {
            index = commandList.size();
        } else {
            index = commandList.indexOf(key);
            if (index == -1)
                throw new SeleniumException("Cannot jump to next of " + key);
            index++;
        }
        iterator = commandList.originalListIterator(index);
    }

    /**
     * Get command sequence.
     *
     * @return command sequence.
     */
    public CommandSequence getCommandSequence() {
        return commandSequence;
    }
}
