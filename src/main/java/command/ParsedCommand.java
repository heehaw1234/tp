package command;

import java.util.Map;

/**
 * Represents a fully parsed user command, containing:
 * <ul>
 *   <li>The command word (e.g. {@code addsku}, {@code listtasks})</li>
 *   <li>A map of flag keys to values (e.g. {@code "n" -> "COMPUTER-MOUSE-X1"})</li>
 * </ul>
 */

//@@author omcodedthis
public class ParsedCommand {

    private final String commandWord;
    private final Map<String, String> args;

    /**
     * Constructs a ParsedCommand.
     *
     * @param commandWord The command keyword in lowercase.
     * @param args        Map of flag keys (without slash) to their string values.
     */
    public ParsedCommand(String commandWord, Map<String, String> args) {
        this.commandWord = commandWord;
        this.args = args;
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
     * Returns the value for the given flag key, or {@code null} if not present.
     *
     * @param key Flag key without slash (e.g. {@code "n"}, {@code "p"}).
     * @return Value string, or {@code null}.
     */
    public String getArg(String key) {
        return args.get(key.toLowerCase());
    }

    /**
     * Returns all flag keys present in this command.
     */
    public java.util.Set<String> getAllFlags() {
        return args.keySet();
    }

    /**
     * Returns whether the given flag key is present in this command.
     *
     * @param key Flag key without slash.
     * @return {@code true} if flag is present.
     */
    public boolean hasArg(String key) {
        return args.containsKey(key.toLowerCase());
    }
}
