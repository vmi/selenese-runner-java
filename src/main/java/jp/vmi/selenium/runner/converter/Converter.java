package jp.vmi.selenium.runner.converter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jp.vmi.selenium.runner.model.side.SideFile;
import jp.vmi.selenium.selenese.Parser;
import jp.vmi.selenium.selenese.Selenese;
import jp.vmi.selenium.selenese.command.CommandFactory;

import static java.nio.file.StandardOpenOption.*;

/**
 * Convert Selenese to Side format.
 */
@SuppressWarnings("javadoc")
public class Converter {

    private static final Logger log = LoggerFactory.getLogger(Converter.class);

    private final Set<String> childFiles = new HashSet<>();
    private final Map<String, String> fileMap = new HashMap<>();

    public boolean convert(Path fromFile, Path toFile) {
        Selenese selenese = Parser.parse(fromFile.toString(), new CommandFactory());
        if (selenese.isError()) {
            System.out.printf("- Can't convert %s (%s)%n", fromFile, selenese.getResult());
            return false;
        }
        List<String> messages = new ArrayList<>();
        SideFile sideFile = Converters.convertSelenese(selenese, messages, childFiles);
        if (!messages.isEmpty())
            messages.forEach(message -> System.out.printf("- %s%n", message));
        if (sideFile == null) {
            System.out.printf("- Skip generating %s%n", toFile);
            return false;
        }
        Path toDir = toFile.getParent();
        if (!Files.isDirectory(toDir)) {
            try {
                Files.createDirectories(toDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(toFile, StandardCharsets.UTF_8, CREATE, TRUNCATE_EXISTING)) {
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
            gson.toJson(sideFile, sideFile.getClass(), writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileMap.put(fromFile.toString(), toFile.toString());
        return true;
    }

    public void removeGeneratedChildFiles() {
        childFiles.forEach(fromFile -> {
            String toFile = fileMap.get(fromFile);
            if (toFile != null) {
                try {
                    Files.delete(Paths.get(toFile));
                    System.out.printf("Removed %s%n", toFile);
                } catch (IOException e) {
                    log.warn(e.toString());
                }
            }
        });
    }

    /**
     * Converver main.
     *
     * @param args command line argument.
     */
    public static void main(String[] args) {
        ConverterOptions options = new ConverterOptions();
        if (args.length == 0)
            options.showHelp(new PrintWriter(System.out));
        args = options.parseArgs(args);
        ConvListGenerator convListGen = new ConvListGenerator(options);
        List<Path[]> convList;
        if (args.length == 0)
            convList = convListGen.generateConversionList();
        else
            convList = convListGen.generateConversionList(args);
        Converter converter = new Converter();
        convList.forEach(pair -> {
            Path fromFile = pair[0];
            Path toFile = pair[1];
            System.out.printf("Converting %s to %s%n", fromFile, toFile);
            converter.convert(fromFile, toFile);
        });
        converter.removeGeneratedChildFiles();
        System.out.println("Done.");
    }
}
