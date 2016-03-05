package jp.vmi.selenium.selenese.config;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Test;
import org.kohsuke.args4j.Option;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("javadoc")
public class NewDefaultConfigTest {

    @Test
    public void testOptionNames() throws Exception {
        Set<String> optionNameFields = new HashSet<>();
        for (Field field : IConfig.class.getFields()) {
            if (field.getType() == String.class) {
                String name = field.getName();
                if (name.matches("[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*") && !name.equals("DEFAULT_TIMEOUT_MILLISEC"))
                    optionNameFields.add((String) field.get(null));
            }
        }
        Set<String> optionFields = new HashSet<>();
        for (Field field : DefaultConfig.class.getDeclaredFields()) {
            Option option = field.getAnnotation(Option.class);
            if (option != null && !option.name().equals("--config")) {
                assertThat(option.name().substring(0, 2), is("--"));
                optionFields.add(option.name().substring(2));
            }
        }
        assertThat(optionNameFields, is(equalTo(optionFields)));
    }

    private static final String[] testArgs = {
        // "--config", "config-file",
        "--driver", "driver-name",
        "--profile", "profile-name",
        "--profile-dir", "profile-dir",
        "--chrome-experimental-options", "json-file",
        "--chrome-extension", "ext-dir1",
        "--chrome-extension", "ext-dir2",
        "--proxy", "proxy-host:proxy-port",
        "--proxy-user", "user-name",
        "--proxy-password", "user-password",
        "--no-proxy", "no-proxy-hosts",
        "--cli-args", "--cliarg1",
        "--cli-args", "-cliarg2",
        "--cli-args", "cliarg3",
        "--remote-url", "remote-url",
        "--remote-platform", "remote-platform",
        "--remote-browser", "remote-browser",
        "--remote-version", "remote-version",
        "--highlight",
        "--screenshot-dir", "screenshot-dir",
        "--screenshot-all", "screenshot-all-dir",
        "--screenshot-on-fail", "screenshot-on-fail-dir",
        "--ignore-screenshot-command",
        "--baseurl", "base-url",
        "--firefox", "firefox-binary-path",
        "--chromedriver", "chromedriver-path",
        "--iedriver", "iedriver-path",
        "--phantomjs", "phantomjs-path",
        "--xml-result", "xml-result-dir",
        "--html-result", "html-result-dir",
        "--timeout", "timeout",
        "--set-speed", "speed",
        "--height", "screen-height",
        "--width", "screen-width",
        "--define", "key1=value11",
        "--define", "key1=value12",
        "--define", "key2+=value21",
        "--define", "key2+=value22",
        "--rollup", "rollup-file1",
        "--rollup", "rollup-file2",
        "--rollup", "rollup-file3",
        "--cookie-filter", "+RE",
        "--command-factory", "com.example.CommandFactory",
        "--no-exit",
        "--strict-exit-code",
        "--help",
        "--define", "--define",
        "arg1", "arg2", "arg3"
    };

    @Test
    public void testParseArg() {
        DefaultConfig options = new DefaultConfig();

        //assertThat(options.getConfig(), is(nullValue()));
        assertThat(options.getDriver(), is(nullValue()));
        assertThat(options.getProfile(), is(nullValue()));
        assertThat(options.getProfileDir(), is(nullValue()));
        assertThat(options.getChromeExperimentalOptions(), is(nullValue()));
        assertThat(options.getChromeExtension(), is(nullValue()));
        assertThat(options.getProxy(), is(nullValue()));
        assertThat(options.getProxyUser(), is(nullValue()));
        assertThat(options.getProxyPassword(), is(nullValue()));
        assertThat(options.getNoProxy(), is(nullValue()));
        assertThat(options.getCliArgs(), is(nullValue()));
        assertThat(options.getRemoteUrl(), is(nullValue()));
        assertThat(options.getRemotePlatform(), is(nullValue()));
        assertThat(options.getRemoteBrowser(), is(nullValue()));
        assertThat(options.getRemoteVersion(), is(nullValue()));
        assertThat(options.isHighlight(), is(false));
        assertThat(options.getScreenshotDir(), is(nullValue()));
        assertThat(options.getScreenshotAll(), is(nullValue()));
        assertThat(options.getScreenshotOnFail(), is(nullValue()));
        assertThat(options.isIgnoreScreenshotCommand(), is(false));
        assertThat(options.getBaseurl(), is(nullValue()));
        assertThat(options.getFirefox(), is(nullValue()));
        assertThat(options.getChromedriver(), is(nullValue()));
        assertThat(options.getIedriver(), is(nullValue()));
        assertThat(options.getPhantomjs(), is(nullValue()));
        assertThat(options.getXmlResult(), is(nullValue()));
        assertThat(options.getHtmlResult(), is(nullValue()));
        assertThat(options.getTimeout(), is(nullValue()));
        assertThat(options.getSetSpeed(), is(nullValue()));
        assertThat(options.getHeight(), is(nullValue()));
        assertThat(options.getWidth(), is(nullValue()));
        assertThat(options.getDefine(), is(nullValue()));
        assertThat(options.getRollup(), is(nullValue()));
        assertThat(options.getCookieFilter(), is(nullValue()));
        assertThat(options.getCommandFactory(), is(nullValue()));
        assertThat(options.isNoExit(), is(false));
        assertThat(options.isStrictExitCode(), is(false));
        assertThat(options.isHelp(), is(false));
        assertThat(options.getArgs(), is(emptyArray()));

        options.parseCommandLine(testArgs);
        //assertThat(options.getConfig(), is("config-file"));
        assertThat(options.getDriver(), is("driver-name"));
        assertThat(options.getProfile(), is("profile-name"));
        assertThat(options.getProfileDir(), is("profile-dir"));
        assertThat(options.getChromeExperimentalOptions(), is("json-file"));
        assertThat(options.getChromeExtension(), is(new String[] { "ext-dir1", "ext-dir2" }));
        assertThat(options.getProxy(), is("proxy-host:proxy-port"));
        assertThat(options.getProxyUser(), is("user-name"));
        assertThat(options.getProxyPassword(), is("user-password"));
        assertThat(options.getNoProxy(), is("no-proxy-hosts"));
        assertThat(options.getCliArgs(), is(new String[] { "--cliarg1", "-cliarg2", "cliarg3" }));
        assertThat(options.getRemoteUrl(), is("remote-url"));
        assertThat(options.getRemotePlatform(), is("remote-platform"));
        assertThat(options.getRemoteBrowser(), is("remote-browser"));
        assertThat(options.getRemoteVersion(), is("remote-version"));
        assertThat(options.isHighlight(), is(true));
        assertThat(options.getScreenshotDir(), is("screenshot-dir"));
        assertThat(options.getScreenshotAll(), is("screenshot-all-dir"));
        assertThat(options.getScreenshotOnFail(), is("screenshot-on-fail-dir"));
        assertThat(options.isIgnoreScreenshotCommand(), is(true));
        assertThat(options.getBaseurl(), is("base-url"));
        assertThat(options.getFirefox(), is("firefox-binary-path"));
        assertThat(options.getChromedriver(), is("chromedriver-path"));
        assertThat(options.getIedriver(), is("iedriver-path"));
        assertThat(options.getPhantomjs(), is("phantomjs-path"));
        assertThat(options.getXmlResult(), is("xml-result-dir"));
        assertThat(options.getHtmlResult(), is("html-result-dir"));
        assertThat(options.getTimeout(), is("timeout"));
        assertThat(options.getSetSpeed(), is("speed"));
        assertThat(options.getHeight(), is("screen-height"));
        assertThat(options.getWidth(), is("screen-width"));
        assertThat(options.getDefine(), is(new String[] { "key1=value11", "key1=value12", "key2+=value21", "key2+=value22", "--define" }));
        assertThat(options.getRollup(), is(new String[] { "rollup-file1", "rollup-file2", "rollup-file3", }));
        assertThat(options.getCookieFilter(), is("+RE"));
        assertThat(options.getCommandFactory(), is("com.example.CommandFactory"));
        assertThat(options.isNoExit(), is(true));
        assertThat(options.isStrictExitCode(), is(true));
        assertThat(options.isHelp(), is(true));
        assertThat(options.getArgs(), is(new String[] { "arg1", "arg2", "arg3" }));
    }

    @Test
    //@Ignore
    public void testShowHelp() {
        System.setProperty("columns", "80");
        StringBuilderWriter sbw = new StringBuilderWriter();
        PrintWriter pw = new PrintWriter(sbw);
        new DefaultConfig().showHelp(pw, "title", "version", "cmdName", "msgs");
        System.out.println(sbw);
    }
}
