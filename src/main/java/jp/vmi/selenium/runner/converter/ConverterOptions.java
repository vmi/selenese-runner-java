package jp.vmi.selenium.runner.converter;

import java.io.PrintWriter;

import org.apache.commons.text.WordUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import jp.vmi.selenium.selenese.Main;
import jp.vmi.selenium.selenese.config.DefaultConfig;
import jp.vmi.selenium.selenese.utils.LangUtils;

@SuppressWarnings("javadoc")
public class ConverterOptions {

    private static final String[] HEADER = {
        "Convert Selenese script to Side script.",
        "",
        "Usage: java -jar selenese-runner.jar convert <options> ..."
    };

    private final CmdLineParser parser;
    private final int helpWidth;

    @Option(name = "--iext", aliases = "-i", metaVar = "<ext>", usage = "file extension of input files (default: html)")
    private String iext;

    @Option(name = "--oext", aliases = "-o", metaVar = "<ext>", usage = "file extension of output files (default: side)")
    private String oext;

    @Option(name = "--idir", aliases = "-I", metaVar = "<dir>", usage = "directory of input files (default: current directory)")
    private String idir;

    @Option(name = "--odir", aliases = "-O", metaVar = "<dir>", usage = "directory of output files (default: same as --idir)")
    private String odir;

    @Argument
    private String[] args = LangUtils.EMPTY_STRING_ARRAY;

    public ConverterOptions() {
        String columns = System.getProperty("columns", System.getenv("COLUMNS"));
        helpWidth = (columns != null && columns.matches("\\d+")) ? Integer.parseInt(columns) : DefaultConfig.HELP_WIDTH;
        ParserProperties props = ParserProperties.defaults()
            .withOptionSorter(null)
            .withUsageWidth(helpWidth)
            .withShowDefaults(false);
        parser = new CmdLineParser(this, props);
    }

    public String[] parseArgs(String... args) {
        try {
            parser.parseArgument(args);
            return this.args;
        } catch (CmdLineException e) {
            throw new RuntimeException(e);
        }
    }

    public String getIext() {
        return iext;
    }

    public void setIext(String iext) {
        this.iext = iext;
    }

    public String getOext() {
        return oext;
    }

    public void setOext(String oext) {
        this.oext = oext;
    }

    public String getIdir() {
        return idir;
    }

    public void setIdir(String idir) {
        this.idir = idir;
    }

    public String getOdir() {
        return odir;
    }

    public void setOdir(String odir) {
        this.odir = odir;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    /**
     * Show help message.
     *
     * @param pw PrintWriter.
     * @param msgs messages.
     */
    public void showHelp(PrintWriter pw, String... msgs) {
        if (msgs.length > 0) {
            for (String msg : msgs)
                System.out.println(msg);
            System.out.println();
        }
        int width = helpWidth - DefaultConfig.HELP_PADDING;
        pw.println(WordUtils.wrap(Main.PROG_TITLE + " " + Main.getVersion(), width));
        pw.println();
        for (String line : HEADER) {
            if (line.isEmpty())
                pw.println();
            else
                pw.println(WordUtils.wrap(line, width));
        }
        pw.println();
        parser.printUsage(pw, null);
        pw.println();
        pw.flush();
    }
}
