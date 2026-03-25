package skutask;

import java.util.ArrayList;

/**
 * Manages a list of SKU tasks.
 * Provides methods to add, delete, retrieve, and display tasks.
 */
public class SKUTaskList {
    private final ArrayList<SKUTask> skuTaskList;

    /**
     * Constructs an empty SKUTaskList.
     */
    public SKUTaskList() {
        this.skuTaskList = new ArrayList<>();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The size of the task list.
     */
    public int getSize() {
        return skuTaskList.size();
    }

    /**
     * Checks whether the task list is empty.
     *
     * @return True if the task list contains no tasks, false otherwise.
     */
    public boolean isEmpty() {
        return skuTaskList.isEmpty();
    }

    /**
     * Adds a new task with the specified SKU ID, priority, due date, and description.
     *
     * @param skuID       The SKU identifier for the task.
     * @param priority    The priority level of the task.
     * @param dueDate     The due date of the task.
     * @param description A text description of what this task involves.
     */
    public void addSKUTask(String skuID, Priority priority, String dueDate, String description) {
        SKUTask newTask = new SKUTask(skuID, priority, dueDate, description);
        skuTaskList.add(newTask);
    }

    /**
     * Adds a new task with the specified SKU ID, priority, and due date.
     * Description defaults to empty.
     *
     * @param skuID    The SKU identifier for the task.
     * @param priority The priority level of the task.
     * @param dueDate  The due date of the task.
     */
    public void addSKUTask(String skuID, Priority priority, String dueDate) {
        addSKUTask(skuID, priority, dueDate, "");
    }

    /**
     * Adds a new task with the specified SKU ID, due date, and description.
     * Priority defaults to HIGH.
     *
     * @param skuID       The SKU identifier for the task.
     * @param dueDate     The due date of the task.
     * @param description A text description of what this task involves.
     */
    public void addSKUTask(String skuID, String dueDate, String description) {
        SKUTask newSkuTask = new SKUTask(skuID, dueDate, description);
        skuTaskList.add(newSkuTask);
    }

    /**
     * Adds a new task with the specified SKU ID and due date.
     * Priority defaults to HIGH. Description defaults to empty.
     *
     * @param skuID   The SKU identifier for the task.
     * @param dueDate The due date of the task.
     */
    public void addSKUTask(String skuID, String dueDate) {
        addSKUTask(skuID, dueDate, "");
    }

    /**
     * Finds the index of a task by its SKU ID.
     *
     * @param skuID The SKU identifier to search for.
     * @return The index of the task, or -1 if not found.
     */
    private int getIndexOfSKUTask(String skuID) {
        int size = getSize();
        for (int i = 0; i < size; i++) {
            if (skuTaskList.get(i).getSKUTaskID().equals(skuID)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Deletes the task with the specified SKU ID from the list.
     * If the task is not found, the list remains unchanged.
     *
     * @param skuIDToDelete The SKU identifier of the task to delete.
     */
    public void deleteSKUTask(String skuIDToDelete) {
        int idxToDelete = getIndexOfSKUTask(skuIDToDelete);
        if (idxToDelete != -1) {
            skuTaskList.remove(idxToDelete);
        }
    }

    /**
     * Deletes the task at the given 1-based index from SKUTasklist.
     *
     * @param taskIndex The 1-based index of the task to delete.
     */
    public void deleteSKUTaskByIndex(int taskIndex) {
        skuTaskList.remove(taskIndex - 1);
    }

    /**
     * Returns the underlying list of SKU tasks.
     *
     * @return The ArrayList of SKUTask objects.
     */
    public ArrayList<SKUTask> getSKUTaskList() {
        return skuTaskList;
    }

    /**
     * Prints all tasks in the list to standard output, numbered starting from 1.
     */
    public void printSKUTaskList() {
        int i = 1;
        for (SKUTask currSkuTask : skuTaskList) {
            System.out.println(i + ". " + currSkuTask);
            i++;
        }
    }

    /**
     * Edits the fields of the task at the given 1-based index.
     * Only non-null values are applied — omitted fields remain unchanged.
     *
     * @param taskIndex   The index of the task to edit.
     * @param newDueDate  The new due date, or null to leave unchanged.
     * @param newPriority The new priority, or null to leave unchanged.
     * @param newDesc     The new description, or null to leave unchanged.
     */
    public void editSKUTask(int taskIndex, String newDueDate, Priority newPriority, String newDesc) {
        SKUTask task = skuTaskList.get(taskIndex - 1);
        if (newDueDate != null) {
            task.setSKUTaskDueDate(newDueDate);
        }
        if (newPriority != null) {
            task.setSKUTaskPriority(newPriority);
        }
        if (newDesc != null) {
            task.setSKUTaskDescription(newDesc);
        }
    }

    /**
     * Marks the task at the given 1-based index as done.
     *
     * @param taskIndex The 1-based index of the task to mark.
     */
    public void markTask(int taskIndex) {
        skuTaskList.get(taskIndex - 1).mark();
    }

    /**
     * Marks the task at the given 1-based index as not done.
     *
     * @param taskIndex The 1-based index of the task to unmark.
     */
    public void unmarkTask(int taskIndex) {
        skuTaskList.get(taskIndex - 1).unmark();
    }
}

