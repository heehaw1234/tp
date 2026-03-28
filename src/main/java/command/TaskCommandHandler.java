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

    public TaskCommandHandler(SKUList skuList) {
        this.skuList = skuList;
    }

    public void handleAddSkuTask(ParsedCommand cmd) throws MissingArgumentException, SKUNotFoundException {
        assert cmd != null : "Internal Error: ParsedCommand cannot be null";

        String skuId = cmd.getArg("n");
        String dueDate = cmd.getArg("d");

        if (skuId == null || dueDate == null) {
            Ui.printError("Usage: addskutask n/SKU_ID d/DUE_DATE [p/PRIORITY] [t/DESCRIPTION]");
            return;
        }

        SKU targetSku = skuList.findByID(skuId);
        if (targetSku == null) {
            LOGGER.log(Level.WARNING, "Failed to add task: SKU [" + skuId + "] not found.");
            Ui.printError("SKU not found: " + skuId + ". Use 'addsku' to register it first.");
            return;
        }

        Priority priority = CommandHelper.parsePriorityOrDefault(cmd);
        if (priority == null) {
            return;
        }

        String description = cmd.hasArg("t") ? cmd.getArg("t") : "";
        SKUTaskList taskList = targetSku.getSKUTaskList();

        try {
            taskList.addSKUTask(skuId.toUpperCase(), priority, dueDate, description);
            int newIndex = taskList.getSize();

            LOGGER.log(Level.INFO, "Added task #" + newIndex + " to SKU [" + skuId + "]");
            Ui.printSuccess("Added task #" + newIndex + " to SKU [" + skuId.toUpperCase() + "] | Priority: "
                    + priority + " | Due: " + dueDate + (description.isEmpty() ? "" : " | Desc: " + description));
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Domain validation rejected task addition", e);
            Ui.printError("Failed to add task due to invalid data: " + e.getMessage());
        }
    }

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

    public void handleDeleteTask(ParsedCommand cmd) throws InvalidIndexException, SKUNotFoundException {
        assert cmd != null : "Internal Error: ParsedCommand cannot be null";

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
            LOGGER.log(Level.WARNING, "Failed to delete task: Index " + index + " out of bounds for SKU ["
                    + skuId + "]");
            Ui.printError("Task index " + index + " is out of range for SKU: " + skuId);
            return;
        }

        try {
            taskList.deleteSKUTaskByIndex(index);
            LOGGER.log(Level.INFO, "Deleted task #" + index + " from SKU [" + skuId + "]");
            Ui.printSuccess("Deleted task #" + index + " from SKU [" + skuId.toUpperCase() + "].");
        } catch (IndexOutOfBoundsException e) {
            LOGGER.log(Level.SEVERE, "Index out of bounds during deletion", e);
            throw new InvalidIndexException(index, skuId);
        }
    }

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
}
