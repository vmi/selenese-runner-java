package jp.vmi.selenium.selenese.locator;


import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class ClassHandler implements LocatorHandler {

   @Override
   public String locatorType() {
      return "class";
   }

   @Override
   public List<WebElement> handle(WebDriver driver, String arg) {
      By by;
      if (StringUtils.contains(arg, StringUtils.SPACE)) {
         //avoid InvalidSelectorError: Compound class names not permitted
         by = By.cssSelector("." + StringUtils.replace(arg, StringUtils.SPACE, "."));
      } else {
         by = By.className(arg);
      }
      return driver.findElements(by);
   }
}
