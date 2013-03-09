package jp.vmi.selenium.selenese.utils;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class PathUtils {

    public static List<File> searchExecutableFile(final String filename) {
        List<File> result = new LinkedList<File>();
        for (String path : System.getenv("PATH").split(Pattern.quote(File.pathSeparator))) {
            File dir = new File(path);
            if (dir.isFile())
                dir = dir.getAbsoluteFile().getParentFile();
            result.addAll(Arrays.asList(dir.listFiles()));
        }

        return new LinkedList<File>(Collections2.filter(result, new Predicate<File>() {
            @Override
            public boolean apply(File file) {
                return file.canExecute() && file.getName().equals(filename);
            }
        }));
    }
}
