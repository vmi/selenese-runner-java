package jp.vmi.selenium.selenese.log;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

/**
 * Value of Cookie.
 */
@SuppressWarnings("javadoc")
public class CookieValue {

    private static final FastDateFormat expiryFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    public final CookieKey key;
    public final String value;
    public final Date expiry;

    public CookieValue(CookieKey key, String value, Date expiry) {
        this.key = key;
        this.value = value;
        this.expiry = expiry;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = key.hashCode();
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((expiry == null) ? 0 : expiry.hashCode());
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
        CookieValue other = (CookieValue) obj;
        return key.equals(other.key)
            && StringUtils.equals(value, other.value)
            && ((expiry != null) ? expiry.equals(other.expiry) : other.expiry == null);
    }

    @Override
    public String toString() {
        String expiryString = (expiry != null) ? expiryFormat.format(expiry) : "*";
        return key.name + "=[" + value + "]"
            + " (domain=" + key.domain + ", path=" + key.path + ", expire=" + expiryString + ")";
    }
}
