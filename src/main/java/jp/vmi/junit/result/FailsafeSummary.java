package jp.vmi.junit.result;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * failsafe-summary.
 */
@XmlRootElement(name = "failsafe-summary")
@XmlType(propOrder = {
    "result",
    "timeout",
    "completed",
    "errors",
    "failures",
    "skipped",
    "failureMessage"
})
@SuppressWarnings("javadoc")
public class FailsafeSummary {

    @XmlAttribute
    public Integer getResult() {
        return (errors == 0 && failures == 0) ? null : 255;
    }

    @XmlAttribute
    public boolean timeout = false;

    @XmlElement
    public int completed = 0;

    @XmlElement
    public int errors = 0;

    @XmlElement
    public int failures = 0;

    @XmlElement
    public int skipped = 0;

    @XmlElement
    public String failureMessage = "";
}
