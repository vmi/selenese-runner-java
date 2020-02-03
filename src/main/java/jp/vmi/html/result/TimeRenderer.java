package jp.vmi.html.result;

import com.floreysoft.jmte.NamedRenderer;
import com.floreysoft.jmte.RenderFormatInfo;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

/**
 * Time with millisecond renderer for JMTE.
 */
public class TimeRenderer implements NamedRenderer {

    @Override
    public RenderFormatInfo getFormatInfo() {
        return null;
    }

    @Override
    public String getName() {
        return "t";
    }

    @Override
    public Class<?>[] getSupportedClasses() {
        return new Class[] { long.class, Long.class };
    }
    @Override
    public String render(Object o, String format, Locale locale, Map<String, Object> model) {
        long value = ((Number) o).longValue();
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault());
        return DateTimeFormatter.ofPattern(format).format(dateTime);
    }
}
