package jp.vmi.selenium.selenese.log;

import java.util.Date;
import java.util.Objects;

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
        if (expiry != null && (expiry.getTime() & 0xFFFF_0000_0000_0000L) == 0x7FFF_0000_0000_0000L) {
            // FIXME GeckoDriver's bug?
            expiry = null;
        }
        this.expiry = expiry;
    }

    public boolean equalsWithoutExpiry(CookieValue other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        return key.equals(other.key)
            && Objects.equals(value, other.value)
            && ((expiry == null) == (other.expiry == null));
    }

    @Override
    public String toString() {
        String expiryString = (expiry != null) ? DateTimeUtils.formatWithoutMS(expiry.getTime()) : "*";
        return key.name + "=[" + value + "]"
            + " (domain=" + key.domain + ", path=" + key.path + ", expire=" + expiryString + ")";
    }
}
