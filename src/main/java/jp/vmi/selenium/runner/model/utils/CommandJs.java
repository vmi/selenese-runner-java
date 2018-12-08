package jp.vmi.selenium.runner.model.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Instance of "Command.js".
 */
public class CommandJs {

    private final Map<String, String> targetTypes = new LinkedHashMap<>();
    private final Map<String, Map<String, String>> argTypes = new LinkedHashMap<>();
    private final Map<String, Map<String, String>> commandList = new LinkedHashMap<>();
    private final Map<String, String> controlFlowCommandNames = new LinkedHashMap<>();

    private CommandJs() {
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
     * Get ArgTypes.
     *
     * @return ArgTypes.
     */
    public Map<String, Map<String, String>> getArgTypes() {
        return argTypes;
    }

    /**
     * Get CommandList.
     *
     * @return CommandList.
     */
    public Map<String, Map<String, String>> getCommandList() {
        return commandList;
    }

    /**
     * Get ControlFlowCommandNames.
     *
     * @return ControlFlowCommandNames.
     */
    public Map<String, String> getControlFlowCommandNames() {
        return controlFlowCommandNames;
    }

    private static final String COMMAND_JS = "/selenium-ide/Command.js";

    private static enum State {

        // for top level
        TOP_LEVEL(State::processTopLevel),

        // for TargetTypes
        TARGET_TYPES(State::processTargetTypes),

        // for ArgTypes
        ARG_TYPES(State::processArgTypes),

        // for CommandList
        COMMAND_LIST(State::processCommandList),

        // for ControlFlowCommandNames
        CONTROL_FLOW_COMMAND_NAMES(State::processControlFlowCommandNames),

        ;

        private static State processTopLevel(Parser parser, String line) {
            if (line.matches("export\\s+const\\s+TargetTypes\\s+=\\s+\\{\\s*")) {
                parser.targetTypes.add("{");
                return TARGET_TYPES;
            } else if (line.matches("export\\s+const\\s+ArgTypes\\s+=\\s+\\{\\s*")) {
                parser.argTypes.add("{");
                return ARG_TYPES;
            } else if (line.matches("class\\s+CommandList\\s*\\{\\s*")) {
                return COMMAND_LIST;
            } else if (line.matches("export\\s+const\\s+ControlFlowCommandNames\\s+=\\s+\\{\\s*")) {
                parser.controlFlowCommandNames.add("{");
                return CONTROL_FLOW_COMMAND_NAMES;
            } else {
                return TOP_LEVEL;
            }
        }

        private static void addLine(List<String> list, String line) {
            if (list.isEmpty()) {
                list.add(line);
                return;
            }
            int lastIndex = list.size() - 1;
            String lastLine = list.get(lastIndex);
            if (lastLine.endsWith("\\"))
                list.set(lastIndex, lastLine.replaceFirst("\\s*\\\\$", " ") + line.trim());
            else if (line.matches("\\s*[\\}\\]],\\s*"))
                removeTrailingComma(list).add(line);
            else
                list.add(line);
        }

        private static List<String> removeTrailingComma(List<String> list) {
            if (list.isEmpty())
                return list;
            int lastIndex = list.size() - 1;
            String lastLine = list.get(lastIndex);
            list.set(lastIndex, lastLine.replaceFirst(",\\s*$", ""));
            return list;
        }

        private static State processTargetTypes(Parser parser, String line) {
            if (line.matches("\\}\\s*")) {
                removeTrailingComma(parser.targetTypes).add("}");
                return TOP_LEVEL;
            } else {
                addLine(parser.targetTypes, line);
                return TARGET_TYPES;
            }
        }

        private static State processArgTypes(Parser parser, String line) {
            if (line.matches("\\}\\s*")) {
                removeTrailingComma(parser.argTypes).add("}");
                return TOP_LEVEL;
            } else {
                addLine(parser.argTypes, line);
                return ARG_TYPES;
            }
        }

        private static State processCommandList(Parser parser, String line) {
            if (parser.commandList.isEmpty()) {
                if (line.matches("\\s*list\\s+=\\s+new\\s+Map\\(\\[\\s*")) {
                    parser.commandList.add("[");
                }
            } else {
                if (line.matches("\\s*\\]\\)\\s*")) {
                    removeTrailingComma(parser.commandList).add("]");
                    return TOP_LEVEL;
                }
                addLine(parser.commandList, line);
            }
            return COMMAND_LIST;
        }

        private static State processControlFlowCommandNames(Parser parser, String line) {
            if (line.matches("\\}\\s*")) {
                removeTrailingComma(parser.controlFlowCommandNames).add("}");
                return TOP_LEVEL;
            } else {
                addLine(parser.controlFlowCommandNames, line);
                return State.CONTROL_FLOW_COMMAND_NAMES;
            }
        }

        private final BiFunction<Parser, String, State> processor;

        private State(BiFunction<Parser, String, State> processor) {
            this.processor = processor;
        }
    }

    private static class Parser {

        private State state = State.TOP_LEVEL;
        private final List<String> targetTypes = new ArrayList<>();
        private final List<String> argTypes = new ArrayList<>();
        private final List<String> commandList = new ArrayList<>();
        private final List<String> controlFlowCommandNames = new ArrayList<>();

        private void parseLine(String line) {
            state = state.processor.apply(this, line);
        }

        private String join(List<String> list) {
            return list.stream().collect(Collectors.joining("\n"));
        }

        @SuppressWarnings("unchecked")
        private Map<String, Object> getTargetTypes() {
            String json = join(targetTypes);
            return new Gson().fromJson(json, Map.class);
        }

        @SuppressWarnings("unchecked")
        private Map<String, Map<String, String>> getArgTypes() {
            String json = join(argTypes);
            return new Gson().fromJson(json, Map.class);
        }

        @SuppressWarnings("unchecked")
        private List<List<Object>> getCommandList() {
            String json = join(commandList);
            return new Gson().fromJson(json, List.class);
        }

        @SuppressWarnings("unchecked")
        private Map<String, String> getControlFlowCommandNames() {
            String json = join(controlFlowCommandNames);
            return new Gson().fromJson(json, Map.class);
        }
    }

    /**
     * Load "Command.js".
     *
     * @return "Command.js" instance.
     */
    public static CommandJs load() {
        Parser parser = new Parser();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(CommandJs.class.getResourceAsStream(COMMAND_JS), StandardCharsets.UTF_8))) {
            r.lines().forEach(parser::parseLine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CommandJs commandJs = new CommandJs();
        parser.getTargetTypes().forEach((key, value) -> {
            if (value instanceof Number && ((Number) value).intValue() == 0)
                commandJs.targetTypes.put(key, "");
            else
                commandJs.targetTypes.put(key, (String) value);
        });
        parser.getArgTypes().forEach(commandJs.argTypes::put);
        parser.getCommandList().forEach(item -> {
            String key = (String) item.get(0);
            @SuppressWarnings("unchecked")
            Map<String, String> value = (Map<String, String>) item.get(1);
            commandJs.commandList.put(key, value);
        });
        parser.getControlFlowCommandNames().forEach(commandJs.controlFlowCommandNames::put);
        return commandJs;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
