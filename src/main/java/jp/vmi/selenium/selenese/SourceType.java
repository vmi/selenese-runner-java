package jp.vmi.selenium.selenese;

/**
 * Test-case/test-suite source type.
 */
@SuppressWarnings("javadoc")
public enum SourceType {
    SELENESE, SIDE,
    ;

    public boolean isSelenese() {
        return this == SourceType.SELENESE;
    }

    public boolean isSide() {
        return this == SourceType.SIDE;
    }

}
