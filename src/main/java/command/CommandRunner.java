package command;

import exception.EmptyListException;
import exception.InvalidCommandException;
import exception.InvalidIndexException;
import exception.ItemTaskerException;
import exception.MissingArgumentException;
import exception.SKUNotFoundException;

import sku.Location;
import sku.SKU;
import sku.SKUList;
import skutask.Priority;
import skutask.SKUTask;
import skutask.SKUTaskList;
import skutask.ViewSKUTask;
import storage.Storage;
import ui.Ui;

import java.io.IOException;
import java.util.List;

/**
 * Receives parsed commands from the user input and routes them to specific
 * handler methods to perform the requested operations.
 * It manages the running state of the application loop and routes task
 * assignments strictly through the core SKUList data structure.
 */
public class CommandRunner {

    /**
     * Set false to stop the main loop.
     */
    private boolean isRunning;

    private final SKUList skuList;

    /**
     * Constructs a CommandRunner backed by the given SKU data store.
     *
     * @param skuList The shared SKU data store for the application.
     */
    public CommandRunner(SKUList skuList) {
        this.skuList = skuList;
        this.isRunning = true;
        Storage.loadState(skuList);
    }

    /**
     * Returns the current running state of the application.
     *
     * @return True if the application should continue running, false otherwise.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Dispatches a parsed command to the appropriate specific handler based on its
     * command word.
     *
     * @param cmd The parsed command object containing the command word and extracted arguments.
     * @throws ItemTaskerException If a domain-specific error occurs duringexecution.
     * @throws IOException         If an error occurs during state saving upon exit.
     */
    public void run(ParsedCommand cmd) throws ItemTaskerException, IOException {
        assert cmd != null : "ParsedCommand should not be null";

        switch (cmd.getCommandWord()) {
        case "addsku":
            handleAddSku(cmd);
            break;
        case "deletesku":
            handleDeleteSku(cmd);
            break;
        case "editsku":
            handleEditSku(cmd);
            break;
        case "addskutask":
            handleAddSkuTask(cmd);
            break;
        case "edittask":
            handleEditTask(cmd);
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
        case "find":
            handleFind(cmd);
            break;
        case "export":
            handleExport();
            break;
        case "help":
            Ui.printHelp();
            break;
        case "bye":
        case "exit":
            try {
                saveState();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Ui.printGoodbye();
            isRunning = false;
            break;
        case "":
            break;
        default:
            Ui.printUnknownCommand(cmd.getCommandWord());
        }
    }

    /**
     * Parses arguments and adds a new SKU to the warehouse.
     *
     * @param cmd The parsed command containing the SKU ID and location.
     * @throws SKUNotFoundException If an issue related to finding the SKU occurs during validation.
     */
    private void handleAddSku(ParsedCommand cmd) throws SKUNotFoundException {
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
        Ui.printSuccess("Added SKU [" + skuId.toUpperCase() + "] at location " + location);
    }

    /**
     * Parses arguments and updates the warehouse location of an existing SKU.
     *
     * @param cmd The parsed command containing the SKU ID and new location.
     * @throws SKUNotFoundException If the specified SKU does not exist in the warehouse.
     */
    private void handleEditSku(ParsedCommand cmd) throws SKUNotFoundException {
        String skuId = cmd.getArg("n");
        String locationStr = cmd.getArg("l");

        if (skuId == null || locationStr == null) {
            Ui.printError("Usage: editsku n/SKU_ID l/NEW_LOCATION");
            return;
        }

        SKU targetSku = findSku(skuId);
        if (targetSku == null) {
            Ui.printError("SKU not found: " + skuId);
            return;
        }

        Location newLocation;
        try {
            newLocation = Location.valueOf(locationStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            Ui.printError("Invalid location '" + locationStr + "'. Must be one of: A1 A2 A3 B1 B2 B3 C1 C2 C3");
            return;
        }

        targetSku.setLocation(newLocation);
        Ui.printSuccess("Updated location of SKU [" + skuId.toUpperCase() + "] to " + newLocation + ".");
    }

    /**
     * Parses arguments and deletes an existing SKU from the warehouse.
     *
     * @param cmd The parsed command containing the SKU ID to delete.
     * @throws MissingArgumentException If the SKU ID is not provided.
     * @throws SKUNotFoundException     If the specified SKU does not exist in the warehouse.
     */
    private void handleDeleteSku(ParsedCommand cmd) throws MissingArgumentException, SKUNotFoundException {
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
        Ui.printSuccess("Deleted SKU [" + skuId.toUpperCase() + "] and all its tasks.");
    }

    /**
     * Parses arguments and adds a new task to a specific SKU.
     *
     * @param cmd The parsed command containing the SKU ID, due date, and optional priority.
     * @throws MissingArgumentException If required arguments (SKU ID or due date) are missing.
     * @throws SKUNotFoundException     If the specified SKU does not exist in the warehouse.
     */
    private void handleAddSkuTask(ParsedCommand cmd) throws MissingArgumentException, SKUNotFoundException {
        String skuId = cmd.getArg("n");
        String dueDate = cmd.getArg("d");

        if (skuId == null || dueDate == null) {
            Ui.printError("Usage: addskutask n/SKU_ID d/DUE_DATE [p/PRIORITY] [t/DESCRIPTION]");
            return;
        }

        SKU targetSku = findSku(skuId);
        if (targetSku == null) {
            Ui.printError("SKU not found: " + skuId + ". Use 'addsku' to register it first.");
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

        String description = cmd.hasArg("t") ? cmd.getArg("t") : "";

        SKUTaskList taskList = targetSku.getSKUTaskList();
        taskList.addSKUTask(skuId.toUpperCase(), priority, dueDate, description);
        int newIndex = taskList.getSize();

        Ui.printSuccess("Added task #" + newIndex +
                " to SKU [" + skuId.toUpperCase() + "] | Priority: "
                + priority + " | Due: " + dueDate
                + (description.isEmpty() ? "" : " | Desc: " + description));
    }

    /**
     * Parses arguments and edits the fields of an existing task.
     * At least one of d/, p/, or t/ must be provided.
     *
     * @param cmd The parsed command containing the SKU ID, task index, and fields to update.
     * @throws InvalidIndexException If the provided index is out of bounds or not a number.
     * @throws SKUNotFoundException  If the specified SKU does not exist in the warehouse.
     */
    private void handleEditTask(ParsedCommand cmd) throws InvalidIndexException, SKUNotFoundException {
        String skuId = cmd.getArg("n");
        String indexStr = cmd.getArg("i");

        if (skuId == null || indexStr == null) {
            Ui.printError("Usage: edittask n/SKU_ID i/TASK_INDEX [d/DATE] [p/PRIORITY] [t/DESC]");
            return;
        }

        String newDate = cmd.getArg("d");
        String newPriorityStr = cmd.getArg("p");
        String newDesc = cmd.getArg("t");

        if (newDate == null && newPriorityStr == null && newDesc == null) {
            Ui.printError("Provide at least one field to update: d/DATE, p/PRIORITY, or t/DESC.");
            return;
        }

        int index = parseIndex(indexStr);
        if (index == -1) {
            return;
        }

        SKU targetSku = findSku(skuId);
        if (targetSku == null) {
            Ui.printError("SKU not found: " + skuId);
            return;
        }

        SKUTaskList taskList = targetSku.getSKUTaskList();
        if (index < 1 || index > taskList.getSize()) {
            throw new InvalidIndexException(index, skuId);
        }

        skutask.Priority newPriority = null;
        if (newPriorityStr != null) {
            try {
                newPriority = skutask.Priority.valueOf(newPriorityStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                Ui.printError("Invalid priority '" + newPriorityStr + "'. Use HIGH, MEDIUM, or LOW.");
                return;
            }
        }

        taskList.editSKUTask(index, newDate, newPriority, newDesc);
        SKUTask updated = taskList.getSKUTaskList().get(index - 1);
        Ui.printSuccess("Updated task #" + index + " for SKU [" + skuId.toUpperCase() + "]: " + updated);
    }
    /**
     * Parses arguments and deletes a specific task from an SKU based on its index.
     *
     * @param cmd The parsed command containing the SKU ID and the task index.
     * @throws InvalidIndexException If the provided index is out of bounds or not a number.
     * @throws SKUNotFoundException  If the specified SKU does not exist in the warehouse.
     */
    private void handleDeleteTask(ParsedCommand cmd) throws InvalidIndexException, SKUNotFoundException {
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

        SKU targetSku = findSku(skuId);
        if (targetSku == null) {
            Ui.printError("SKU not found: " + skuId);
            return;
        }

        SKUTaskList taskList = targetSku.getSKUTaskList();
        if (index < 1 || index > taskList.getSize()) {
            Ui.printError("Task index " + index + " is out of range for SKU: " + skuId);
            return;
        }

        taskList.deleteSKUTaskByIndex(index);
        Ui.printSuccess("Deleted task #" + index + " from SKU [" + skuId.toUpperCase() + "].");
    }

    /**
     * Parses arguments and marks a specific task as done.
     *
     * @param cmd The parsed command containing the SKU ID and the task index.
     * @throws MissingArgumentException If required arguments are missing.
     * @throws InvalidIndexException    If the provided index is out of bounds or not a number.
     */
    private void handleMarkTask(ParsedCommand cmd) throws MissingArgumentException, InvalidIndexException {
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

        SKU targetSku = findSku(skuId);
        if (targetSku == null) {
            Ui.printError("SKU not found: " + skuId);
            return;
        }

        SKUTaskList taskList = targetSku.getSKUTaskList();
        if (index < 1 || index > taskList.getSize()) {
            throw new InvalidIndexException(index, skuId);
        }

        SKUTask task = taskList.getSKUTaskList().get(index - 1);
        if (task.isDone()) {
            Ui.printInfo("Task #" + index + " for SKU [" + skuId.toUpperCase() + "] is already marked as done.");
            return;
        }

        taskList.markTask(index);
        Ui.printSuccess("Marked task #" + index + " as done for SKU [" + skuId.toUpperCase() + "].");
    }

    /**
     * Parses arguments and unmarks a completed task.
     *
     * @param cmd The parsed command containing the SKU ID and the task index.
     * @throws MissingArgumentException If required arguments are missing.
     * @throws InvalidIndexException    If the provided index is out of bounds or not a number.
     */
    private void handleUnmarkTask(ParsedCommand cmd) throws MissingArgumentException, InvalidIndexException {
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

        SKU targetSku = findSku(skuId);
        if (targetSku == null) {
            Ui.printError("SKU not found: " + skuId);
            return;
        }

        SKUTaskList taskList = targetSku.getSKUTaskList();
        if (index < 1 || index > taskList.getSize()) {
            throw new InvalidIndexException(index, skuId);
        }

        SKUTask task = taskList.getSKUTaskList().get(index - 1);
        if (!task.isDone()) {
            Ui.printInfo("Task #" + index + " for SKU [" + skuId.toUpperCase() + "] is already unmarked.");
            return;
        }

        taskList.unmarkTask(index);
        Ui.printSuccess("Unmarked task #" + index + " for SKU [" + skuId.toUpperCase() + "].");
    }

    /**
     * Parses arguments and displays a filtered or sorted list of tasks.
     *
     * @param cmd The parsed command containing optional filter arguments (SKU, priority, or location).
     * @throws InvalidCommandException If the command format is fundamentally invalid.
     * @throws EmptyListException      If the system is queried but currently tracks no tasks.
     * @throws SKUNotFoundException    If a specific SKU filter is applied but the SKU does not exist.
     */
    private void handleListTasks(ParsedCommand cmd) throws InvalidCommandException, EmptyListException,
            SKUNotFoundException {
        ViewSKUTask viewer = new ViewSKUTask();
        String n = cmd.getArg("n");
        String p = cmd.getArg("p");
        String l = cmd.getArg("l");
        viewer.setSkuFilter(n);
        viewer.setPriorityFilter(p);
        viewer.setLocationFilter(l);

        // NOTE: ViewSKUTask signature must be updated to accept only the skuList
        List<SKUTask> results = viewer.listTasks(this.skuList);

        if (n != null) {
            SKU targetSku = findSku(n);
            if (targetSku == null || targetSku.getSKUTaskList().isEmpty()) {
                Ui.printInfo("No tasks found for SKU: " + n.toUpperCase());
                return;
            }
            System.out.println(" Tasks for SKU [" + n.toUpperCase() + "]:");
            Ui.printDivider();
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }
            Ui.printDivider();

        } else if (p != null) {
            try {
                Priority.valueOf(p.toUpperCase());
            } catch (IllegalArgumentException e) {
                Ui.printError("Invalid priority '" + p + "'. Use HIGH, MEDIUM, or LOW.");
                return;
            }
            System.out.println(" Tasks with priority [" + p.toUpperCase() + "]:");
            Ui.printDivider();
            if (results.isEmpty()) {
                Ui.printInfo("No tasks found with priority: " + p.toUpperCase());
            } else {
                results.forEach(t -> System.out.println("  [SKU: " + t.getSKUTaskID() + "] " + t));
            }
            Ui.printDivider();

        } else if (l != null) {
            sku.Location from;
            try {
                from = sku.Location.valueOf(l.toUpperCase());
            } catch (IllegalArgumentException e) {
                Ui.printError("Invalid location '" + l + "'. Must be one of: A1 A2 A3 B1 B2 B3 C1 C2 C3");
                return;
            }
            System.out.println(" Tasks sorted by distance from [" + from + "]:");
            Ui.printDivider();
            if (results.isEmpty()) {
                Ui.printInfo("No tasks found.");
            } else {
                for (SKUTask t : results) {
                    SKU skuObj = findSku(t.getSKUTaskID());
                    if (skuObj == null) {
                        continue;
                    }
                    int dist = viewer.calculateDistance(t, l, this.skuList);
                    System.out.printf("  [SKU: %-25s | dist=%-2d] %s%n", t.getSKUTaskID(), dist, t);
                }
            }
            Ui.printDivider();

        } else {
            System.out.println(" All tasks:");
            Ui.printDivider();
            if (skuList.getSKUList().isEmpty()) {
                Ui.printInfo("No SKUs registered yet.");
            }

            boolean anyTasks = false;
            for (SKU sku : skuList.getSKUList()) {
                System.out.println(" SKU [" + sku.getSKUID().toUpperCase() + "]:");
                if (sku.getSKUTaskList().isEmpty()) {
                    System.out.println("   No tasks for this SKU.");
                } else {
                    sku.getSKUTaskList().printSKUTaskList();
                    anyTasks = true;
                }
            }
            if (!skuList.getSKUList().isEmpty() && !anyTasks) {
                Ui.printInfo("No tasks in the system yet.");
            }
            Ui.printDivider();
        }
    }

    /**
     * Serializes the current application state to JSON storage.
     *
     * @throws IOException If an error occurs during file writing.
     */
    private void saveState() throws IOException {
        Storage.saveState(this.skuList);
    }

    /**
     * Searches for a specific SKU object in the warehouse by its ID.
     *
     * @param skuId The alphanumeric string identifying the SKU.
     * @return The matching SKU object, or null if no match is found.
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
     * Handles the 'find' command with combinable filters.
     * Supports optional flags: n/SKU_ID, t/DESCRIPTION, i/TASK_INDEX.
     * All flags can be combined to narrow down results.
     *
     * @param cmd The parsed command containing the filter flags.
     * @throws MissingArgumentException If no filter flags are provided.
     * @throws SKUNotFoundException     If the specified SKU does not exist in the warehouse.
     * @throws InvalidIndexException    If the task index is not a valid number or is out of range.
     */
    private void handleFind(ParsedCommand cmd) throws MissingArgumentException, SKUNotFoundException,
            InvalidIndexException {
        String skuFilter = cmd.getArg("n");
        String descFilter = cmd.getArg("t");
        String indexStr = cmd.getArg("i");

        if (skuFilter == null && descFilter == null && indexStr == null) {
            throw new MissingArgumentException("Usage: find [n/SKU_ID] [t/DESCRIPTION] [i/TASK_INDEX]");
        }

        if (skuFilter != null && findSku(skuFilter) == null) {
            throw new SKUNotFoundException(skuFilter);
        }

        int taskIndex = -1;
        if (indexStr != null) {
            taskIndex = parseIndex(indexStr);
            if (taskIndex == -1) {
                return;
            }
        }

        boolean found = false;
        System.out.println(" Search results:");
        Ui.printDivider();

        for (SKU sku : skuList.getSKUList()) {
            // If n/ filter is given, skip non-matching SKUs
            if (skuFilter != null && !sku.getSKUID().equalsIgnoreCase(skuFilter)) {
                continue;
            }

            SKUTaskList taskList = sku.getSKUTaskList();
            java.util.ArrayList<SKUTask> tasks = taskList.getSKUTaskList();

            // If i/ filter is given, only look at that specific index
            if (taskIndex > 0) {
                if (taskIndex > tasks.size()) {
                    if (skuFilter != null) {
                        throw new InvalidIndexException(taskIndex, sku.getSKUID());
                    }
                    continue;
                }
                SKUTask task = tasks.get(taskIndex - 1);
                if (matchesDescription(task, descFilter)) {
                    System.out.println("  [SKU: " + sku.getSKUID().toUpperCase() + "] #" + taskIndex + ". " + task);
                    found = true;
                }
            } else {
                // Search all tasks in this SKU
                for (int i = 0; i < tasks.size(); i++) {
                    SKUTask task = tasks.get(i);
                    if (matchesDescription(task, descFilter)) {
                        System.out.println("  [SKU: " + sku.getSKUID().toUpperCase() + "] #" + (i + 1) + ". " + task);
                        found = true;
                    }
                }
            }
        }

        if (!found) {
            Ui.printInfo("No matching tasks found.");
        }
        Ui.printDivider();
    }

    /**
     * Checks if a task's description contains the given keyword (case-insensitive).
     * Returns true if no description filter is specified.
     *
     * @param task       The task to check.
     * @param descFilter The keyword to search for, or null to match all.
     * @return True if the task matches the filter.
     */
    private boolean matchesDescription(SKUTask task, String descFilter) {
        if (descFilter == null) {
            return true;
        }
        String desc = task.getSKUTaskDescription();
        if (desc == null) {
            return false;
        }
        return desc.toLowerCase().contains(descFilter.toLowerCase());
    }

    /**
     * Parses a string input into a numeric integer index.
     *
     * @param indexStr The raw string input provided by the user.
     * @return The parsed integer index, or -1 if the input is not a valid integer.
     * @throws InvalidIndexException If the string fails to parse (handled internally to print an error).
     */
    private int parseIndex(String indexStr) throws InvalidIndexException {
        try {
            return Integer.parseInt(indexStr.trim());
        } catch (NumberFormatException e) {
            Ui.printError("Task index must be a number, got: '" + indexStr + "'");
            return -1;
        }
    }

    /**
     * Initiates the export of the system's inventory to a readable text file.
     */
    private void handleExport() {
        try {
            storage.Export.exportToTextFile(this.skuList);
            Ui.printSuccess("Warehouse state successfully exported to Data/ItemTasker_Export.txt");
        } catch (IOException e) {
            Ui.printError("Failed to export data: " + e.getMessage());
        }
    }
}
