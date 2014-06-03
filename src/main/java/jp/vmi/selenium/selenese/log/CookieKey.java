package jp.vmi.selenium.selenese.log;

/**
 * Key of Cookie.
 */
@SuppressWarnings("javadoc")
public class CookieKey implements Comparable<CookieKey> {

    public final String name;
    public final String path;
    public final String domain;

    /**
     * Constructor.
     *
     * @param name name.
     * @param path path.
     * @param domain domain.
     */
    public CookieKey(String name, String path, String domain) {
        this.name = name;
        this.path = path != null ? path : "*";
        this.domain = domain != null ? domain : "*";
    }

    @Override
    public int compareTo(CookieKey other) {
        int cn = name.compareTo(other.name);
        if (cn != 0)
            return cn;
        int cp = path.compareTo(other.path);
        if (cp != 0)
            return cp;
        return domain.compareTo(other.domain);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = name.hashCode();
        result = prime * result + path.hashCode();
        result = prime * result + domain.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CookieKey other = (CookieKey) obj;
        return name.equals(other.name)
            && path.equals(other.path)
            && domain.equals(other.domain);
    }

    @Override
    public String toString() {
        return name + " (domain=" + domain + ", path=" + path + ")";
    }
}
