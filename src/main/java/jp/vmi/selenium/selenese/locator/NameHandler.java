package jp.vmi.selenium.selenese.locator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.utils.SeleniumUtils;

class NameHandler implements LocatorHandler {

    // 1=index, 2=string-match pattern
    private static final Pattern FILTER_RE = Pattern.compile("index=(\\d+)|(?:value=)?(.*)");

    @Override
    public String locatorType() {
        return "name";
    }

    @Override
    public List<WebElement> handle(WebDriver driver, String arg) {
        String[] args = arg.split("\\s+", 2);
        List<WebElement> result = driver.findElements(By.name(args[0]));
        if (result.isEmpty() || args.length == 1)
            return result;
        List<WebElement> filtered = new ArrayList<>();
        Matcher matcher = FILTER_RE.matcher(args[1]);
        matcher.matches();
        String indexString = matcher.group(1);
        if (StringUtils.isNotEmpty(indexString)) {
            // use index
            int index = NumberUtils.toInt(indexString);
            if (index < result.size())
                filtered.add(result.get(index));
        } else {
            // use value
            String pattern = matcher.group(2);
            for (WebElement element : result) {
                try {
                    String value = element.getAttribute("value");
                    if (value != null && SeleniumUtils.patternMatches(pattern, value))
                        filtered.add(element);
                } catch (StaleElementReferenceException e) {
                    continue;
                }
            }
        }
        return filtered;
    }
}
