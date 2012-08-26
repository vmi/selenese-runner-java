package jp.vmi.junit.result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * error element.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@SuppressWarnings("unused")
class Error {

    @XmlAttribute
    private String message;

    @XmlValue
    private String value;

    public Error() {
    }

    /**
     * Constructor.
     *
     * @param message error message.
     * @param value error value.
     */
    public Error(String message, String value) {
        this.message = message;
        this.value = value;
    }
}
