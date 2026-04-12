package ui;

import command.ParsedCommand;
import exception.InvalidCommandException;
import exception.MissingArgumentException;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses raw user input into structured commands.
 */

//@@author dorndorn54
public class Parser {

    /**
     * Parses the raw user input string into a ParsedCommand.
     *
     * @param input The raw input string from the user.
     * @return A ParsedCommand object containing the command word and flags.
     * @throws InvalidCommandException If the command syntax is invalid.
     * @throws MissingArgumentException If required arguments are missing from the input.
     */
    public static ParsedCommand parse(String input) throws InvalidCommandException, MissingArgumentException {
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

    /**
     * Parses a string of flags into a key-value map.
     *
     * @param argsString The string containing flags and values.
     * @return A map of flag keys to their respective values.
     * @throws InvalidCommandException If the flag syntax is invalid.
     */
    private static Map<String, String> parseFlags(String argsString) throws InvalidCommandException {
        Map<String, String> args = new HashMap<>();

        // Split at boundaries like " n/" " p/" " d/" " l/" " i/"
        String[] tokens = argsString.split("(?<=\\S)\\s+(?=[a-zA-Z]/)");

        for (String token : tokens) {
            int slashIdx = token.indexOf('/');

            if (slashIdx <= 0) {
                throw new InvalidCommandException(
                        "Invalid argument format: '" + token + "'. " +
                                "Make sure to use a prefix (e.g., n/, p/, l/)."
                );
            }
            String key = token.substring(0, slashIdx).trim().toLowerCase();
            String value = token.substring(slashIdx + 1).trim();
            if (args.containsKey(key)) {
                throw new InvalidCommandException(
                        "Duplicate flag '" + key + "/' is not allowed.");
            }
            args.put(key, value);
        }

        return args;
    }
}
