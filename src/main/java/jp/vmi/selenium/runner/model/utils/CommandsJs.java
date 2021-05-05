package jp.vmi.selenium.runner.model.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jp.vmi.selenium.selenese.utils.EscapeUtils;

/**
 * Instance of "Commands.js".
 */
public class CommandsJs {

    private final Map<String, String> targetTypes = new LinkedHashMap<>();
    private final Map<String, Map<String, String>> commands = new LinkedHashMap<>();

    private CommandsJs() {
        // no operation.
    }

    /**
     * Get TargetTypes.
     *
     * @return TargetTypes.
     */
    public Map<String, String> getTargetTypes() {
        return targetTypes;
    }

    /**
     * Get Commands.
     *
     * @return Commands.
     */
    public Map<String, Map<String, String>> getCommands() {
        return commands;
    }

    private static final String COMMANDS_JS = "/selenium-ide/Commands.js";

    private static enum State {

        // for top level
        TOP_LEVEL(State::processTopLevel),

        // for TargetTypes
        TARGET_TYPES(State::processTargetTypes),

        // for Commands
        COMMANDS(State::processCommands),

        ;

        private static State processTopLevel(Parser parser, String line) {
            if (line.matches("export\\s+const\\s+TargetTypes\\s+=\\s+\\{\\s*")) {
                parser.targetTypes.add("{");
                return TARGET_TYPES;
            } else if (line.matches("export\\s+const\\s+Commands\\s*=\\s*\\[\\s*")) {
                parser.commands.add("[");
                return COMMANDS;
            } else {
                return TOP_LEVEL;
            }
        }

        private static State processTargetTypes(Parser parser, String line) {
            if (line.matches("\\}\\s*")) {
                parser.targetTypes.removeTrailingComma().add("}");
                return TOP_LEVEL;
            } else {
                parser.targetTypes.add(line);
                return TARGET_TYPES;
            }
        }

        private static final Pattern OPTIONAL_VALUE = Pattern.compile(
            "(?<indent>\\s*)value:\\s*\\{\\s*isOptional:\\s*true,\\s*\\.{3}(?<value>.*?)\\s*\\},\\s*");

        private static State processCommands(Parser parser, String line) {
            if (line.matches("\\]\\s*")) {
                parser.commands.removeTrailingComma().add("]");
                return TOP_LEVEL;
            } else if (line.matches("\\s*[\\]\\}].*")) {
                parser.commands.removeTrailingComma();
            }
            Matcher matcher = OPTIONAL_VALUE.matcher(line);
            if (matcher.matches()) {
                String indent = matcher.group("indent");
                String value = matcher.group("value");
                line = indent + "value: " + value + ",\n"
                    + indent + "valueIsOptional: true,";
            }
            parser.commands.add(line);
            return COMMANDS;
        }

        private final BiFunction<Parser, String, State> processor;

        private State(BiFunction<Parser, String, State> processor) {
            this.processor = processor;
        }
    }

    private static class Lines {
        private final StringBuilder body = new StringBuilder();
        private boolean isInMLStr = false;

        private void append(String s) {
            body.append(isInMLStr ? EscapeUtils.escapeJSString(s) : s);
        }

        public Lines add(String line) {
            if (body.length() > 0)
                body.append(' ');
            line = line.trim();
            int bqIndex = line.indexOf('`');
            if (bqIndex < 0) {
                append(line);
                return this;
            }
            int offset = 0;
            do {
                if (offset < bqIndex) {
                    String ss = line.substring(offset, bqIndex);
                    append(ss);
                }
                body.append('"');
                isInMLStr = !isInMLStr;
                offset = bqIndex + 1;
            } while ((bqIndex = line.indexOf('`', offset)) >= 0);
            if (offset < line.length())
                append(line.substring(offset));
            return this;
        }

        public Lines removeTrailingComma() {
            int tail = body.length() - 1;
            if (tail >= 0 && body.charAt(tail) == ',') {
                body.deleteCharAt(tail);
            }
            return this;
        }

        @Override
        public String toString() {
            return body.toString();
        }
    }

    private static class Parser {

        private State state = State.TOP_LEVEL;
        private final Lines targetTypes = new Lines();
        private final Lines commands = new Lines();

        private void parseLine(String line) {
            state = state.processor.apply(this, line);
        }

        @SuppressWarnings("unchecked")
        private Map<String, Object> getTargetTypes() {
            String json = targetTypes.toString();
            return new Gson().fromJson(json, Map.class);
        }

        @SuppressWarnings("unchecked")
        private List<List<Object>> getCommands() {
            String json = commands.toString();
            return new Gson().fromJson(json, List.class);
        }
    }

    /**
     * Load "Commands.js".
     *
     * @return "Commands.js" instance.
     */
    public static CommandsJs load() {
        Parser parser = new Parser();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(CommandsJs.class.getResourceAsStream(COMMANDS_JS), StandardCharsets.UTF_8))) {
            r.lines().forEach(parser::parseLine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CommandsJs commandJs = new CommandsJs();
        parser.getTargetTypes().forEach((key, value) -> {
            if (value instanceof Number && ((Number) value).intValue() == 0)
                commandJs.targetTypes.put(key, "");
            else
                commandJs.targetTypes.put(key, (String) value);
        });
        parser.getCommands().forEach(item -> {
            String key = (String) item.get(0);
            @SuppressWarnings("unchecked")
            Map<String, String> value = (Map<String, String>) item.get(1);
            // workaround.
            switch (key) {
            case "assertConfirmation":
            case "assertPrompt":
                if (!value.containsKey("target"))
                    value.put("target", "ArgTypes.alertText");
                break;
            case "storeTitle":
                if (!value.containsKey("value"))
                    value.put("value", "ArgTypes.variableName");
                break;
            }
            commandJs.commands.put(key, value);
        });
        return commandJs;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
