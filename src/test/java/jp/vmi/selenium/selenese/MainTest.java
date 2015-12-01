package jp.vmi.selenium.selenese;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import jp.vmi.selenium.selenese.config.DefaultConfig;
import jp.vmi.selenium.selenese.config.IConfig;
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
}
