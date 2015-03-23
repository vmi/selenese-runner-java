package jp.vmi.selenium.selenese.utils;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Date/Time formatter by FastDateFormat.
 */
public class FDate extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return DateTimeUtils.formatWithMS(event.getTimeStamp());
    }
}
