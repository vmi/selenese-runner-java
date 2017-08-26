package jp.vmi.selenium.selenese;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;

import static java.lang.System.*;

@SuppressWarnings("javadoc")
public class DumpEnvTest {

    private void indent(int n) {
        for (int i = 0; i < n; i++)
            out.print(' ');
    }

    private void printEntry(String key, String value, int indent) {
        indent(indent);
        out.printf("%s=[", key);
        if (key.endsWith(".class.path")) {
            for (String path : value.split(File.pathSeparator)) {
                out.print('\n');
                indent(indent + 2);
                out.print(path);
            }
            out.print('\n');
            indent(indent);
            out.print("]\n");
        } else {
            out.printf("[%s]\n", StringEscapeUtils.escapeJava(value));
        }
    }

    private void dumpEntries(String title, Map<?, ?> map) {
        out.printf("###[%s]\n", title);
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
            printEntry(e.getKey(), e.getValue(), 3);
        }
    }

    @Test
    public void dumpEnv() {
        Properties props = getProperties();
        Map<String, String> env = getenv();
        dumpEntries("Properties", props);
        dumpEntries("EnvironmentVariables", env);
    }
}
