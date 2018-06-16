package jp.vmi.selenium.selenese.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jp.vmi.selenium.selenese.parser.SideCommandList.CmdElem;

/**
 * Side command list.
 */
public class SideCommandList implements List<CmdElem> {

    private final List<CmdElem> list;

    @SuppressWarnings("javadoc")
    public static class CmdAttr {
        public String name;
        public String type;
    }

    @SuppressWarnings("javadoc")
    public static class CmdElem {
        public String cmd;
        public CmdAttr attr;
    }

    private static Gson newGson() {
        return new GsonBuilder()
            .registerTypeAdapter(List.class, (JsonDeserializer<List<CmdElem>>) (json, typeOfT, context) -> {
                List<CmdElem> list = new ArrayList<>();
                json.getAsJsonArray().forEach(elem -> list.add(context.deserialize(elem, CmdElem.class)));
                return list;
            })
            .registerTypeAdapter(CmdElem.class, (JsonDeserializer<CmdElem>) (json, typeOfT, context) -> {
                CmdElem cmdElem = new CmdElem();
                JsonArray array = json.getAsJsonArray();
                cmdElem.cmd = array.get(0).getAsString();
                cmdElem.attr = context.deserialize(array.get(1), CmdAttr.class);
                return cmdElem;
            })
            .registerTypeAdapter(CmdAttr.class, (JsonDeserializer<CmdAttr>) (json, typeOfT, context) -> {
                CmdAttr cmdAttr = new CmdAttr();
                JsonObject object = json.getAsJsonObject();
                JsonElement name = object.get("name");
                if (name != null)
                    cmdAttr.name = name.toString();
                JsonElement type = object.get("type");
                if (type != null)
                    cmdAttr.type = type.toString();
                return cmdAttr;
            })
            .create();
    }

    /**
     * Constructor.
     */
    public SideCommandList() {
        try (BufferedReader r = new BufferedReader(
            new InputStreamReader(SideCommandList.class.getResourceAsStream("/selenium-ide/Command.js"), StandardCharsets.UTF_8))) {
            while (!r.readLine().matches("\\s*class\\s+CommandList\\s+\\{\\s*"))
                /* Skip lines */;
            String line = r.readLine();
            if (!line.matches("\\s*@observable\\s+list\\s+=\\s+new\\s+Map\\(\\[\\s*"))
                throw new RuntimeException("Unrecognized format");
            StringBuilder listStr = new StringBuilder("[");
            while (!(line = r.readLine()).matches("\\s*\\]\\)\\s*"))
                listStr.append(line);
            listStr.append("]");
            @SuppressWarnings("unchecked")
            List<CmdElem> list = newGson().fromJson(listStr.toString(), List.class);
            this.list = list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void forEach(Consumer<? super CmdElem> action) {
        list.forEach(action);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<CmdElem> iterator() {
        return list.iterator();
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
    public boolean add(CmdElem e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends CmdElem> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends CmdElem> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(UnaryOperator<CmdElem> operator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(Predicate<? super CmdElem> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort(Comparator<? super CmdElem> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        return list.equals(o);
    }

    @Override
    public CmdElem get(int index) {
        return list.get(index);
    }

    @Override
    public CmdElem set(int index, CmdElem element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, CmdElem element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<CmdElem> stream() {
        return list.stream();
    }

    @Override
    public CmdElem remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<CmdElem> parallelStream() {
        return list.parallelStream();
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<CmdElem> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<CmdElem> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<CmdElem> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<CmdElem> spliterator() {
        return list.spliterator();
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
