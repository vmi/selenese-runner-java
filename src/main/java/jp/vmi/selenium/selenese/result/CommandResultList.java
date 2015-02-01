package jp.vmi.selenium.selenese.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import jp.vmi.selenium.selenese.command.ICommand;

import static jp.vmi.selenium.selenese.result.Unexecuted.*;

/**
 * List of command result.
 */
public class CommandResultList implements List<CommandResult> {

    private final List<CommandResult> list = new ArrayList<CommandResult>();
    private Result result = UNEXECUTED;
    private long endTime = System.currentTimeMillis();
    private CommandResultMap map = null;

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean add(CommandResult cresult) {
        if (result.compareTo(cresult.getResult()) < 0)
            result = cresult.getResult();
        list.add(cresult);
        long endTime = cresult.getEndTime();
        if (this.endTime < endTime)
            this.endTime = endTime;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends CommandResult> cresults) {
        for (CommandResult cresult : cresults)
            add(cresult);
        return true;
    }

    @Override
    public CommandResult get(int index) {
        return list.get(index);
    }

    /**
     * Set result.
     *
     * @param result result.
     * @return same as result.
     */
    public Result setResult(Result result) {
        return this.result = result;
    }

    /**
     * Get latest result.
     *
     * @return latest result.
     */
    public Result getResult() {
        return result;
    }

    @Override
    public Iterator<CommandResult> iterator() {
        return list.iterator();
    }

    @Override
    public ListIterator<CommandResult> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<CommandResult> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<CommandResult> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    /**
     * Get end time of last command.
     *
     * @return end time. (ms)
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Get results of the command.
     *
     * @param command command.
     * @return results.
     */
    public List<CommandResult> getResults(ICommand command) {
        if (map == null)
            map = new CommandResultMap(this);
        return map.get(command);
    }

    /**
     * Set end time.
     *
     * @param endTime end time.
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    // The following methods are not implemented.

    @Override
    public CommandResult set(int index, CommandResult element) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public void add(int index, CommandResult element) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());

    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public boolean addAll(int index, Collection<? extends CommandResult> c) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public CommandResult remove(int index) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

}
