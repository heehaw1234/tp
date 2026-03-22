package skutask;

/**
 * Represents a task associated with a Stock Keeping Unit (SKU).
 * Each task has an ID, a priority level, a due date, and a completion status.
 */
public class SKUTask {
    private String skuTaskID;
    private Priority priority;
    private String dueDate;
    private boolean isDone;
    private String taskDescription;

    /**
     * Constructs a new SKUTask with the specified ID, priority, due date, and description.
     *
     * @param skuTaskID       The unique identifier for this task.
     * @param priority        The priority level of this task.
     * @param dueDate         The due date of this task.
     * @param taskDescription A text description of what this task involves.
     */
    public SKUTask(String skuTaskID, Priority priority, String dueDate, String taskDescription) {
        this.skuTaskID = skuTaskID;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isDone = false;
        this.taskDescription = taskDescription;
    }

    /**
     * Constructs a new SKUTask with the specified ID, priority, and due date.
     * Description defaults to empty.
     *
     * @param skuTaskID The unique identifier for this task.
     * @param priority  The priority level of this task.
     * @param dueDate   The due date of this task.
     */
    public SKUTask(String skuTaskID, Priority priority, String dueDate) {
        this(skuTaskID, priority, dueDate, "");
    }

    /**
     * Constructs a new SKUTask with the specified ID, due date, and description.
     * Priority defaults to HIGH.
     *
     * @param skuTaskID       The unique identifier for this task.
     * @param dueDate         The due date of this task.
     * @param taskDescription A text description of what this task involves.
     */
    public SKUTask(String skuTaskID, String dueDate, String taskDescription) {
        this(skuTaskID, Priority.HIGH, dueDate, taskDescription);
    }

    /**
     * Constructs a new SKUTask with the specified ID and due date.
     * Priority defaults to HIGH. Description defaults to empty.
     *
     * @param skuTaskID The unique identifier for this task.
     * @param dueDate   The due date of this task.
     */
    public SKUTask(String skuTaskID, String dueDate) {
        this(skuTaskID, Priority.HIGH, dueDate, "");
    }

    /**
     * Returns the description of this task.
     *
     * @return The task description.
     */
    public String getSKUTaskDescription() {
        return taskDescription;
    }

    /**
     * Sets the description of this task.
     *
     * @param taskDescription The new description to assign.
     */
    public void setSKUTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }


    /**
     * Returns the unique identifier of this task.
     *
     * @return The task ID.
     */
    public String getSKUTaskID() {
        return skuTaskID;
    }

    /**
     * Returns the priority level of this task.
     *
     * @return The task priority.
     */
    public Priority getSKUTaskPriority() {
        return priority;
    }

    /**
     * Returns the due date of this task.
     *
     * @return The due date string.
     */
    public String getSKUTaskDueDate() {
        return dueDate;
    }

    /**
     * Returns whether this task is marked as done.
     *
     * @return True if the task is completed, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        assert !isDone : "Task is already marked as done.";
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        assert isDone : "Task is already marked as not done.";
        this.isDone = false;
    }

    /**
     * Returns a string representation of the task including its status, ID, priority, and due date.
     *
     * @return Formatted string of the task details.
     */
    @Override
    public String toString() {
        String status = isDone ? "[X]" : "[ ]";
        String desc = (taskDescription != null && !taskDescription.isEmpty())
                ? " | Desc: " + taskDescription : "";
        return status + " ID: " + skuTaskID + " | Priority: " + priority + " | Due: " + dueDate + desc;
    }
}
