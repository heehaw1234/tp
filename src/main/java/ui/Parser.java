package ui;

import java.util.HashMap;
import java.util.Map;

public class Parser {
    public static ParsedCommand parse(String input){
        assert input != null : "Input Should not be null";

        String trimmed = input.trim();
        if(trimmed.isEmpty()){
            return new ParsedCommand("", new HashMap<>());
        }

        String[] parts = trimmed.split("\\s+", 2);
        String commandWord = parts[0].toLowerCase();

        Map<String, String> args = new HashMap<>();
        if(parts.length > 1){
            args = parseFlags(parts[1].trim());
        }

        return new ParsedCommand(commandWord, args);
    }

    private static Map<String, String> parseFlags(String argsString) {
        Map<String, String> args = new HashMap<>();

        // Split at boundaries like " n/" " p/" " d/" " l/" " i/"
        String[] tokens = argsString.split("(?<=\\S)\\s+(?=[a-zA-Z]/)");

        for (String token : tokens) {
            int slashIdx = token.indexOf('/');
            if (slashIdx > 0) {
                String key = token.substring(0, slashIdx).trim().toLowerCase();
                String value = token.substring(slashIdx + 1).trim();
                args.put(key, value);
            }
        }

        return args;
    }
}
