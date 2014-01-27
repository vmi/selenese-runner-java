package jp.vmi.selenium.selenese;

import org.apache.commons.cli.CommandLine;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import jp.vmi.selenium.webdriver.DriverOptions;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("javadoc")
public class MainTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testOptions() {
        CommandLine cli = new Main().parseCommandLine("-D", "key1=value1", "-D", "key2=value2");
        String[] defines = cli.getOptionValues("define");
        assertThat(defines, is(array(equalTo("key1=value1"), equalTo("key2=value2"))));
        DriverOptions driverOptions = new DriverOptions(cli);
        DesiredCapabilities caps = new DesiredCapabilities();
        driverOptions.addCapabilityDefinitions(caps);
        assertThat(caps.getCapability("key1"), is(equalTo((Object) "value1")));
        assertThat(caps.getCapability("key2"), is(equalTo((Object) "value2")));
    }
}
