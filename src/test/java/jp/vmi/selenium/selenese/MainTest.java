package jp.vmi.selenium.selenese;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import jp.vmi.selenium.selenese.config.DefaultConfig;
import jp.vmi.selenium.selenese.config.IConfig;
import jp.vmi.selenium.selenese.log.LogFilter;
import jp.vmi.selenium.webdriver.DriverOptions;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("javadoc")
public class MainTest {

    @Test
    public void testOptions() {
        IConfig config = new DefaultConfig(
            "-D", "key1=value1",
            "-D", "key2+=value2",
            "-D", "key3=value31", "-D", "key3+=value32",
            "-D", "key4+=value41", "-D", "key4+=value42");
        String[] defines = config.getDefine();
        assertThat(defines, is(arrayContaining(
            "key1=value1",
            "key2+=value2",
            "key3=value31", "key3+=value32",
            "key4+=value41", "key4+=value42")));
        DriverOptions driverOptions = new DriverOptions(config);
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.merge(driverOptions.getCapabilities());
        assertThat((String) caps.getCapability("key1"), is(equalTo("value1")));
        assertThat((String[]) caps.getCapability("key2"), is(arrayContaining("value2")));
        assertThat((String[]) caps.getCapability("key3"), is(arrayContaining("value31", "value32")));
        assertThat((String[]) caps.getCapability("key4"), is(arrayContaining("value41", "value42")));
        assertThat(driverOptions.toString(), is(not("[]")));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testVar() {
        Main main = new Main();
        Runner runner = new Runner();
        IConfig config = new DefaultConfig(
            "-d", "htmlunit",
            "-V", "key1=null",
            "-V", "key2=1",
            "-V", "key3='str'",
            "-V", "key4=['a', 'b', 'c']",
            "-V", "key5={'A':1,'B':2,'C':3}");
        main.setupRunner(runner, config);
        VarsMap varsMap = runner.getVarsMap();
        assertThat(varsMap.get("key1"), is(nullValue()));
        assertThat(varsMap.get("key2"), is(1.0));
        assertThat(varsMap.get("key3"), is("str"));
        assertThat((List<String>) varsMap.get("key4"), is(contains("a", "b", "c")));
        Map<Object, Object> value5 = (Map<Object, Object>) varsMap.get("key5");
        assertThat(value5, is(notNullValue()));
        assertThat(value5.get("A"), is(1.0));
        assertThat(value5.get("B"), is(2.0));
        assertThat(value5.get("C"), is(3.0));
    }

    @Test
    public void testLogFilterOption() {
        Main main = new Main();
        Runner runner = new Runner();
        IConfig config = new DefaultConfig(
            "-d", "htmlunit",
            "--log-filter", "+pageinfo");
        main.setupRunner(runner, config);
        assertThat(runner.getLogFilter(), is(LogFilter.all()));

        runner = new Runner();
        config = new DefaultConfig(
            "-d", "htmlunit",
            "--log-filter", "-pageinfo",
            "--log-filter", "+cookie",
            "--log-filter", "+title");
        main.setupRunner(runner, config);
        assertThat(runner.getLogFilter().contains(LogFilter.COOKIE), is(true));
        assertThat(runner.getLogFilter().contains(LogFilter.TITLE), is(true));
        assertThat(runner.getLogFilter().contains(LogFilter.URL), is(false));

        runner = new Runner();
        config = new DefaultConfig(
            "-d", "htmlunit",
            "--log-filter", "-cookie",
            "--log-filter", "-title");
        main.setupRunner(runner, config);
        assertThat(runner.getLogFilter().contains(LogFilter.COOKIE), is(false));
        assertThat(runner.getLogFilter().contains(LogFilter.TITLE), is(false));
        assertThat(runner.getLogFilter().contains(LogFilter.URL), is(true));

        IllegalArgumentException e = org.junit.Assert.assertThrows(IllegalArgumentException.class, () -> {
            Runner runner2 = new Runner();
            IConfig config2 = new DefaultConfig(
                "-d", "htmlunit",
                "--log-filter", "foo");
            main.setupRunner(runner2, config2);
        });
        assertThat(e.getMessage(), is("Invalid value for --log-filter: foo"));
    }
}
