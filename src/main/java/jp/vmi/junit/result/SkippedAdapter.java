package jp.vmi.junit.result;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import jp.vmi.junit.result.SkippedAdapter.Skipped;

/**
 * marshaling and unmarshaling "skipped" tag.
 */
public class SkippedAdapter extends XmlAdapter<Skipped, Integer> {

    private static final Skipped skipped = new Skipped();

    /** Skipped */
    @XmlRootElement
    public static class Skipped {
    }

    @Override
    public Integer unmarshal(Skipped value) throws Exception {
        return value != null ? 1 : 0;
    }

    @Override
    public Skipped marshal(Integer value) throws Exception {
        return value != 0 ? skipped : null;
    }
}
