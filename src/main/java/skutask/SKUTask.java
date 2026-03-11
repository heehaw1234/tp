package skutask;

public class SKUTask {
    private String skuTaskID;
    // private String description;
    private Priority priority;
    private String dueDate;
    private boolean isDone;

    public SKUTask(String skuTaskID, Priority priority, String dueDate) {
        this.skuTaskID = skuTaskID;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isDone = false;
    }

    public SKUTask(String skuTaskID, String dueDate) {
        this(skuTaskID, Priority.HIGH, dueDate); // default priority set to HIGH
    }

    public String getSKUTaskID() {
        return skuTaskID;
    }

    public Priority getSKUTaskPriority() {
        return priority;
    }

    public String getSKUTaskDueDate() {
        return dueDate;
    }

    public boolean isDone() {
        return isDone;
    }

    public void mark() {
        this.isDone = true;
    }

    public void unmark() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        String status = isDone ? "[X]" : "[ ]";
        return status + " ID: " + skuTaskID + " | Priority: " + priority + " | Due: " + dueDate;
    }
}
