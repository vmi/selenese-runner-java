package jp.vmi.selenium.runner.converter;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Conversion list generator.
 */
@SuppressWarnings("javadoc")
public class ConvListGenerator {

    private Path inputDir = null;
    private Path outputDir = null;
    private String seleneseExt = ".html";
    private String sideExt = ".side";

    public ConvListGenerator(ConverterOptions options) {
        if (options.getIdir() != null)
            inputDir = Paths.get(options.getIdir());
        else
            inputDir = Paths.get(".");
        if (options.getOdir() != null)
            outputDir = Paths.get(options.getOdir());
        else
            outputDir = inputDir;
        if (options.getIext() != null)
            setSeleneseExt(options.getIext());
        if (options.getOext() != null)
            setSideExt(options.getOext());
    }

    public Path getInputDir() {
        return inputDir;
    }

    public void setInputDir(Path inputDir) {
        this.inputDir = inputDir;
    }

    public Path getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(Path outputDir) {
        this.outputDir = outputDir;
    }

    public String getSeleneseExt() {
        return seleneseExt;
    }

    public void setSeleneseExt(String seleneseExt) {
        if (seleneseExt.startsWith("."))
            this.seleneseExt = seleneseExt;
        else
            this.seleneseExt = "." + seleneseExt;
    }

    public String getSideExt() {
        return sideExt;
    }

    public void setSideExt(String sideExt) {
        if (sideExt.startsWith("."))
            this.sideExt = sideExt;
        else
            this.sideExt = "." + sideExt;
    }

    private Path getOutputFile(Path inputFile) {
        Path rDir = inputDir.relativize(inputFile.getParent());
        Path oDir = outputDir.resolve(rDir);
        String inputName = inputFile.getFileName().toString();
        String outputName = inputName.substring(0, inputName.length() - seleneseExt.length()) + sideExt;
        return oDir.resolve(outputName);
    }

    public List<Path[]> generateConversionList() {
        try {
            return Files.walk(inputDir, FileVisitOption.FOLLOW_LINKS)
                .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(seleneseExt))
                .sorted()
                .map(path -> new Path[] { path, getOutputFile(path) })
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Path[]> generateConversionList(String... args) {
        List<Path[]> list = new ArrayList<>();
        int i = 0;
        Path[] pair = new Path[2];
        while (i < args.length) {
            String name = args[i];
            if (pair[0] == null) {
                if (name.endsWith(seleneseExt))
                    pair[0] = Paths.get(name);
                else if (name.endsWith(sideExt))
                    throw new IllegalArgumentException("no input file for " + name);
                else
                    throw new IllegalArgumentException("illegal argument: " + name);
            } else {
                if (name.endsWith(sideExt)) {
                    pair[1] = Paths.get(name);
                    list.add(pair);
                    pair = new Path[2];
                } else if (name.endsWith(seleneseExt)) {
                    pair[1] = getOutputFile(pair[0]);
                    list.add(pair);
                    pair = new Path[2];
                    pair[0] = Paths.get(name);
                } else {
                    throw new IllegalArgumentException("illegal argument: " + name);
                }
            }
        }
        if (pair[0] != null) {
            pair[1] = getOutputFile(pair[0]);
            list.add(pair);
        }
        return list;
    }
}
