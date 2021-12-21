package jp.vmi.selenium.selenese.config;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import static jp.vmi.selenium.selenese.config.IConfig.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("javadoc")
public class DefaultConfigTest {

    private final String[] args = {
        "--" + DRIVER + "=opt-firefox",
        "--" + HEADLESS,
        "--" + PROFILE + "=opt-selenium",
        "--" + PROFILE_DIR + "=/opt/path/to/profile/directory",
        "--" + PROXY + "=opt-proxy-host",
        "--" + PROXY_USER + "=opt-user-name",
        "--" + PROXY_PASSWORD + "=opt-password",
        "--" + NO_PROXY + "=opt-no-proxy-hosts",
        "--" + CLI_ARGS + "=opt-arg1",
        "--" + CLI_ARGS + "=opt-arg2",
        "--" + CLI_ARGS + "=opt-arg3",
        "--" + REMOTE_URL + "=http://example.com:4444/opt",
        "--" + REMOTE_PLATFORM + "=opt-linux",
        "--" + REMOTE_BROWSER + "=opt-firefox",
        "--" + REMOTE_VERSION + "=1.2.3-opt",
        "--" + SCREENSHOT_DIR + "=/opt/path/to/screenshot/directory",
        "--" + SCREENSHOT_ALL + "=/opt/path/to/screenshot/directory/all",
        "--" + SCREENSHOT_ON_FAIL + "=/opt/path/to/screenshot/directory/fail",
        "--" + IGNORE_SCREENSHOT_COMMAND,
        "--" + BASEURL + "=http://baseurl.example.com/opt",
        "--" + FIREFOX + "=/opt/path/to/firefox/binary",
        "--" + GECKODRIVER + "=/opt/path/to/geckodriver",
        "--" + CHROMEDRIVER + "=/opt/path/to/chromedriver",
        "--" + IEDRIVER + "=/opt/path/to/iedriver",
        "--" + EDGEDRIVER + "=/opt/path/to/edgedriver",
        "--" + XML_RESULT + "=/opt/path/to/xml/result",
        "--" + HTML_RESULT + "=/opt/path/to/html/result",
        "--" + TIMEOUT + "=600",
        "--" + SET_SPEED + "=200",
        "--" + HEIGHT + "=2048",
        "--" + WIDTH + "=1536",
        "--" + DEFINE + "=opt-key1=opt-value1",
        "--" + DEFINE + "=opt-key2=opt-value2",
        "--" + DEFINE + "=opt-key3=opt-value3",
        "--" + ROLLUP + "=/opt/path/to/rollup",
        "--" + ALERTS_POLICY + "=dismiss",
        "--" + COOKIE_FILTER + "=^OPT_SID",
        "--" + COMMAND_FACTORY + "=opt.full.qualify.class.Name",
        "--" + LOG_FILTER + "=-cookie",
        "--" + NO_REPLACE_ALERT_METHOD
    };

    @Test
    public void testEmptyConfig() {
        IConfig config = new DefaultConfig();
        assertThat(config.get(DRIVER), is(nullValue()));
        assertThat(config.get(HEADLESS), is(false));
        assertThat(config.get(PROFILE), is(nullValue()));
        assertThat(config.get(PROFILE_DIR), is(nullValue()));
        assertThat(config.get(PROXY), is(nullValue()));
        assertThat(config.get(PROXY_USER), is(nullValue()));
        assertThat(config.get(PROXY_PASSWORD), is(nullValue()));
        assertThat(config.get(NO_PROXY), is(nullValue()));
        assertThat(config.getCliArgs(), is(nullValue()));
        assertThat(config.get(REMOTE_URL), is(nullValue()));
        assertThat(config.get(REMOTE_PLATFORM), is(nullValue()));
        assertThat(config.get(REMOTE_BROWSER), is(nullValue()));
        assertThat(config.get(REMOTE_VERSION), is(nullValue()));
        assertThat(config.isHighlight(), is(false));
        assertThat(config.get(SCREENSHOT_DIR), is(nullValue()));
        assertThat(config.get(SCREENSHOT_ALL), is(nullValue()));
        assertThat(config.get(SCREENSHOT_ON_FAIL), is(nullValue()));
        assertThat(config.isIgnoreScreenshotCommand(), is(false));
        assertThat(config.get(BASEURL), is(nullValue()));
        assertThat(config.get(FIREFOX), is(nullValue()));
        assertThat(config.get(GECKODRIVER), is(nullValue()));
        assertThat(config.get(CHROMEDRIVER), is(nullValue()));
        assertThat(config.get(IEDRIVER), is(nullValue()));
        assertThat(config.get(EDGEDRIVER), is(nullValue()));
        assertThat(config.get(XML_RESULT), is(nullValue()));
        assertThat(config.get(HTML_RESULT), is(nullValue()));
        assertThat(config.get(TIMEOUT), is(nullValue()));
        assertThat(config.get(SET_SPEED), is(nullValue()));
        assertThat(config.get(HEIGHT), is(nullValue()));
        assertThat(config.get(WIDTH), is(nullValue()));
        assertThat(config.getDefine(), is(nullValue()));
        assertThat(config.getAlertsPolicy(), is(nullValue()));
        assertThat(config.get(ROLLUP), is(nullValue()));
        assertThat(config.get(COOKIE_FILTER), is(nullValue()));
        assertThat(config.get(COMMAND_FACTORY), is(nullValue()));
        assertThat(config.get(LOG_FILTER), is(nullValue()));
        assertThat(config.getArgs(), is(emptyArray()));
        assertThat(config.get(NO_REPLACE_ALERT_METHOD), is(false));
    }

    @Test
    public void testConfigFile() {
        String file = DefaultConfigTest.class.getResource("/config/test.config").getPath();
        IConfig config = new DefaultConfig(new String[] { "--config", file });
        assertThat((String) config.get(DRIVER), is("firefox"));
        assertThat((boolean) config.get(HEADLESS), is(true));
        assertThat((String) config.get(PROFILE), is("selenium"));
        assertThat((String) config.get(PROFILE_DIR), is("/path/to/profile/directory"));
        assertThat((String) config.get(PROXY), is("proxy-host"));
        assertThat((String) config.get(PROXY_USER), is("user-name"));
        assertThat((String) config.get(PROXY_PASSWORD), is("password"));
        assertThat((String) config.get(NO_PROXY), is("no-proxy-hosts"));
        assertThat(config.getCliArgs(), equalTo(new String[] { "arg1", "arg2", "arg3" }));
        assertThat((String) config.get(REMOTE_URL), is("http://example.com:4444/"));
        assertThat((String) config.get(REMOTE_PLATFORM), is("linux"));
        assertThat((String) config.get(REMOTE_BROWSER), is("firefox"));
        assertThat((String) config.get(REMOTE_VERSION), is("1.2.3"));
        assertThat(config.isHighlight(), is(true));
        assertThat((String) config.get(SCREENSHOT_DIR), is("/path/to/screenshot/directory"));
        assertThat((String) config.get(SCREENSHOT_ALL), is("/path/to/screenshot/directory/all"));
        assertThat((String) config.get(SCREENSHOT_ON_FAIL), is("/path/to/screenshot/directory/fail"));
        assertThat(config.isIgnoreScreenshotCommand(), is(false));
        assertThat((String) config.get(BASEURL), is("http://baseurl.example.com/"));
        assertThat((String) config.get(FIREFOX), is("/path/to/firefox/binary"));
        assertThat((String) config.get(GECKODRIVER), is("/path/to/geckodriver"));
        assertThat((String) config.get(CHROMEDRIVER), is("/path/to/chromedriver"));
        assertThat((String) config.get(IEDRIVER), is("/path/to/iedriver"));
        assertThat((String) config.get(EDGEDRIVER), is("/path/to/edgedriver"));
        assertThat((String) config.get(XML_RESULT), is("/path/to/xml/result"));
        assertThat((String) config.get(HTML_RESULT), is("/path/to/html/result"));
        assertThat((String) config.get(TIMEOUT), is("300"));
        assertThat((String) config.get(SET_SPEED), is("100"));
        assertThat((String) config.get(HEIGHT), is("1024"));
        assertThat((String) config.get(WIDTH), is("768"));
        assertThat(config.getDefine(), equalTo(new String[] { "key1=value1", "key2=value2", "key3=value3" }));
        assertThat((String) config.get(ALERTS_POLICY), is("accept"));
        assertThat(config.getRollup(), equalTo(new String[] { "/path/to/rollup" }));
        assertThat((String) config.get(COOKIE_FILTER), is("^SID"));
        assertThat((String) config.get(COMMAND_FACTORY), is("full.qualify.class.Name"));
        assertThat(config.getLogFilter(), equalTo(new String[] { "-pageinfo", "+title", "+url" }));
        assertThat(config.isNoReplaceAlertMethod(), is(true));
    }

    @Test
    public void testCommandLine() {
        String file = DefaultConfigTest.class.getResource("/config/test.config").getPath();
        String[] newArgs = ArrayUtils.addAll(args, "--config", file);
        IConfig config = new DefaultConfig(newArgs);
        assertThat((String) config.get(DRIVER), is("opt-firefox"));
        assertThat((boolean) config.get(HEADLESS), is(true));
        assertThat((String) config.get(PROFILE), is("opt-selenium"));
        assertThat((String) config.get(PROFILE_DIR), is("/opt/path/to/profile/directory"));
        assertThat((String) config.get(PROXY), is("opt-proxy-host"));
        assertThat((String) config.get(PROXY_USER), is("opt-user-name"));
        assertThat((String) config.get(PROXY_PASSWORD), is("opt-password"));
        assertThat((String) config.get(NO_PROXY), is("opt-no-proxy-hosts"));
        assertThat(config.getCliArgs(), equalTo(new String[] { "opt-arg1", "opt-arg2", "opt-arg3" }));
        assertThat((String) config.get(REMOTE_URL), is("http://example.com:4444/opt"));
        assertThat((String) config.get(REMOTE_PLATFORM), is("opt-linux"));
        assertThat((String) config.get(REMOTE_BROWSER), is("opt-firefox"));
        assertThat((String) config.get(REMOTE_VERSION), is("1.2.3-opt"));
        assertThat(config.isHighlight(), is(true));
        assertThat((String) config.get(SCREENSHOT_DIR), is("/opt/path/to/screenshot/directory"));
        assertThat((String) config.get(SCREENSHOT_ALL), is("/opt/path/to/screenshot/directory/all"));
        assertThat((String) config.get(SCREENSHOT_ON_FAIL), is("/opt/path/to/screenshot/directory/fail"));
        assertThat(config.isIgnoreScreenshotCommand(), is(true));
        assertThat((String) config.get(BASEURL), is("http://baseurl.example.com/opt"));
        assertThat((String) config.get(FIREFOX), is("/opt/path/to/firefox/binary"));
        assertThat((String) config.get(GECKODRIVER), is("/opt/path/to/geckodriver"));
        assertThat((String) config.get(CHROMEDRIVER), is("/opt/path/to/chromedriver"));
        assertThat((String) config.get(IEDRIVER), is("/opt/path/to/iedriver"));
        assertThat((String) config.get(EDGEDRIVER), is("/opt/path/to/edgedriver"));
        assertThat((String) config.get(XML_RESULT), is("/opt/path/to/xml/result"));
        assertThat((String) config.get(HTML_RESULT), is("/opt/path/to/html/result"));
        assertThat((String) config.get(TIMEOUT), is("600"));
        assertThat((String) config.get(SET_SPEED), is("200"));
        assertThat((String) config.get(HEIGHT), is("2048"));
        assertThat((String) config.get(WIDTH), is("1536"));
        assertThat(config.getDefine(), equalTo(new String[] { "opt-key1=opt-value1", "opt-key2=opt-value2", "opt-key3=opt-value3" }));
        assertThat((String) config.get(ALERTS_POLICY), is("dismiss"));
        assertThat(config.getRollup(), equalTo(new String[] { "/opt/path/to/rollup" }));
        assertThat((String) config.get(COOKIE_FILTER), is("^OPT_SID"));
        assertThat((String) config.get(COMMAND_FACTORY), is("opt.full.qualify.class.Name"));
        assertThat(config.getLogFilter(), equalTo(new String[] { "-cookie" }));
        assertThat(config.isNoReplaceAlertMethod(), is(true));
    }
}
