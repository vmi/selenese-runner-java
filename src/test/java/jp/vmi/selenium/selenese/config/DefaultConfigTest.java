package jp.vmi.selenium.selenese.config;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@SuppressWarnings({ "javadoc", "deprecation" })
public class DefaultConfigTest {

    private final String[] args = {
        "--" + DRIVER + "=opt-firefox",
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
        "--" + REMOTE_BROWSER + "=opt-phantomjs",
        "--" + REMOTE_VERSION + "=1.2.3-opt",
        "--" + SCREENSHOT_DIR + "=/opt/path/to/screenshot/directory",
        "--" + SCREENSHOT_ALL + "=/opt/path/to/screenshot/directory/all",
        "--" + SCREENSHOT_ON_FAIL + "=/opt/path/to/screenshot/directory/fail",
        "--" + IGNORE_SCREENSHOT_COMMAND,
        "--" + BASEURL + "=http://baseurl.example.com/opt",
        "--" + FIREFOX + "=/opt/path/to/firefox/binary",
        "--" + CHROMEDRIVER + "=/opt/path/to/chromedriver",
        "--" + IEDRIVER + "=/opt/path/to/iedriver",
        "--" + PHANTOMJS + "=/opt/path/to/phantomjs",
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
        "--" + COOKIE_FILTER + "=^OPT_SID",
        "--" + COMMAND_FACTORY + "=opt.full.qualify.class.Name",
    };

    @Test
    public void testEmptyConfig() {
        IConfig config = new DefaultConfig();
        assertThat(config.getOptionValue(DRIVER), is(nullValue()));
        assertThat(config.getOptionValue(PROFILE), is(nullValue()));
        assertThat(config.getOptionValue(PROFILE_DIR), is(nullValue()));
        assertThat(config.getOptionValue(PROXY), is(nullValue()));
        assertThat(config.getOptionValue(PROXY_USER), is(nullValue()));
        assertThat(config.getOptionValue(PROXY_PASSWORD), is(nullValue()));
        assertThat(config.getOptionValue(NO_PROXY), is(nullValue()));
        assertThat(config.getOptionValues(CLI_ARGS), is(nullValue()));
        assertThat(config.getOptionValue(REMOTE_URL), is(nullValue()));
        assertThat(config.getOptionValue(REMOTE_PLATFORM), is(nullValue()));
        assertThat(config.getOptionValue(REMOTE_BROWSER), is(nullValue()));
        assertThat(config.getOptionValue(REMOTE_VERSION), is(nullValue()));
        assertThat(config.getOptionValueAsBoolean(HIGHLIGHT), is(false));
        assertThat(config.getOptionValue(SCREENSHOT_DIR), is(nullValue()));
        assertThat(config.getOptionValue(SCREENSHOT_ALL), is(nullValue()));
        assertThat(config.getOptionValue(SCREENSHOT_ON_FAIL), is(nullValue()));
        assertThat(config.getOptionValueAsBoolean(IGNORE_SCREENSHOT_COMMAND), is(false));
        assertThat(config.getOptionValue(BASEURL), is(nullValue()));
        assertThat(config.getOptionValue(FIREFOX), is(nullValue()));
        assertThat(config.getOptionValue(CHROMEDRIVER), is(nullValue()));
        assertThat(config.getOptionValue(IEDRIVER), is(nullValue()));
        assertThat(config.getOptionValue(PHANTOMJS), is(nullValue()));
        assertThat(config.getOptionValue(XML_RESULT), is(nullValue()));
        assertThat(config.getOptionValue(HTML_RESULT), is(nullValue()));
        assertThat(config.getOptionValue(TIMEOUT), is(nullValue()));
        assertThat(config.getOptionValue(SET_SPEED), is(nullValue()));
        assertThat(config.getOptionValue(HEIGHT), is(nullValue()));
        assertThat(config.getOptionValue(WIDTH), is(nullValue()));
        assertThat(config.getOptionValues(DEFINE), is(nullValue()));
        assertThat(config.getOptionValue(ROLLUP), is(nullValue()));
        assertThat(config.getOptionValue(COOKIE_FILTER), is(nullValue()));
        assertThat(config.getOptionValue(COMMAND_FACTORY), is(nullValue()));
        assertThat(config.getArgs(), is(emptyArray()));
    }

    @Test
    public void testConfigFile() {
        String file = DefaultConfigTest.class.getResource("/config/test.config").getPath();
        IConfig config = new DefaultConfig(new String[] { "--config", file });
        assertThat(config.getOptionValue(DRIVER), is("firefox"));
        assertThat(config.getOptionValue(PROFILE), is("selenium"));
        assertThat(config.getOptionValue(PROFILE_DIR), is("/path/to/profile/directory"));
        assertThat(config.getOptionValue(PROXY), is("proxy-host"));
        assertThat(config.getOptionValue(PROXY_USER), is("user-name"));
        assertThat(config.getOptionValue(PROXY_PASSWORD), is("password"));
        assertThat(config.getOptionValue(NO_PROXY), is("no-proxy-hosts"));
        assertThat(config.getOptionValues(CLI_ARGS), equalTo(new String[] { "arg1", "arg2", "arg3" }));
        assertThat(config.getOptionValue(REMOTE_URL), is("http://example.com:4444/"));
        assertThat(config.getOptionValue(REMOTE_PLATFORM), is("linux"));
        assertThat(config.getOptionValue(REMOTE_BROWSER), is("phantomjs"));
        assertThat(config.getOptionValue(REMOTE_VERSION), is("1.2.3"));
        assertThat(config.getOptionValueAsBoolean(HIGHLIGHT), is(true));
        assertThat(config.getOptionValue(SCREENSHOT_DIR), is("/path/to/screenshot/directory"));
        assertThat(config.getOptionValue(SCREENSHOT_ALL), is("/path/to/screenshot/directory/all"));
        assertThat(config.getOptionValue(SCREENSHOT_ON_FAIL), is("/path/to/screenshot/directory/fail"));
        assertThat(config.getOptionValueAsBoolean(IGNORE_SCREENSHOT_COMMAND), is(false));
        assertThat(config.getOptionValue(BASEURL), is("http://baseurl.example.com/"));
        assertThat(config.getOptionValue(FIREFOX), is("/path/to/firefox/binary"));
        assertThat(config.getOptionValue(CHROMEDRIVER), is("/path/to/chromedriver"));
        assertThat(config.getOptionValue(IEDRIVER), is("/path/to/iedriver"));
        assertThat(config.getOptionValue(PHANTOMJS), is("/path/to/phantomjs"));
        assertThat(config.getOptionValue(XML_RESULT), is("/path/to/xml/result"));
        assertThat(config.getOptionValue(HTML_RESULT), is("/path/to/html/result"));
        assertThat(config.getOptionValue(TIMEOUT), is("300"));
        assertThat(config.getOptionValue(SET_SPEED), is("100"));
        assertThat(config.getOptionValue(HEIGHT), is("1024"));
        assertThat(config.getOptionValue(WIDTH), is("768"));
        assertThat(config.getOptionValues(DEFINE), equalTo(new String[] { "key1=value1", "key2=value2", "key3=value3" }));
        assertThat(config.getOptionValues(ROLLUP), equalTo(new String[] { "/path/to/rollup" }));
        assertThat(config.getOptionValue(COOKIE_FILTER), is("^SID"));
        assertThat(config.getOptionValue(COMMAND_FACTORY), is("full.qualify.class.Name"));
    }

    @Test
    public void testCommandLine() {
        String file = DefaultConfigTest.class.getResource("/config/test.config").getPath();
        String[] newArgs = ArrayUtils.addAll(args, "--config", file);
        IConfig config = new DefaultConfig(newArgs);
        assertThat(config.getOptionValue(DRIVER), is("opt-firefox"));
        assertThat(config.getOptionValue(PROFILE), is("opt-selenium"));
        assertThat(config.getOptionValue(PROFILE_DIR), is("/opt/path/to/profile/directory"));
        assertThat(config.getOptionValue(PROXY), is("opt-proxy-host"));
        assertThat(config.getOptionValue(PROXY_USER), is("opt-user-name"));
        assertThat(config.getOptionValue(PROXY_PASSWORD), is("opt-password"));
        assertThat(config.getOptionValue(NO_PROXY), is("opt-no-proxy-hosts"));
        assertThat(config.getOptionValues(CLI_ARGS), equalTo(new String[] { "opt-arg1", "opt-arg2", "opt-arg3" }));
        assertThat(config.getOptionValue(REMOTE_URL), is("http://example.com:4444/opt"));
        assertThat(config.getOptionValue(REMOTE_PLATFORM), is("opt-linux"));
        assertThat(config.getOptionValue(REMOTE_BROWSER), is("opt-phantomjs"));
        assertThat(config.getOptionValue(REMOTE_VERSION), is("1.2.3-opt"));
        assertThat(config.getOptionValueAsBoolean(HIGHLIGHT), is(true));
        assertThat(config.getOptionValue(SCREENSHOT_DIR), is("/opt/path/to/screenshot/directory"));
        assertThat(config.getOptionValue(SCREENSHOT_ALL), is("/opt/path/to/screenshot/directory/all"));
        assertThat(config.getOptionValue(SCREENSHOT_ON_FAIL), is("/opt/path/to/screenshot/directory/fail"));
        assertThat(config.getOptionValueAsBoolean(IGNORE_SCREENSHOT_COMMAND), is(true));
        assertThat(config.getOptionValue(BASEURL), is("http://baseurl.example.com/opt"));
        assertThat(config.getOptionValue(FIREFOX), is("/opt/path/to/firefox/binary"));
        assertThat(config.getOptionValue(CHROMEDRIVER), is("/opt/path/to/chromedriver"));
        assertThat(config.getOptionValue(IEDRIVER), is("/opt/path/to/iedriver"));
        assertThat(config.getOptionValue(PHANTOMJS), is("/opt/path/to/phantomjs"));
        assertThat(config.getOptionValue(XML_RESULT), is("/opt/path/to/xml/result"));
        assertThat(config.getOptionValue(HTML_RESULT), is("/opt/path/to/html/result"));
        assertThat(config.getOptionValue(TIMEOUT), is("600"));
        assertThat(config.getOptionValue(SET_SPEED), is("200"));
        assertThat(config.getOptionValue(HEIGHT), is("2048"));
        assertThat(config.getOptionValue(WIDTH), is("1536"));
        assertThat(config.getOptionValues(DEFINE), equalTo(new String[] { "opt-key1=opt-value1", "opt-key2=opt-value2", "opt-key3=opt-value3" }));
        assertThat(config.getOptionValues(ROLLUP), equalTo(new String[] { "/opt/path/to/rollup" }));
        assertThat(config.getOptionValue(COOKIE_FILTER), is("^OPT_SID"));
        assertThat(config.getOptionValue(COMMAND_FACTORY), is("opt.full.qualify.class.Name"));
    }
}
