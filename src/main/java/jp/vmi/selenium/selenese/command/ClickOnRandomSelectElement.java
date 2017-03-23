package jp.vmi.selenium.selenese.command;

/**
 * Selects random element from drop-down lists by the xpath locator. Works correctly with drop-down lists with ul/li elements.
 *
 /* @param locator an element locator should return the list of elements by xpath.
 */
public class ClickOnRandomSelectElement extends ClickAt {

    // Fix me
    ClickOnRandomSelectElement(int index, String name, String... args) {
        super(index, name, args);
    }
}



