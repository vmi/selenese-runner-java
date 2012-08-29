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

    public void setMessage(String message) {
        this.message = message;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
