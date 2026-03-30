package command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a fully parsed user command, containing the command word (e.g., addsku, listtasks)
 * and a map of flag keys to values (e.g., "n" -> "COMPUTER-MOUSE-X1").
 */
//@@author omcodedthis
public class ParsedCommand {
    private final String commandWord;
    private final Map<String, String> args;

    /**
     * Constructs a ParsedCommand.
     *
     * @param commandWord The command keyword in lowercase.
     * @param args Map of flag keys (without slash) to their string values.
     */
    public ParsedCommand(String commandWord, Map<String, String> args) {
        if (commandWord == null) {
            throw new IllegalArgumentException("Command word cannot be null");
        }
        if (args == null) {
            throw new IllegalArgumentException("Arguments map cannot be null");
        }

        this.commandWord = commandWord.trim().toLowerCase();

        Map<String, String> normalizedArgs = new HashMap<>();
        for (Map.Entry<String, String> entry : args.entrySet()) {
            String cleanKey = (entry.getKey() == null) ? null : entry.getKey().trim().toLowerCase();
            normalizedArgs.put(cleanKey, entry.getValue());
        }

        this.args = Collections.unmodifiableMap(normalizedArgs);
    }

    /**
     * Returns the command word (always lowercase).
     *
     * @return Command word string.
     */
    public String getCommandWord() {
        return commandWord;
    }

    /**
     * Returns the value for the given flag key, or null if not present.
     *
     * @param key Flag key without slash (e.g., "n", "p").
     * @return Value string, or null.
     */
    public String getArg(String key) {
        if (key == null) {
            return null;
        }
        return args.get(key.toLowerCase());
    }

    /**
     * Returns a read-only set of all flag keys present in this command.
     * @return A set of all flag keys.
     */
    public Set<String> getAllFlags() {
        return args.keySet();
    }

    /**
     * Returns whether the given flag key is present in this command.
     *
     * @param key Flag key without slash.
     * @return true if flag is present, false otherwise.
     */
    public boolean hasArg(String key) {
        if (key == null) {
            return false;
        }
        return args.containsKey(key.toLowerCase());
    }
}
