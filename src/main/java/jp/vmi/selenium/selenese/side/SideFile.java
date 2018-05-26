package jp.vmi.selenium.selenese.side;

import java.util.List;

/**
 * element of side format file.
 */
@SuppressWarnings("javadoc")
public class SideFile extends SideBase {

    private String url;
    private List<SideTest> tests;
    private List<SideSuite> suites;
    private List<Object> urls;

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

    public List<SideSuite> getSuites() {
        return suites;
    }

    public void setSuites(List<SideSuite> suites) {
        this.suites = suites;
    }

    public List<Object> getUrls() {
        return urls;
    }

    public void setUrls(List<Object> urls) {
        this.urls = urls;
    }
}
