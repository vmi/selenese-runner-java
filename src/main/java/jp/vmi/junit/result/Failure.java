package jp.vmi.junit.result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * failure element.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
class Failure {

    @XmlAttribute
    private String message;

    @XmlValue
    private String value;

    public Failure() {
    }

    /**
     * Constructor.
     *
     * @param message failure message.
     * @param value failure value.
     */
    public Failure(String message, String value) {
        this.message = message;
        this.value = value;
    }
}
