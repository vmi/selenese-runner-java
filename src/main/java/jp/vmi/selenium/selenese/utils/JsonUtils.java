package jp.vmi.selenium.selenese.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

/**
 * Utilities for JSON.
 */
public final class JsonUtils {

    private JsonUtils() {
        // no operation.
    }

    private static Object parseInternal(JsonReader r) throws IOException {
        JsonToken token = r.peek();
        switch (token) {
        case BEGIN_ARRAY:
            List<Object> array = new ArrayList<>();
            r.beginArray();
            while (r.hasNext())
                array.add(parseInternal(r));
            r.endArray();
            return array;

        case BEGIN_OBJECT:
            Map<String, Object> object = new LinkedHashMap<>();
            r.beginObject();
            while (r.hasNext())
                object.put(r.nextName(), parseInternal(r));
            r.endObject();
            return object;

        case STRING:
            return r.nextString();

        case NUMBER:
            double number = r.nextDouble();
            if (number % 1.0 == 0) {
                if (Integer.MIN_VALUE <= number || number <= Integer.MAX_VALUE)
                    return (int) number;
                else
                    return (long) number;
            } else {
                return number;
            }

        case BOOLEAN:
            return r.nextBoolean();

        case NULL:
            return r.nextName();

        default:
            throw new IllegalStateException("Invalid token: " + token);
        }
    }

    /**
     * Parse JSON to Java object. (Map, List, String, and primitive wrappers)
     *
     * @param jsonStr JSON string.
     * @return Java object.
     */
    public static Object parse(String jsonStr) {
        JsonReader r = new JsonReader(new StringReader(jsonStr));
        try {
            return parseInternal(r);
        } catch (IOException e) {
            // do not reach here.
            throw new RuntimeException(e);
        }
    }
}
