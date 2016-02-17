package jp.vmi.script;

import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class JSWrapperTest {

    private ScriptEngine engine;

    @Before
    public void init() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByExtension("js");
    }

    @Test
    public void testMap() throws Exception {
        Object object = engine.eval("var map = {'abc': 'ABC', 'def': 'DEF', 'ghi': 'GHI'}; map");
        JSMap<String, String> map = new JSMap<>(engine, object);
        assertThat(map.get("abc"), equalTo("ABC"));
        assertThat(map.keySet(), equalTo((Set<String>) Sets.newHashSet("abc", "def", "ghi")));
        assertThat(map.size(), equalTo(3));
    }

    @Test
    public void testList() throws Exception {
        Object object = engine.eval("var list = ['abc', 'def', 'ghi']; list");
        JSList<String> list = new JSList.JSNativeList<>(engine, object);
        assertThat(list.get(1), equalTo("def"));
        assertThat(list.size(), equalTo(3));
    }
}
