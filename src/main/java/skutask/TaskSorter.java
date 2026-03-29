package skutask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sorts a list of {@link SKUTask} objects by a specified field
 * (date, priority, or completion status) in ascending or descending order.
 *
 * <p>Follows the Single Responsibility Principle: this class handles
 * only the sorting logic, leaving command parsing and output to other classes.</p>
 */
//@@author AkshayPranav19
public class TaskSorter {
    private static final Logger LOGGER = Logger.getLogger(TaskSorter.class.getName());

    private final List<SKUTask> sortedTasks;
    private final String sortField;
    private final boolean ascending;

    /**
     * Constructs a TaskSorter and immediately sorts a defensive copy of the given tasks.
     *
     * @param tasks     The list of tasks to sort (not modified).
     * @param sortField The field to sort by: {@code "date"}, {@code "priority"}, or {@code "status"}.
     * @param ascending True for ascending order, false for descending.
     */
    public TaskSorter(List<SKUTask> tasks, String sortField, boolean ascending) {
        assert tasks != null : "Task list cannot be null";
        assert sortField != null : "Sort field cannot be null";
        assert !sortField.trim().isEmpty() : "Sort field cannot be empty";

        this.sortField = sortField.toLowerCase();
        this.ascending = ascending;
        this.sortedTasks = new ArrayList<>(tasks);

        LOGGER.log(Level.INFO, "TaskSorter created: field=" + this.sortField
                + ", order=" + (ascending ? "asc" : "desc")
                + ", taskCount=" + tasks.size());
        sort();

        assert this.sortedTasks.size() == tasks.size()
                : "Sorted list size must equal input list size";
    }

    /**
     * Returns the sorted list of tasks.
     *
     * @return A new list containing the tasks in sorted order.
     */
    public List<SKUTask> getSortedTasks() {
        return sortedTasks;
    }

    /**
     * Applies the appropriate comparator based on the sort field.
     */
    private void sort() {
        Comparator<SKUTask> comparator = buildComparator();
        if (comparator == null) {
            LOGGER.log(Level.WARNING, "Unknown sort field: " + sortField + ". No sorting applied.");
            return;
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        try {
            sortedTasks.sort(comparator);
            LOGGER.log(Level.INFO, "Successfully sorted " + sortedTasks.size() + " tasks by "
                    + sortField + " (" + (ascending ? "asc" : "desc") + ")");
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Comparator contract violated during sort", e);
        } catch (ClassCastException e) {
            LOGGER.log(Level.SEVERE, "Incompatible task types encountered during sort", e);
        }
    }

    /**
     * Builds a comparator for the configured sort field.
     *
     * @return The appropriate comparator, or null if the sort field is unknown.
     */
    private Comparator<SKUTask> buildComparator() {
        switch (sortField) {
        case "date":
            return buildDateComparator();
        case "priority":
            return buildPriorityComparator();
        case "status":
            return buildStatusComparator();
        default:
            return null;
        }
    }

    /**
     * Compares tasks by due date in lexicographic order (yyyy-MM-dd format).
     *
     * @return A comparator that orders tasks by due date.
     */
    private Comparator<SKUTask> buildDateComparator() {
        return Comparator.comparing(SKUTask::getSKUTaskDueDate);
    }

    /**
     * Compares tasks by priority ordinal (HIGH=0 &lt; MEDIUM=1 &lt; LOW=2).
     * Ascending order places HIGH-priority tasks first.
     *
     * @return A comparator that orders tasks by priority.
     */
    private Comparator<SKUTask> buildPriorityComparator() {
        return Comparator.comparingInt(task -> task.getSKUTaskPriority().ordinal());
    }

    /**
     * Compares tasks by completion status (incomplete before complete in ascending).
     *
     * @return A comparator that orders tasks by done status.
     */
    private Comparator<SKUTask> buildStatusComparator() {
        return Comparator.comparing(SKUTask::isDone);
    }

    /**
     * Checks whether the given field name is a valid sort field.
     *
     * @param field The field name to validate.
     * @return True if the field is one of {@code "date"}, {@code "priority"}, or {@code "status"}.
     */
    public static boolean isValidSortField(String field) {
        assert field != null : "Sort field to validate cannot be null";
        String lower = field.toLowerCase();
        return lower.equals("date") || lower.equals("priority") || lower.equals("status");
    }

    /**
     * Returns the sort field used by this sorter.
     *
     * @return The sort field name (lowercase).
     */
    public String getSortField() {
        return sortField;
    }

    /**
     * Returns whether this sorter is in ascending order.
     *
     * @return True if ascending, false if descending.
     */
    public boolean isAscending() {
        return ascending;
    }
}
