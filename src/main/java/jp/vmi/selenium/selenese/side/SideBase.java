package jp.vmi.selenium.selenese.side;

/**
 * base element of side format.
 */
@SuppressWarnings("javadoc")
public abstract class SideBase {

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return Side.toJson(this);
    }
}
