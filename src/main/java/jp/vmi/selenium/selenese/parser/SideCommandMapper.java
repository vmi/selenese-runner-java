package jp.vmi.selenium.selenese.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Side command mapper.
 */
public class SideCommandMapper {

    private final Map<String, String> commands = new HashMap<>();

    /**
     * Constructor.
     */
    public SideCommandMapper() {
        try (BufferedReader r = new BufferedReader(
            new InputStreamReader(SideCommandMapper.class.getResourceAsStream("/selenium-ide/Command.js"), StandardCharsets.UTF_8))) {
            while (!r.readLine().matches("\\s*export\\s+const\\s+Commands\\s+=.*"))
                /* Skip lines */;
            String line;
            while (!(line = r.readLine()).contains(");")) {
                String[] entry = line.trim().split("\\s*:\\s*\"", 2);
                String cmdName = entry[0];
                String cmdStr = entry[1].replaceFirst("\"\\s*,?", "");
                commands.put(cmdStr, cmdName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get mapped command.
     *
     * @param command command name in side file.
     * @return mapped command.
     */
    public String getMappedCommand(String command) {
        return commands.get(command);
    }

    /**
     * Get command map.
     *
     * @return command map.
     */
    public Map<String, String> getCommandMap() {
        return commands;
    }

    @Override
    public String toString() {
        return commands.toString();
    }
}
