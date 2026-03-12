package ui;

import sku.Location;
import sku.SKU;
import sku.SKUList;

import skutask.Priority;
import skutask.SKUTask;
import skutask.SKUTaskList;
import skutask.ViewSKUTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Executes {@link ParsedCommand} instances against the warehouse data model.
 *
 * <p><b>Design note:</b> Since {@code SKU} does not yet hold a {@code SKUTaskList}
 * field directly (it is commented out in SKU.java), this class maintains an internal
 * {@code HashMap<String, SKUTaskList>} keyed by SKU_ID. Once Om's team uncomments
 * and wires in {@code SKUTaskList} inside {@code SKU}, this map can be replaced by
 * delegating straight to the {@code SKU} object.
 *
 * <p><b>Dependency note:</b> {@code findSku()} calls {@code skuList.getSKUList()},
 * which requires adding a {@code getSKUList()} method to {@code SKUList.java}:
 * <pre>
 *   public ArrayList{@literal <SKU>} getSKUList() { return skuList; }
 * </pre>
 */
public class CommandRunner {

    /** Set to false to stop the main loop. */
    private boolean isRunning;

    private final SKUList skuList;

    /**
     * Internal task storage keyed by SKU_ID.
     * Remove this once SKU holds its own SKUTaskList.
     */
    private final HashMap<String, SKUTaskList> taskMap;

    /**
     * Constructs a CommandRunner backed by the given {@link SKUList}.
     *
     * @param skuList The shared SKU data store.
     */
    public CommandRunner(SKUList skuList) {
        this.skuList = skuList;
        this.taskMap = new HashMap<>();
        this.isRunning = true;
    }

    /**
     * Returns {@code true} while the application should keep running.
     *
     * @return Running state.
     */
    public boolean isRunning() {
        return isRunning;
    }

    // =========================================================
    // Command dispatch
    // =========================================================

    /**
     * Dispatches a {@link ParsedCommand} to the appropriate handler.
     *
     * @param cmd The parsed command to execute.
     */
    public void run(ParsedCommand cmd) {
        assert cmd != null : "ParsedCommand should not be null";

        switch (cmd.getCommandWord()) {
            case "addsku":
                handleAddSku(cmd);
                break;
            case "deletesku":
                handleDeleteSku(cmd);
                break;
            case "addskutask":
                handleAddSkuTask(cmd);
                break;
            case "deletetask":
                handleDeleteTask(cmd);
                break;
            case "marktask":
                handleMarkTask(cmd);
                break;
            case "unmarktask":
                handleUnmarkTask(cmd);
                break;
            case "listtasks":
                handleListTasks(cmd);
                break;
            case "help":
                Ui.printHelp();
                break;
            case "bye":
                // falls through
            case "exit":
                Ui.printGoodbye();
                isRunning = false;
                break;
            case "":
                break; // empty input — do nothing
            default:
                Ui.printUnknownCommand(cmd.getCommandWord());
        }
    }

    // =========================================================
    // SKU handlers
    // =========================================================

    /**
     * Handles {@code addsku n/SKU_ID l/LOCATION}.
     * Validates that the location is a legal 3x3 sector and that the SKU is not a duplicate.
     */
    private void handleAddSku(ParsedCommand cmd) {
        String skuId = cmd.getArg("n");
        String locationStr = cmd.getArg("l");

        if (skuId == null || locationStr == null) {
            Ui.printError("Usage: addsku n/SKU_ID l/LOCATION  (e.g. addsku n/WIDGET-A1 l/B2)");
            return;
        }

        Location location;
        try {
            location = Location.valueOf(locationStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            Ui.printError("Invalid location '" + locationStr + "'. Must be one of: A1 A2 A3 B1 B2 B3 C1 C2 C3");
            return;
        }

        if (findSku(skuId) != null) {
            Ui.printError("SKU already exists: " + skuId);
            return;
        }

        skuList.addSKU(skuId, location);
        taskMap.put(skuId.toUpperCase(), new SKUTaskList());
        Ui.printSuccess("Added SKU [" + skuId.toUpperCase() + "] at location " + location);
    }

    /**
     * Handles {@code deletesku n/SKU_ID}.
     * Also removes all tasks associated with the SKU.
     */
    private void handleDeleteSku(ParsedCommand cmd) {
        String skuId = cmd.getArg("n");
        if (skuId == null) {
            Ui.printError("Usage: deletesku n/SKU_ID");
            return;
        }
        if (findSku(skuId) == null) {
            Ui.printError("SKU not found: " + skuId);
            return;
        }
        skuList.deleteSKU(skuId);
        taskMap.remove(skuId.toUpperCase());
        Ui.printSuccess("Deleted SKU [" + skuId.toUpperCase() + "] and all its tasks.");
    }

    // =========================================================
    // Task handlers
    // =========================================================

    /**
     * Handles {@code addskutask n/SKU_ID d/DUE_DATE [p/PRIORITY]}.
     * Priority defaults to HIGH if {@code p/} flag is omitted.
     */
    private void handleAddSkuTask(ParsedCommand cmd) {
        String skuId = cmd.getArg("n");
        String dueDate = cmd.getArg("d");

        if (skuId == null || dueDate == null) {
            Ui.printError("Usage: addskutask n/SKU_ID d/DUE_DATE [p/PRIORITY]");
            return;
        }
        if (findSku(skuId) == null) {
            Ui.printError("SKU not found: " + skuId
                    + ". Use 'addsku' to register it first.");
            return;
        }

        Priority priority = Priority.HIGH;
        if (cmd.hasArg("p")) {
            try {
                priority = Priority.valueOf(cmd.getArg("p").toUpperCase());
            } catch (IllegalArgumentException e) {
                Ui.printError("Invalid priority '" + cmd.getArg("p") + "'. Use HIGH, MEDIUM, or LOW.");
                return;
            }
        }

        SKUTaskList taskList = getOrCreateTaskList(skuId);
        taskList.addSKUTask(skuId.toUpperCase(), priority, dueDate);
        int newIndex = taskList.getSize();
        Ui.printSuccess("Added task #" + newIndex + " to SKU [" + skuId.toUpperCase()
                + "] | Priority: " + priority + " | Due: " + dueDate);
    }

    /**
     * Handles {@code deletetask n/SKU_ID i/TASK_INDEX}.
     * Removes the task at the 1-based index shown by {@code listtasks}.
     */
    private void handleDeleteTask(ParsedCommand cmd) {
        String skuId = cmd.getArg("n");
        String indexStr = cmd.getArg("i");

        if (skuId == null || indexStr == null) {
            Ui.printError("Usage: deletetask n/SKU_ID i/TASK_INDEX");
            return;
        }

        int index = parseIndex(indexStr);
        if (index == -1) {
            return;
        }

        SKUTaskList taskList = taskMap.get(skuId.toUpperCase());
        if (taskList == null || index < 1 || index > taskList.getSize()) {
            Ui.printError("Task index " + index + " is out of range for SKU: " + skuId);
            return;
        }

        taskList.getSKUTaskList().remove(index - 1);
        Ui.printSuccess("Deleted task #" + index + " from SKU [" + skuId.toUpperCase() + "].");
    }

    /**
     * Handles {@code marktask n/SKU_ID i/TASK_INDEX}.
     * Marks the task at the given 1-based index as completed.
     */
    private void handleMarkTask(ParsedCommand cmd) {
        String skuId = cmd.getArg("n");
        String indexStr = cmd.getArg("i");

        if (skuId == null || indexStr == null) {
            Ui.printError("Usage: marktask n/SKU_ID i/TASK_INDEX");
            return;
        }

        int index = parseIndex(indexStr);
        if (index == -1) {
            return;
        }

        SKUTaskList taskList = taskMap.get(skuId.toUpperCase());
        if (taskList == null || index < 1 || index > taskList.getSize()) {
            Ui.printError("Task index " + index + " is out of range for SKU: " + skuId);
            return;
        }

        taskList.markTask(index);
        Ui.printSuccess("Marked task #" + index + " as done for SKU [" + skuId.toUpperCase() + "].");
    }

    /**
     * Handles {@code unmarktask n/SKU_ID i/TASK_INDEX}.
     * Unmarks the task at the given 1-based index.
     */
    private void handleUnmarkTask(ParsedCommand cmd) {
        String skuId = cmd.getArg("n");
        String indexStr = cmd.getArg("i");

        if (skuId == null || indexStr == null) {
            Ui.printError("Usage: unmarktask n/SKU_ID i/TASK_INDEX");
            return;
        }

        int index = parseIndex(indexStr);
        if (index == -1) {
            return;
        }

        SKUTaskList taskList = taskMap.get(skuId.toUpperCase());
        if (taskList == null || index < 1 || index > taskList.getSize()) {
            Ui.printError("Task index " + index + " is out of range for SKU: " + skuId);
            return;
        }

        taskList.unmarkTask(index);
        Ui.printSuccess("Unmarked task #" + index + " for SKU [" + skuId.toUpperCase() + "].");
    }

    // =========================================================
    // List / view handlers
    // =========================================================

    /**
     * Dispatches the {@code listtasks} command to the appropriate sub-view
     * based on which flag is present ({@code n/}, {@code p/}, or {@code l/}).
     * With no flags, all tasks are shown.
     */
    private void handleListTasks(ParsedCommand cmd) {
        if (cmd.hasArg("n")) {
            listTasksBySku(cmd.getArg("n"));
        } else if (cmd.hasArg("p")) {
            listTasksByPriority(cmd.getArg("p"));
        } else if (cmd.hasArg("l")) {
            listTasksByDistance(cmd.getArg("l"));
        } else {
            listAllTasks();
        }
    }

    /**
     * Lists all tasks belonging to a specific SKU.
     *
     * @param skuId The SKU_ID to look up.
     */
    private void listTasksBySku(String skuId) {
        SKUTaskList taskList = taskMap.get(skuId.toUpperCase());
        if (taskList == null || taskList.isEmpty()) {
            Ui.printInfo("No tasks found for SKU: " + skuId.toUpperCase());
            return;
        }
        System.out.println(" Tasks for SKU [" + skuId.toUpperCase() + "]:");
        Ui.printDivider();
        taskList.printSKUTaskList();
        Ui.printDivider();
    }

    /**
     * Lists all tasks across all SKUs filtered by the given priority level.
     *
     * @param priorityStr Priority string (HIGH / MEDIUM / LOW).
     */
    private void listTasksByPriority(String priorityStr) {
        Priority filter;
        try {
            filter = Priority.valueOf(priorityStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            Ui.printError("Invalid priority '" + priorityStr + "'. Use HIGH, MEDIUM, or LOW.");
            return;
        }

        System.out.println(" Tasks with priority [" + filter + "]:");
        Ui.printDivider();

        int count = 0;
        for (Map.Entry<String, SKUTaskList> entry : taskMap.entrySet()) {
            for (SKUTask task : entry.getValue().getSKUTaskList()) {
                if (task.getSKUTaskPriority() == filter) {
                    System.out.println("  [SKU: " + entry.getKey() + "] " + task);
                    count++;
                }
            }
        }

        if (count == 0) {
            Ui.printInfo("No tasks found with priority: " + filter);
        }
        Ui.printDivider();
    }

    /**
     * Lists all tasks sorted by Manhattan distance from the given warehouse sector.
     *
     * @param fromStr Starting location (e.g. {@code "B2"}).
     */
    private void listTasksByDistance(String fromStr) {
        Location from;
        try {
            from = Location.valueOf(fromStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            Ui.printError("Invalid location '" + fromStr + "'. Must be one of: A1 A2 A3 B1 B2 B3 C1 C2 C3");
            return;
        }

        // Build a list of [skuId, task, distanceToSku] tuples
        List<TaskEntry> entries = new ArrayList<>();

        for (Map.Entry<String, SKUTaskList> entry : taskMap.entrySet()) {
            SKU sku = findSku(entry.getKey());
            if (sku == null) {
                continue;
            }
            int dist = manhattanDistance(from, sku.getSKULocation());
            for (SKUTask task : entry.getValue().getSKUTaskList()) {
                entries.add(new TaskEntry(entry.getKey(), task, dist));
            }
        }

        entries.sort(Comparator.comparingInt(e -> e.distance));

        System.out.println(" Tasks sorted by distance from [" + from + "]:");
        Ui.printDivider();

        if (entries.isEmpty()) {
            Ui.printInfo("No tasks found.");
        } else {
            for (TaskEntry e : entries) {
                System.out.printf("  [SKU: %-25s | dist=%-2d] %s%n",
                        e.skuId, e.distance, e.task);
            }
        }
        Ui.printDivider();
    }

    /**
     * Lists every task in the system, grouped by SKU.
     */
    private void listAllTasks() {
        System.out.println(" All tasks:");
        Ui.printDivider();

        boolean anyTasks = false;
        for (Map.Entry<String, SKUTaskList> entry : taskMap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                System.out.println(" SKU [" + entry.getKey() + "]:");
                entry.getValue().printSKUTaskList();
                anyTasks = true;
            }
        }

        if (!anyTasks) {
            Ui.printInfo("No tasks in the system yet.");
        }
        Ui.printDivider();
    }

    // =========================================================
    // Private helpers
    // =========================================================

    /**
     * Looks up a SKU by ID from the SKUList.
     * Case-insensitive comparison.
     *
     * <p><b>Note for Om's team:</b> This method requires {@code SKUList.getSKUList()} to return
     * the underlying {@code ArrayList<SKU>}. Please add:
     * <pre>
     *   public ArrayList{@literal <SKU>} getSKUList() { return skuList; }
     * </pre>
     * to {@code SKUList.java}.
     *
     * @param skuId The SKU_ID to search for.
     * @return The matching {@link SKU}, or {@code null} if not found.
     */
    private SKU findSku(String skuId) {
        for (SKU sku : skuList.getSKUList()) {
            if (sku.getSKUID().equalsIgnoreCase(skuId)) {
                return sku;
            }
        }
        return null;
    }

    /**
     * Returns the {@link SKUTaskList} for a given SKU_ID, creating one if absent.
     *
     * @param skuId The SKU_ID key.
     * @return Existing or newly created {@link SKUTaskList}.
     */
    private SKUTaskList getOrCreateTaskList(String skuId) {
        return taskMap.computeIfAbsent(skuId.toUpperCase(), k -> new SKUTaskList());
    }

    /**
     * Parses a 1-based task index from a string, printing an error on failure.
     *
     * @param indexStr String representation of the index.
     * @return The parsed integer, or {@code -1} if parsing failed.
     */
    private int parseIndex(String indexStr) {
        try {
            return Integer.parseInt(indexStr.trim());
        } catch (NumberFormatException e) {
            Ui.printError("Task index must be a number, got: '" + indexStr + "'");
            return -1;
        }
    }

    /**
     * Computes the Manhattan distance between two {@link Location} grid sectors.
     * Row = letter (A=0, B=1, C=2), column = digit (1=0, 2=1, 3=2).
     *
     * @param a First location.
     * @param b Second location.
     * @return Manhattan distance as an integer.
     */
    private int manhattanDistance(Location a, Location b) {
        int rowA = a.name().charAt(0) - 'A';
        int colA = Character.getNumericValue(a.name().charAt(1)) - 1;
        int rowB = b.name().charAt(0) - 'A';
        int colB = Character.getNumericValue(b.name().charAt(1)) - 1;
        return Math.abs(rowA - rowB) + Math.abs(colA - colB);
    }

    // =========================================================
    // Inner helper record
    // =========================================================

    /** Bundles a task with its parent SKU ID and precomputed distance for sorting. */
    private static class TaskEntry {
        final String skuId;
        final SKUTask task;
        final int distance;

        TaskEntry(String skuId, SKUTask task, int distance) {
            this.skuId = skuId;
            this.task = task;
            this.distance = distance;
        }
    }
}