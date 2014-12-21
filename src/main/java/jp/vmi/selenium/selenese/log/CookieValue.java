package jp.vmi.selenium.selenese.log;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import jp.vmi.selenium.selenese.utils.DateTimeUtils;

/**
 * Value of Cookie.
 */
@SuppressWarnings("javadoc")
public class CookieValue {

    public final CookieKey key;
    public final String value;
    public final Date expiry;

    public CookieValue(CookieKey key, String value, Date expiry) {
        this.key = key;
        this.value = value;
        this.expiry = expiry;
    }

    public boolean equalsWithoutExpiry(CookieValue other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        return key.equals(other.key)
            && StringUtils.equals(value, other.value)
            && ((expiry == null) == (other.expiry == null));
    }

    @Override
    public String toString() {
        String expiryString = (expiry != null) ? DateTimeUtils.formatWithoutMS(expiry.getTime()) : "*";
        return key.name + "=[" + value + "]"
            + " (domain=" + key.domain + ", path=" + key.path + ", expire=" + expiryString + ")";
    }
}
