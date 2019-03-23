package jp.vmi.selenium.selenese;

import java.lang.reflect.Field;

/**
 * Command arguments inforamtion.
 */
public interface ArgumentInfo {

    /**
     * Get argument count.
     *
     * @return argument count.
     */
    default int getArgumentCount() {
        int count = 0;
        for (Field f : getClass().getDeclaredFields()) {
            String name = f.getName();
            if (!name.startsWith("ARG_") || f.getType() != int.class)
                continue;
            try {
                f.setAccessible(true);
                int offset = (int) f.get(null);
                if (count < offset + 1)
                    count = offset + 1;
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // no operation.
            }
        }
        return count;
    }
}
