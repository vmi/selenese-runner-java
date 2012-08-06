package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.Command.Result;
import jp.vmi.selenium.selenese.inject.ExecuteTestSuite;

public class TestSuite implements Selenese {

    private File file;
    private String parentDir;
    private String name;
    private final List<File> files = new ArrayList<File>();

    public TestSuite initialize(File file) {
        try {
            this.file = file;
            this.parentDir = file.getCanonicalFile().getParent();
            this.name = file.getName().replaceFirst("\\.[^.]+$", "");
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTestCase(String filename) {
        File tcFile = new File(filename);
        if (!tcFile.isAbsolute())
            tcFile = new File(parentDir, filename);
        files.add(tcFile);
    }

    @ExecuteTestSuite
    @Override
    public Result execute(Runner runner) {
        Result totalResult = Command.SUCCESS;
        for (File file : files)
            totalResult = totalResult.update(runner.run(file));
        return totalResult;
    }

    @Override
    public String toString() {
        return "TestSuite[" + name + "] (" + file + ")";
    }
}
