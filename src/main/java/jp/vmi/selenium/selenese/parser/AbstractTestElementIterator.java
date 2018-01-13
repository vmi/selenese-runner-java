package jp.vmi.selenium.selenese.parser;

/**
 * Abstrat class for implementing test element iterator.
 *
 * @param <E> element class.
 */
public abstract class AbstractTestElementIterator<E extends TestElementEntry> implements TestElementIterator<E> {

    private final String filename;
    private String name;
    private String id;

    /**
     * Constructor.
     *
     * @param filename filename.
     */
    public AbstractTestElementIterator(String filename) {
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }
}
