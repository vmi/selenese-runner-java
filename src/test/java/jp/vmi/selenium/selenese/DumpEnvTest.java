package jp.vmi.selenium.selenese;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class DumpEnvTest {

    private void dumpEntries(String title, Map<?, ?> map) {
        System.out.printf("###[%s]\n", title);
        List<Object> list = new ArrayList<Object>(map.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                @SuppressWarnings("unchecked")
                Entry<String, String> e1 = (Entry<String, String>) o1;
                @SuppressWarnings("unchecked")
                Entry<String, String> e2 = (Entry<String, String>) o2;
                return e1.getKey().compareTo(e2.getKey());
            }
        });
        for (Object o : list) {
            @SuppressWarnings("unchecked")
            Entry<String, String> e = (Entry<String, String>) o;
            System.out.printf("   %s=[%s]\n", e.getKey(), StringEscapeUtils.escapeJava(e.getValue()));
        }
    }

    @Test
    public void dumpEnv() {
        Properties props = System.getProperties();
        Map<String, String> env = System.getenv();
        dumpEntries("Properties", props);
        dumpEntries("EnvironmentVariables", env);
    }
}
