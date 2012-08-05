package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.command.Command.Result;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final int HELP_WIDTH = 78;

    private static final String PROG_TITLE = "Selenese Runner %s";

    private static final String HEADER = "Selenese script interpreter implemented by Java.";

    private static final String FOOTER = "*note: If you want to use basic and/or proxy authentication on Firefox, "
        + "then create new profile, "
        + "install AutoAuth plugin, "
        + "configure all settings, "
        + "access test site with the profile, "
        + "and specify the profile by --profile option.";

    private static class SROptions extends Options {
        private static final long serialVersionUID = 1L;

        private int i = 0;

        public final Map<Option, Integer> optionOrder = new HashMap<Option, Integer>();

        @Override
        public Options addOption(Option opt) {
            optionOrder.put(opt, ++i);
            return super.addOption(opt);
        }

        public Comparator<Option> getOptionComparator() {
            return new Comparator<Option>() {
                @Override
                public int compare(Option o1, Option o2) {
                    return optionOrder.get(o1) - optionOrder.get(o2);
                }
            };
        }
    }

    private final SROptions options = new SROptions();

    @SuppressWarnings("static-access")
    public Main() {
        options.addOption(OptionBuilder.withLongOpt("driver")
            .hasArg().withArgName("driver")
            .withDescription("firefox (default) | chrome | ie | safari | htmlunit | FQCN-of-WebDriverFactory.")
            .create('d'));
        options.addOption(OptionBuilder.withLongOpt("profile")
            .hasArg().withArgName("name")
            .withDescription("profile name (Firefox only)")
            .create('p'));
        options.addOption(OptionBuilder.withLongOpt("profile-dir")
            .hasArg().withArgName("dir")
            .withDescription("profile directory (Firefox only)")
            .create('P'));
        options.addOption(OptionBuilder.withLongOpt("proxy")
            .hasArg().withArgName("proxy")
            .withDescription("proxy host and port (HOST:PORT) (excepting IE)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("proxy-user")
            .hasArg().withArgName("user")
            .withDescription("proxy username (HtmlUnit only *)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("proxy-password")
            .hasArg().withArgName("password")
            .withDescription("proxy password (HtmlUnit only *)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("no-proxy")
            .hasArg().withArgName("no-proxy")
            .withDescription("no-proxy hosts")
            .create());
        options.addOption(OptionBuilder.withLongOpt("screenshot-dir")
            .hasArg().withArgName("dir")
            .withDescription("directory for screenshot images. (default: current directory)")
            .create('s'));
        options.addOption(OptionBuilder.withLongOpt("screenshot-all")
            .withDescription("screenshot all commands")
            .create('S'));
        options.addOption(OptionBuilder.withLongOpt("baseuri")
            .hasArg().withArgName("baseURI")
            .withDescription("override baseURI set in selenese")
            .create('b'));
        options.addOption(OptionBuilder.withLongOpt("chromedriver")
            .hasArg().withArgName("path")
            .withDescription("path to 'chromedriver' binary")
            .create());
        options.addOption(OptionBuilder.withLongOpt("help")
            .withDescription("show this message")
            .create('h'));
    }

    public String getVersion() {
        InputStream is = getClass().getResourceAsStream("/META-INF/maven/jp.vmi.selenium/selenese-runner-java/pom.properties");
        if (is != null) {
            try {
                Properties prop = new Properties();
                prop.load(is);
                return prop.getProperty("version");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return "(missing version information)";
    }

    private void help(String... msgs) {
        if (msgs.length > 0) {
            for (String msg : msgs)
                System.out.println(msg);
            System.out.println();
        }

        String progName = System.getenv("PROG_NAME");
        if (StringUtils.isBlank(progName))
            progName = "java -jar selenese-runner.jar";

        HelpFormatter fmt = new HelpFormatter();
        fmt.setOptionComparator(options.getOptionComparator());
        PrintWriter pw = new PrintWriter(System.out);
        pw.format(PROG_TITLE + "\n\n" + HEADER + "\n\n", getVersion());
        fmt.setSyntaxPrefix("Usage: ");
        fmt.printHelp(pw, HELP_WIDTH, progName + " <option> ... <testcase|testsuite> ...\n",
            null, options, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
        pw.println();
        fmt.printWrapped(pw, HELP_WIDTH, FOOTER);
        pw.flush();
        exit(1);
    }

    public CommandLine parseCommandLine(String[] args) throws IllegalArgumentException {
        CommandLine cli = null;
        try {
            cli = new PosixParser().parse(options, args);
            for (Option opt : cli.getOptions())
                log.debug(opt.getLongOpt() + ":" + opt.getValue());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return cli;
    }

    public void run(String[] args) {
        int exitCode = 1;
        try {
            CommandLine cli = parseCommandLine(args);
            String[] filenames = cli.getArgs();
            if (filenames.length == 0)
                help();
            List<File> files = new ArrayList<File>(filenames.length);
            for (String filename : filenames) {
                File file = new File(filename);
                if (!file.isFile())
                    throw new IllegalArgumentException("File does not exist: \"" + filename + "\"");
                files.add(file);
            }
            String driverName = cli.getOptionValue("driver");
            DriverOptions driverOptions = new DriverOptions(cli);
            WebDriverManager manager = WebDriverManager.getInstance();
            manager.setWebDriverFactory(driverName);
            manager.setDriverOptions(driverOptions);
            Runner runner = Binder.getRunner();
            runner.setDriver(manager.get());
            runner.setScreenshotDir(new File(cli.getOptionValue("screenshot-dir", new File(".").getAbsoluteFile().getParent())));
            runner.setScreenshotAll(cli.hasOption("screenshot-all"));
            runner.setBaseURI(cli.getOptionValue("baseuri"));
            Result totalResult = runner.run(files);
            exitCode = totalResult.exitCode();
        } catch (IllegalArgumentException e) {
            help("Error: " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        exit(exitCode);
    }

    public void exit(int exitCode) {
        System.exit(exitCode);
    }

    /**
     * Selenese Runner main.
     *
     * @param args options and filenames
     */
    public static void main(String[] args) {
        new Main().run(args);
    }
}
