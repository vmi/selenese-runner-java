package jp.vmi.junit.result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * property element.
 */
@XmlAccessorType(XmlAccessType.FIELD)
class Property {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String value;

    public Property() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
