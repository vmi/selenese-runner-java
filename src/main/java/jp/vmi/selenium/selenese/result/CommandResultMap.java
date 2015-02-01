package jp.vmi.selenium.selenese.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.vmi.selenium.selenese.command.ICommand;

/**
 * CommandResult map for chaching.
 */
public class CommandResultMap implements Map<ICommand, List<CommandResult>> {

    private final IdentityHashMap<ICommand, List<CommandResult>> map;

    /**
     * Constructor.
     *
     * @param cresultList command result list.
     */
    public CommandResultMap(CommandResultList cresultList) {
        map = new IdentityHashMap<ICommand, List<CommandResult>>(cresultList.size());
        putAll(cresultList);
    }

    /**
     * Put command result.
     *
     * @param cresult command result.
     */
    @SuppressWarnings("unchecked")
    public void put(CommandResult cresult) {
        if (cresult instanceof List) {
            putAll((List<CommandResult>) cresult);
            return;
        }
        ICommand command = ((CommandResult) cresult).getCommand();
        List<CommandResult> list = map.get(command);
        if (list == null)
            map.put(command, list = new ArrayList<CommandResult>(1));
        list.add((CommandResult) cresult);
    }

    /**
     * Put command result list.
     *
     * @param cresultList command result list.
     */
    public void putAll(List<CommandResult> cresultList) {
        for (CommandResult cresult : cresultList)
            put(cresult);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public List<CommandResult> get(Object key) {
        return map.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Set<ICommand> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<List<CommandResult>> values() {
        return map.values();
    }

    @Override
    public Set<Entry<ICommand, List<CommandResult>>> entrySet() {
        return map.entrySet();
    }

    // The following methods are not implemented.

    @Override
    public List<CommandResult> put(ICommand key, List<CommandResult> value) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public void putAll(Map<? extends ICommand, ? extends List<CommandResult>> m) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public List<CommandResult> remove(Object key) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }
}
