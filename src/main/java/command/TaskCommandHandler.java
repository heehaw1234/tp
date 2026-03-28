package command;

import exception.InvalidIndexException;
import exception.MissingArgumentException;
import exception.SKUNotFoundException;

import sku.SKU;
import sku.SKUList;
import skutask.Priority;
import skutask.SKUTask;
import skutask.SKUTaskList;
import ui.Ui;

/**
 * Handles all task-level commands: adding, editing, deleting,
 * marking, and unmarking tasks within SKUs.
 * Each public method corresponds to a single user command.
 */
public class TaskCommandHandler {

    private final SKUList skuList;

    public TaskCommandHandler(SKUList skuList) {
        this.skuList = skuList;
    }

    /**
     * Adds a new task to a specific SKU after validating all arguments.
     *
     * @param cmd The parsed command containing the SKU ID, due date, and optional priority.
     * @throws MissingArgumentException If required arguments (SKU ID or due date) are missing.
     * @throws SKUNotFoundException     If the specified SKU does not exist in the warehouse.
     */
    public void handleAddSkuTask(ParsedCommand cmd) throws MissingArgumentException, SKUNotFoundException {
        String skuId = cmd.getArg("n");
        String dueDate = cmd.getArg("d");

        if (skuId == null || dueDate == null) {
            Ui.printError("Usage: addskutask n/SKU_ID d/DUE_DATE [p/PRIORITY] [t/DESCRIPTION]");
            return;
        }

        SKU targetSku = skuList.findByID(skuId);
        if (targetSku == null) {
            Ui.printError("SKU not found: " + skuId + ". Use 'addsku' to register it first.");
            return;
        }

        Priority priority = CommandHelper.parsePriorityOrDefault(cmd);
        if (priority == null) {
            return;
        }

        String description = cmd.hasArg("t") ? cmd.getArg("t") : "";

        SKUTaskList taskList = targetSku.getSKUTaskList();
        taskList.addSKUTask(skuId.toUpperCase(), priority, dueDate, description);
        int newIndex = taskList.getSize();

        Ui.printSuccess("Added task #" + newIndex + " to SKU [" + skuId.toUpperCase() + "] | Priority: "
                + priority + " | Due: " + dueDate + (description.isEmpty() ? "" : " | Desc: " + description));
    }

    /**
     * Edits the fields of an existing task.
     * At least one of d/, p/, or t/ must be provided.
     *
     * @param cmd The parsed command containing the SKU ID, task index, and fields to update.
     * @throws InvalidIndexException If the provided index is out of bounds or not a number.
     * @throws SKUNotFoundException  If the specified SKU does not exist in the warehouse.
     */
    public void handleEditTask(ParsedCommand cmd) throws InvalidIndexException, SKUNotFoundException {
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
            throw new InvalidIndexException(index, skuId);
        }

        Priority newPriority = CommandHelper.parsePriority(newPriorityStr);
        if (newPriorityStr != null && newPriority == null) {
            return;
        }

        taskList.editSKUTask(index, newDate, newPriority, newDesc);
        SKUTask updated = taskList.getSKUTaskList().get(index - 1);
        Ui.printSuccess("Updated task #" + index + " for SKU [" + skuId.toUpperCase() + "]: " + updated);
    }

    /**
     * Deletes a specific task from an SKU based on its index.
     *
     * @param cmd The parsed command containing the SKU ID and the task index.
     * @throws InvalidIndexException If the provided index is out of bounds or not a number.
     * @throws SKUNotFoundException  If the specified SKU does not exist in the warehouse.
     */
    public void handleDeleteTask(ParsedCommand cmd) throws InvalidIndexException, SKUNotFoundException {
        String skuId = cmd.getArg("n");
        String indexStr = cmd.getArg("i");

        if (skuId == null || indexStr == null) {
            Ui.printError("Usage: deletetask n/SKU_ID i/TASK_INDEX");
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
            Ui.printError("Task index " + index + " is out of range for SKU: " + skuId);
            return;
        }

        taskList.deleteSKUTaskByIndex(index);
        Ui.printSuccess("Deleted task #" + index + " from SKU [" + skuId.toUpperCase() + "].");
    }

    /**
     * Marks a specific task as done after validating the SKU and index.
     *
     * @param cmd The parsed command containing the SKU ID and the task index.
     * @throws MissingArgumentException If required arguments are missing.
     * @throws InvalidIndexException    If the provided index is out of bounds or not a number.
     */
    public void handleMarkTask(ParsedCommand cmd) throws MissingArgumentException, InvalidIndexException {
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
     * Unmarks a completed task after validating the SKU and index.
     *
     * @param cmd The parsed command containing the SKU ID and the task index.
     * @throws MissingArgumentException If required arguments are missing.
     * @throws InvalidIndexException    If the provided index is out of bounds or not a number.
     */
    public void handleUnmarkTask(ParsedCommand cmd) throws MissingArgumentException, InvalidIndexException {
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
}
