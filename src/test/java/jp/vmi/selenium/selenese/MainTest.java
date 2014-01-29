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
        CommandLine cli = new Main().parseCommandLine("-D", "key1=value1", "-D", "key2=value21", "-D", "key2+=value22");
        String[] defines = cli.getOptionValues("define");
        assertThat(defines, is(array(equalTo("key1=value1"), equalTo("key2=value21"), equalTo("key2+=value22"))));
        DriverOptions driverOptions = new DriverOptions(cli);
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.merge(driverOptions.getCapabilities());
        assertThat(caps.getCapability("key1"), is(equalTo((Object) "value1")));
        assertThat((Object[]) caps.getCapability("key2"), is(array(equalTo((Object) "value21"), equalTo((Object) "value22"))));
        assertThat(driverOptions.toString(), is(not("[]")));
    }
}
