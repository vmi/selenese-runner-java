package jp.vmi.selenium.selenese.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Configuration information.
 */
public class DefaultConfig implements IConfig {

    private final CommandLine cli;
    private final Map<String, List<String>> config = new HashMap<>();

    /**
     * Constructor.
     *
     * @param args command line arguments.
     */
    public DefaultConfig(String... args) {
        this(new SeleneseRunnerOptions().parseCommandLine(args));
    }

    /**
     * Constructor.
     *
     * @param cli parsed command line.
     */
    public DefaultConfig(CommandLine cli) {
        this.cli = cli;
        String file = cli.getOptionValue(SeleneseRunnerOptions.CONFIG);
        if (file != null)
            loadFrom(file);
    }

    @Override
    public String[] getArgs() {
        return cli.getArgs();
    }

    private boolean cliHasOption(String opt) {
        return cli != null && cli.hasOption(opt);
    }

    // Comment, KEY(1):VALUE(2), or NEXT_VALUE(3).
    private static final Pattern RE = Pattern.compile("#.*|([\\w\\-]+)\\s*:\\s*(.*?)\\s*|\\s+(.*?)\\s*");

    /**
     * load configuration from file.
     *
     * @param file configuration file name.
     */
    public void loadFrom(String file) {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
            int cnt = 0;
            String line;
            String currentKey = null;
            List<String> values = null;
            while ((line = r.readLine()) != null) {
                cnt++;
                if (line.isEmpty())
                    continue;
                Matcher matcher = RE.matcher(line);
                if (!matcher.matches())
                    throw new RuntimeException(file + ":" + cnt + ": Invalid format: " + line);
                String key = matcher.group(1);
                if (key != null) {
                    currentKey = key;
                    values = new ArrayList<>();
                    String value = matcher.group(2);
                    if (StringUtils.isNotEmpty(value))
                        values.add(value);
                    config.put(key, values);
                } else if (currentKey != null) {
                    String value = matcher.group(3);
                    if (StringUtils.isNotEmpty(value))
                        values.add(value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(r);
        }
    }

    @Override
    public boolean hasOption(String opt) {
        return cliHasOption(opt) || config.containsKey(opt);
    }

    @Override
    public String getOptionValue(String opt) {
        return getOptionValue(opt, null);
    }

    @Override
    public String getOptionValue(String opt, String defaultValue) {
        if (cliHasOption(opt))
            return cli.getOptionValue(opt);
        List<String> values = config.get(opt);
        if (values == null || values.isEmpty())
            return defaultValue;
        return values.get(0);
    }

    @Override
    public String[] getOptionValues(String opt) {
        if (cliHasOption(opt))
            return cli.getOptionValues(opt);
        List<String> values = config.get(opt);
        if (values == null || values.isEmpty())
            return null;
        return values.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    @Override
    public boolean getOptionValueAsBoolean(String opt) {
        if (cliHasOption(opt))
            return true;
        List<String> values = config.get(opt);
        return values != null && !values.isEmpty() && BooleanUtils.toBoolean(values.get(0));
    }
}
