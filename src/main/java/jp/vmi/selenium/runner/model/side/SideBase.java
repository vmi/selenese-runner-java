package jp.vmi.selenium.runner.model.side;

import java.util.UUID;

/**
 * base element of side format.
 */
@SuppressWarnings("javadoc")
public abstract class SideBase {

    private String id;
    private String name;

    public SideBase(boolean isGen) {
        if (isGen)
            id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return SideProject.toJson(this);
    }
}
