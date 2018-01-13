package jp.vmi.selenium.selenese.side;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jp.vmi.selenium.selenese.InvalidSeleneseException;
import jp.vmi.selenium.selenese.parser.ParserUtils;

/**
 * parsed side format data.
 */
@SuppressWarnings("javadoc")
public class Side {

    private final String filename;
    private final String name;
    private final String id;
    private final String url;
    private final List<SideSuite> suites;
    private final Map<String, SideSuite> suiteMap = new HashMap<>();
    private final Map<String, SideTest> testMap = new HashMap<>();

    private Side(String filename, SideFile sideFile) {
        this.filename = filename;
        this.name = sideFile.getName();
        this.id = sideFile.getId();
        this.url = sideFile.getUrl();
        this.suites = sideFile.getSuites();
        suites.stream().forEach(suite -> suiteMap.put(suite.getId(), suite));
        sideFile.getTests().stream().forEach(test -> testMap.put(test.getId(), test));
    }

    public String getFilename() {
        return filename;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public List<SideSuite> getSuites() {
        return suites;
    }

    public Map<String, SideSuite> getSuiteMap() {
        return suiteMap;
    }

    public Map<String, SideTest> getTestMap() {
        return testMap;
    }

    /**
     * Read side format data.
     *
     * @param filename filename of side format file. (only label)
     * @param is input stream of side format file.
     * @return SideFile data.
     * @throws InvalidSeleneseException invalid selenese exception.
     */
    public static Side parse(String filename, InputStream is) throws InvalidSeleneseException {
        try (Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return new Side(filename, new Gson().fromJson(r, SideFile.class));
        } catch (IOException e) {
            throw new InvalidSeleneseException(e, filename, ParserUtils.getNameFromFilename(filename));
        }
    }

    /**
     * Serialize to JSON.
     *
     * @param object object.
     * @return JSON string.
     */
    public static String toJson(Object object) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(object);
    }
}
