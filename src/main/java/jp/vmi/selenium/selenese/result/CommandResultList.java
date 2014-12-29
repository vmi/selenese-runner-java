package jp.vmi.selenium.selenese.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static jp.vmi.selenium.selenese.result.Unexecuted.*;

/**
 * List of command result.
 */
public class CommandResultList implements ICommandResult, List<ICommandResult> {

    private final List<ICommandResult> list = new ArrayList<ICommandResult>();
    private Result result = UNEXECUTED;
    private long endTime = System.currentTimeMillis();

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean add(ICommandResult cresult) {
        if (result.compareTo(cresult.getResult()) < 0)
            result = cresult.getResult();
        list.add(cresult);
        long endTime = cresult.getEndTime();
        if (this.endTime < endTime)
            this.endTime = endTime;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends ICommandResult> cresults) {
        for (ICommandResult cresult : cresults)
            add(cresult);
        return true;
    }

    @Override
    public ICommandResult get(int index) {
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

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public Iterator<ICommandResult> iterator() {
        return list.iterator();
    }

    @Override
    public ListIterator<ICommandResult> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<ICommandResult> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<ICommandResult> subList(int fromIndex, int toIndex) {
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

    @Override
    public long getEndTime() {
        return endTime;
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
    public ICommandResult set(int index, ICommandResult element) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public void add(int index, ICommandResult element) {
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
    public boolean addAll(int index, Collection<? extends ICommandResult> c) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public ICommandResult remove(int index) {
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
