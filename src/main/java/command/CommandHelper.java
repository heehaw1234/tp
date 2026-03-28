package command;

import sku.Location;
import sku.SKU;
import sku.SKUList;
import skutask.Priority;
import skutask.SKUTask;
import ui.Ui;

/**
 * Provides shared static helper methods used across command handlers
 * for parsing and validating user input.
 */
public class CommandHelper {

    /**
     * Finds a SKU by ID using the SKUList, printing an error if not found.
     *
     * @param skuList The SKU data store to search.
     * @param skuId   The SKU identifier to search for.
     * @return The matching SKU, or null if not found (error is printed).
     */
    public static SKU findSkuOrError(SKUList skuList, String skuId) {
        SKU sku = skuList.findByID(skuId);
        if (sku == null) {
            Ui.printError("SKU not found: " + skuId);
        }
        return sku;
    }

    /**
     * Parses a location string into a Location enum value.
     * Prints an error and returns null if the string is not a valid location.
     *
     * @param locationStr The raw location string from user input.
     * @return The parsed Location, or null if invalid.
     */
    public static Location parseLocation(String locationStr) {
        try {
            return Location.valueOf(locationStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            Ui.printError("Invalid location '" + locationStr
                    + "'. Must be one of: A1 A2 A3 B1 B2 B3 C1 C2 C3");
            return null;
        }
    }

    /**
     * Parses a priority string into a Priority enum value.
     * Prints an error and returns null if the string is not a valid priority.
     *
     * @param priorityStr The raw priority string, or null.
     * @return The parsed Priority, or null if invalid or input was null.
     */
    public static Priority parsePriority(String priorityStr) {
        if (priorityStr == null) {
            return null;
        }
        try {
            return Priority.valueOf(priorityStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            Ui.printError("Invalid priority '" + priorityStr + "'. Use HIGH, MEDIUM, or LOW.");
            return null;
        }
    }

    /**
     * Parses the optional priority flag from a command, defaulting to HIGH if absent.
     *
     * @param cmd The parsed command to extract the priority from.
     * @return The parsed Priority (defaults to HIGH), or null if the provided value was invalid.
     */
    public static Priority parsePriorityOrDefault(ParsedCommand cmd) {
        if (!cmd.hasArg("p")) {
            return Priority.HIGH;
        }
        return parsePriority(cmd.getArg("p"));
    }

    /**
     * Parses a string into a 1-based integer index.
     * Prints an error and returns -1 if the input is not a valid number.
     *
     * @param indexStr The raw string input provided by the user.
     * @return The parsed integer index, or -1 if the input is not a valid integer.
     */
    public static int parseIndex(String indexStr) {
        try {
            return Integer.parseInt(indexStr.trim());
        } catch (NumberFormatException e) {
            Ui.printError("Task index must be a number, got: '" + indexStr + "'");
            return -1;
        }
    }

    /**
     * Checks if a task's description contains the given keyword (case-insensitive).
     * Returns true if no description filter is specified.
     *
     * @param task       The task to check.
     * @param descFilter The keyword to search for, or null to match all.
     * @return True if the task matches the filter.
     */
    public static boolean matchesDescription(SKUTask task, String descFilter) {
        if (descFilter == null) {
            return true;
        }
        String desc = task.getSKUTaskDescription();
        if (desc == null) {
            return false;
        }
        return desc.toLowerCase().contains(descFilter.toLowerCase());
    }
}
