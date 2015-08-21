package jp.vmi.selenium.selenese.locator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.utils.SeleniumUtils.SeleniumPattern;

class LinkHandler implements LocatorHandler {

    @Override
    public String locatorType() {
        return "link";
    }

    private List<WebElement> findByRegexp(WebDriver driver, By by, Pattern pattern) {
        List<WebElement> result = new ArrayList<WebElement>();
        List<WebElement> as = driver.findElements(by);
        for (WebElement a : as) {
            String text;
            try {
                if ((text = a.getText()) == null)
                    text = "";
            } catch (StaleElementReferenceException e) {
                continue;
            }
            Matcher matcher = pattern.matcher(text);
            if (matcher.find())
                result.add(a);
        }
        return result;
    }

    private void appendXPathString(StringBuilder xpath, String s) {
        if (s.indexOf('"') >= 0)
            xpath.append("concat(\"").append(s.replace("\"", "\",'\"',\"")).append("\")");
        else
            xpath.append('"').append(s).append('"');
    }

    private List<WebElement> findByGlobString(WebDriver driver, SeleniumPattern sp) {
        boolean and = false;
        StringBuilder xpath = new StringBuilder("//a[");
        for (String ss : sp.stringPattern.split("[*?]+")) {
            if (StringUtils.isEmpty(ss))
                continue;
            if (and)
                xpath.append(" and ");
            xpath.append("contains(.,");
            appendXPathString(xpath, ss);
            xpath.append(")]");
            and = true;
        }
        By by = and ? By.xpath(xpath.toString()) : By.tagName("a");
        return findByRegexp(driver, by, sp.regexpPattern);
    }

    private List<WebElement> findByExactString(WebDriver driver, SeleniumPattern sp) {
        StringBuilder xpath = new StringBuilder("//a[.=");
        appendXPathString(xpath, sp.stringPattern);
        xpath.append(']');
        return driver.findElements(By.xpath(xpath.toString()));
    }

    @Override
    public List<WebElement> handle(WebDriver driver, String arg) {
        SeleniumPattern sp = new SeleniumPattern(arg);
        switch (sp.type) {
        case REGEXP:
        case REGEXPI:
            return findByRegexp(driver, By.tagName("a"), sp.regexpPattern);
        case GLOB:
            return findByGlobString(driver, sp);
        case EXACT:
            return findByExactString(driver, sp);
        default:
            throw new UnsupportedOperationException(arg);
        }
    }
}
