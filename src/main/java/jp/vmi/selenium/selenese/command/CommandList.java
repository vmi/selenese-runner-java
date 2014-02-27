package jp.vmi.selenium.selenese.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 * Command list.
 */
public class CommandList extends ArrayList<ICommand> {

    private static final long serialVersionUID = 1L;

    private final Map<Object, Integer> indexCache = new HashMap<Object, Integer>();

    @Override
    public boolean add(ICommand command) {
        if (command instanceof ILabel)
            indexCache.put(((ILabel) command).getLabel(), size());
        return super.add(command);
    }

    /**
     * Get index by label or command.
     *
     * @param key label string or ICommand object.
     * @return index or -1.
     */
    @Override
    public int indexOf(Object key) {
        Integer index = indexCache.get(key);
        if (index == null) {
            index = super.indexOf(key);
            if (index == null)
                return -1;
            indexCache.put(key, index);
        }
        return index;
    }

    /**
     * Original list iterator.
     * 
     * see {@link ArrayList#listIterator(int)}
     * 
     * @param index start index.
     * @return ListIterator.
     */
    public ListIterator<ICommand> originalListIterator(int index) {
        return super.listIterator(index);
    }

    @Override
    public CommandListIterator listIterator(int index) {
        return new CommandListIterator(this);
    }

    @Override
    public CommandListIterator listIterator() {
        return listIterator(0);
    }

    @Override
    public CommandListIterator iterator() {
        return listIterator();
    }
}
