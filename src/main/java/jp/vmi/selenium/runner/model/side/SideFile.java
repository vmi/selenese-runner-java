package jp.vmi.selenium.runner.model.side;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import jp.vmi.selenium.selenese.InvalidSeleneseException;
import jp.vmi.selenium.selenese.parser.ParserUtils;

/**
 * element of side format file.
 */
@SuppressWarnings("javadoc")
public class SideFile extends SideBase {

    private String url;
    private List<SideTest> tests = null;
    private List<SideSuite> suites = null;
    private List<Object> urls = null;

    public SideFile(boolean isGen) {
        super(isGen);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<SideTest> getTests() {
        return tests;
    }

    public void setTests(List<SideTest> tests) {
        this.tests = tests;
    }

    public void addTest(SideTest test) {
        if (tests == null)
            tests = new ArrayList<>();
        tests.add(test);
    }

    public List<SideSuite> getSuites() {
        return suites;
    }

    public void setSuites(List<SideSuite> suites) {
        this.suites = suites;
    }

    public void addSuite(SideSuite suite) {
        if (suites == null)
            suites = new ArrayList<>();
        suites.add(suite);
    }

    public List<Object> getUrls() {
        return urls;
    }

    public void setUrls(List<Object> urls) {
        this.urls = urls;
    }

    @SuppressWarnings("unchecked")
    private static <T> TypeAdapter<T> newSideSuiteAdapter(Gson gson) {
        return (TypeAdapter<T>) new TypeAdapter<SideSuite>() {
            @Override
            public void write(JsonWriter out, SideSuite suite) throws IOException {
                out.beginObject();
                out.name("parallel");
                out.value(suite.isParallel());
                out.name("timeout");
                out.value(suite.getTimeout());
                out.name("tests");
                out.beginArray();
                for (SideTest test : suite.getTests())
                    out.value(test.getId());
                out.endArray();
                out.name("id");
                out.value(suite.getId());
                out.name("name");
                out.value(suite.getName());
                out.endObject();
            }

            @Override
            public SideSuite read(JsonReader in) throws IOException {
                SideSuite suite = new SideSuite(false);
                in.beginObject();
                while (in.peek() != JsonToken.END_OBJECT) {
                    // expect JsonToken.NAME
                    switch (in.nextName()) {
                    case "id":
                        suite.setId(in.nextString());
                        break;
                    case "name":
                        suite.setName(in.nextString());
                        break;
                    case "parallel":
                        suite.setParallel(in.nextBoolean());
                        break;
                    case "timeout":
                        suite.setTimeout(in.nextInt());
                        break;
                    case "persistSession":
                        suite.setPersistSession(in.nextBoolean());
                        break;
                    case "tests":
                        in.beginArray();
                        List<SideTest> tests = new ArrayList<>();
                        while (in.peek() != JsonToken.END_ARRAY) {
                            SideTest test = new SideTest(false);
                            test.setId(in.nextString());
                            tests.add(test);
                        }
                        suite.setTests(tests);
                        in.endArray();
                        break;
                    default:
                        // unsupported member.
                        in.skipValue();
                        break;
                    }
                }
                in.endObject();
                return suite;
            }
        };
    }

    private static Gson newGson() {
        return new GsonBuilder()
            .registerTypeAdapterFactory(new TypeAdapterFactory() {
                @Override
                public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                    if (typeToken.getType() == SideSuite.class)
                        return newSideSuiteAdapter(gson);
                    else
                        return null;
                }
            }).create();
    }

    /**
     * Read side format data.
     *
     * @param filename filename of side format file. (only label)
     * @param is input stream of side format file.
     * @return SideFile data.
     * @throws InvalidSeleneseException invalid selenese exception.
     */
    public static SideProject parse(String filename, InputStream is) throws InvalidSeleneseException {
        try (Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return new SideProject(filename, newGson().fromJson(r, SideFile.class));
        } catch (IOException e) {
            throw new InvalidSeleneseException(e, filename, ParserUtils.getNameFromFilename(filename));
        }
    }
}
