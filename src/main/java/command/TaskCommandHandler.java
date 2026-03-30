package command;

import exception.InvalidIndexException;
import exception.MissingArgumentException;
import exception.SKUNotFoundException;

import sku.SKU;
import sku.SKUList;
import skutask.Priority;
import skutask.SKUTask;
import skutask.SKUTaskList;
import skutask.TaskSorter;
import ui.Ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles all task-level commands: adding, editing, deleting,
 * marking, and unmarking tasks within SKUs.
 */
//@@author omcodedthis
public class TaskCommandHandler {
    private static final Logger LOGGER = Logger.getLogger(TaskCommandHandler.class.getName());
    private final SKUList skuList;

    /**
     * Constructs a TaskCommandHandler backed by the provided SKUList.
     *
     * @param skuList The shared SKU data store for the application.
     * @throws IllegalArgumentException If the provided SKUList is null.
     */
    public TaskCommandHandler(SKUList skuList) {
        if (skuList == null) {
            throw new IllegalArgumentException("TaskCommandHandler requires a non-null SKUList");
        }
        this.skuList = skuList;
    }

    /**
     * Handles the addition of a new task to a specific SKU.
     *
     * @param cmd The parsed command containing the SKU ID, due date, and optionally the priority and description.
     * @throws MissingArgumentException If the required SKU ID or due date arguments are missing.
     * @throws SKUNotFoundException     If the target SKU cannot be found in the warehouse.
     */
    public void handleAddSkuTask(ParsedCommand cmd) throws MissingArgumentException, SKUNotFoundException {
        assert cmd != null : "Internal Error: ParsedCommand cannot be null";

        String skuId = cmd.getArg("n");
        String dueDate = cmd.getArg("d");

        if (skuId == null || dueDate == null) {
            throw new MissingArgumentException("Usage: addskutask n/SKU_ID d/DUE_DATE [p/PRIORITY] [t/DESCRIPTION]");
        }

        String validatedDate = DateValidator.validateDateOrError(dueDate);
        if (validatedDate == null) {
            return;
        }

        SKU targetSku = skuList.findByID(skuId);
        if (targetSku == null) {
            LOGGER.log(Level.WARNING, "Failed to add task: SKU [" + skuId + "] not found.");
            throw new SKUNotFoundException(skuId);
        }

        Priority priority = CommandHelper.parsePriorityOrDefault(cmd);
        if (priority == null) {
            return;
        }

        String description = cmd.hasArg("t") ? cmd.getArg("t") : "";
        SKUTaskList taskList = targetSku.getSKUTaskList();

        try {
            taskList.addSKUTask(skuId.toUpperCase(), priority, validatedDate, description);
            int newIndex = taskList.getSize();

            LOGGER.log(Level.INFO, "Added task #" + newIndex + " to SKU [" + skuId + "]");
            Ui.printSuccess("Added task #" + newIndex + " to SKU [" + skuId.toUpperCase() + "] | Priority: "
                    + priority + " | Due: " + validatedDate
                    + (description.isEmpty() ? "" : " | Desc: " + description));
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Domain validation rejected task addition", e);
            Ui.printError("Failed to add task due to invalid data: " + e.getMessage());
        }
    }

    //@@author AkshayPranav19
    /**
     * Handles editing an existing task's properties (due date, priority, or description).
     *
     * @param cmd The parsed command containing the SKU ID, task index, and at least one property to update.
     * @throws InvalidIndexException If the provided task index is out of bounds or invalid.
     * @throws SKUNotFoundException  If the target SKU cannot be found in the warehouse.
     */
    public void handleEditTask(ParsedCommand cmd) throws InvalidIndexException, SKUNotFoundException {
        assert cmd != null : "Internal Error: ParsedCommand cannot be null";

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

        if (newDate != null) {
            newDate = DateValidator.validateDateOrError(newDate);
            if (newDate == null) {
                return;
            }
        }

        int index = CommandHelper.parseIndex(indexStr);
        if (index == -1) {
            return;
        }

        SKU targetSku = CommandHelper.findSkuOrError(skuList, skuId);
        if (targetSku == null) {
            return;
        }

        SKUTaskList taskList = targetSku.getSKUTaskList();
        if (index < 1 || index > taskList.getSize()) {
            LOGGER.log(Level.WARNING, "Failed to edit task: Index " + index + " out of bounds for SKU ["
                    + skuId + "]");
            throw new InvalidIndexException(index, skuId);
        }

        Priority newPriority = CommandHelper.parsePriority(newPriorityStr);
        if (newPriorityStr != null && newPriority == null) {
            return;
        }

        try {
            taskList.editSKUTask(index, newDate, newPriority, newDesc);
            SKUTask updated = taskList.getSKUTaskList().get(index - 1);

            LOGGER.log(Level.INFO, "Edited task #" + index + " for SKU [" + skuId + "]");
            Ui.printSuccess("Updated task #" + index + " for SKU [" + skuId.toUpperCase() + "]: " + updated);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.log(Level.SEVERE, "Index out of bounds during edit, bypassing guard clause", e);
            throw new InvalidIndexException(index, skuId);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Domain validation rejected task edit", e);
            Ui.printError("Failed to edit task due to invalid data: " + e.getMessage());
        }
    }

    //@@author omcodedthis
    /**
     * Handles the deletion of a specific task from a SKU.
     *
     * @param cmd The parsed command containing the SKU ID and the index of the task to delete.
     * @throws InvalidIndexException    If the provided task index is out of bounds or invalid.
     * @throws SKUNotFoundException     If the target SKU cannot be found in the warehouse.
     * @throws MissingArgumentException If the required SKU ID or task index arguments are missing.
     */
    public void handleDeleteTask(ParsedCommand cmd) throws InvalidIndexException, SKUNotFoundException,
            MissingArgumentException {
        assert cmd != null : "Internal Error: ParsedCommand cannot be null";

        String skuId = cmd.getArg("n");
        String indexStr = cmd.getArg("i");

        if (skuId == null || indexStr == null) {
            throw new MissingArgumentException("Usage: deletetask n/SKU_ID i/TASK_INDEX");
        }

        int index = CommandHelper.parseIndex(indexStr);
        if (index == -1) {
            return;
        }

        SKU targetSku = skuList.findByID(skuId);
        if (targetSku == null) {
            throw new SKUNotFoundException(skuId);
        }

        SKUTaskList taskList = targetSku.getSKUTaskList();
        if (index < 1 || index > taskList.getSize()) {
            LOGGER.log(Level.WARNING, "Failed to delete task: Index " + index + " out of bounds for SKU ["
                    + skuId + "]");
            throw new InvalidIndexException(index, skuId);
        }

        taskList.deleteSKUTaskByIndex(index);
        LOGGER.log(Level.INFO, "Deleted task #" + index + " from SKU [" + skuId + "]");
        Ui.printSuccess("Deleted task #" + index + " from SKU [" + skuId.toUpperCase() + "].");
    }

    //@@author AkshayPranav19
    /**
     * Handles marking a specific task as completed.
     *
     * @param cmd The parsed command containing the SKU ID and the index of the task to mark.
     * @throws MissingArgumentException If the required SKU ID or task index arguments are missing.
     * @throws InvalidIndexException    If the provided task index is out of bounds or invalid.
     */
    public void handleMarkTask(ParsedCommand cmd) throws MissingArgumentException, InvalidIndexException {
        assert cmd != null : "Internal Error: ParsedCommand cannot be null";

        String skuId = cmd.getArg("n");
        String indexStr = cmd.getArg("i");

        if (skuId == null || indexStr == null) {
            Ui.printError("Usage: marktask n/SKU_ID i/TASK_INDEX");
            return;
        }

        int index = CommandHelper.parseIndex(indexStr);
        if (index == -1) {
            return;
        }

        SKU targetSku = CommandHelper.findSkuOrError(skuList, skuId);
        if (targetSku == null) {
            return;
        }

        SKUTaskList taskList = targetSku.getSKUTaskList();
        if (index < 1 || index > taskList.getSize()) {
            LOGGER.log(Level.WARNING, "Failed to mark task: Index " + index + " out of bounds for SKU ["
                    + skuId + "]");
            throw new InvalidIndexException(index, skuId);
        }

        try {
            SKUTask task = taskList.getSKUTaskList().get(index - 1);
            if (task.isDone()) {
                Ui.printInfo("Task #" + index + " for SKU [" + skuId.toUpperCase() + "] is already marked as done.");
                return;
            }

            taskList.markTask(index);
            LOGGER.log(Level.INFO, "Marked task #" + index + " as done for SKU [" + skuId + "]");
            Ui.printSuccess("Marked task #" + index + " as done for SKU [" + skuId.toUpperCase() + "].");
        } catch (IndexOutOfBoundsException e) {
            LOGGER.log(Level.SEVERE, "Index out of bounds during mark", e);
            throw new InvalidIndexException(index, skuId);
        }
    }

    /**
     * Handles unmarking a completed task, changing its status back to incomplete.
     *
     * @param cmd The parsed command containing the SKU ID and the index of the task to unmark.
     * @throws MissingArgumentException If the required SKU ID or task index arguments are missing.
     * @throws InvalidIndexException    If the provided task index is out of bounds or invalid.
     */
    public void handleUnmarkTask(ParsedCommand cmd) throws MissingArgumentException, InvalidIndexException {
        assert cmd != null : "Internal Error: ParsedCommand cannot be null";

        String skuId = cmd.getArg("n");
        String indexStr = cmd.getArg("i");

        if (skuId == null || indexStr == null) {
            Ui.printError("Usage: unmarktask n/SKU_ID i/TASK_INDEX");
            return;
        }

        int index = CommandHelper.parseIndex(indexStr);
        if (index == -1) {
            return;
        }

        SKU targetSku = CommandHelper.findSkuOrError(skuList, skuId);
        if (targetSku == null) {
            return;
        }

        SKUTaskList taskList = targetSku.getSKUTaskList();
        if (index < 1 || index > taskList.getSize()) {
            LOGGER.log(Level.WARNING, "Failed to unmark task: Index " + index + " out of bounds for SKU ["
                    + skuId + "]");
            throw new InvalidIndexException(index, skuId);
        }

        try {
            SKUTask task = taskList.getSKUTaskList().get(index - 1);
            if (!task.isDone()) {
                Ui.printInfo("Task #" + index + " for SKU [" + skuId.toUpperCase() + "] is already unmarked.");
                return;
            }

            taskList.unmarkTask(index);
            LOGGER.log(Level.INFO, "Unmarked task #" + index + " for SKU [" + skuId + "]");
            Ui.printSuccess("Unmarked task #" + index + " for SKU [" + skuId.toUpperCase() + "].");
        } catch (IndexOutOfBoundsException e) {
            LOGGER.log(Level.SEVERE, "Index out of bounds during unmark", e);
            throw new InvalidIndexException(index, skuId);
        }
    }


    /**
     * Sorts tasks for a specific SKU by the given field in the specified order.
     * Supported sort fields: date, priority, status.
     * Default sort order is ascending if not specified.
     *
     * @param cmd The parsed command containing SKU ID, sort field, and optional order.
     * @throws SKUNotFoundException If the specified SKU does not exist.
     */
    //@@author AkshayPranav19
    public void handleSortTask(ParsedCommand cmd) throws SKUNotFoundException {
        assert cmd != null : "Internal Error: ParsedCommand cannot be null";

        String skuId = cmd.getArg("n");
        String sortField = cmd.getArg("s");

        if (skuId == null || sortField == null) {
            LOGGER.log(Level.WARNING, "Sort command missing required arguments.");
            Ui.printError("Usage: sorttasks n/SKU_ID s/date|priority|status [o/asc|desc]");
            return;
        }

        LOGGER.log(Level.INFO, "Sorting tasks for SKU [" + skuId + "] by field: " + sortField);

        SKU targetSku = CommandHelper.findSkuOrError(skuList, skuId);
        if (targetSku == null) {
            LOGGER.log(Level.WARNING, "Sort failed: SKU [" + skuId + "] not found.");
            return;
        }

        if (!TaskSorter.isValidSortField(sortField)) {
            LOGGER.log(Level.WARNING, "Invalid sort field provided: " + sortField);
            Ui.printError("Invalid sort field '" + sortField + "'. Use: date, priority, or status.");
            return;
        }

        String orderStr = cmd.hasArg("o") ? cmd.getArg("o") : "ascending";
        boolean ascending = !orderStr.toLowerCase().startsWith("desc");

        SKUTaskList taskList = targetSku.getSKUTaskList();
        assert taskList != null : "Internal Error: SKUTaskList should never be null";

        try {
            List<SKUTask> tasks = new ArrayList<>(taskList.getSKUTaskList());
            TaskSorter sorter = new TaskSorter(tasks, sortField.toLowerCase(), ascending);
            List<SKUTask> sorted = sorter.getSortedTasks();

            assert sorted.size() == tasks.size()
                    : "Sorted result size must match original task count";

            LOGGER.log(Level.INFO, "Successfully sorted " + sorted.size() + " tasks for SKU ["
                    + skuId + "] by " + sortField + " (" + orderStr + ")");
            Ui.printSortedTasks(skuId, sortField, orderStr, sorted);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Sort failed due to invalid data in SKU [" + skuId + "]", e);
            Ui.printError("Failed to sort tasks: " + e.getMessage());
        }
    }
}
