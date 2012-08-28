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

    /**
     * Constructor.
     *
     * @param name property name.
     * @param value property value.
     */
    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
