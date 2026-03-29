package skutask;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@@author AkshayPranav19
public class TaskSorterTest {

    @Test
    public void sortByDate_ascending_earliestFirst() {
        List<SKUTask> tasks = new ArrayList<>();
        tasks.add(new SKUTask("SKU1", Priority.HIGH, "2026-12-01", "late"));
        tasks.add(new SKUTask("SKU1", Priority.HIGH, "2026-01-01", "early"));
        tasks.add(new SKUTask("SKU1", Priority.HIGH, "2026-06-15", "mid"));

        TaskSorter sorter = new TaskSorter(tasks, "date", true);
        List<SKUTask> sorted = sorter.getSortedTasks();

        assertEquals("2026-01-01", sorted.get(0).getSKUTaskDueDate());
        assertEquals("2026-06-15", sorted.get(1).getSKUTaskDueDate());
        assertEquals("2026-12-01", sorted.get(2).getSKUTaskDueDate());
    }

    @Test
    public void sortByDate_descending_latestFirst() {
        List<SKUTask> tasks = new ArrayList<>();
        tasks.add(new SKUTask("SKU1", Priority.HIGH, "2026-01-01", "early"));
        tasks.add(new SKUTask("SKU1", Priority.HIGH, "2026-12-01", "late"));

        TaskSorter sorter = new TaskSorter(tasks, "date", false);
        List<SKUTask> sorted = sorter.getSortedTasks();

        assertEquals("2026-12-01", sorted.get(0).getSKUTaskDueDate());
        assertEquals("2026-01-01", sorted.get(1).getSKUTaskDueDate());
    }

    @Test
    public void sortByPriority_ascending_highFirst() {
        List<SKUTask> tasks = new ArrayList<>();
        tasks.add(new SKUTask("SKU1", Priority.LOW, "2026-01-01", "low"));
        tasks.add(new SKUTask("SKU1", Priority.HIGH, "2026-01-01", "high"));
        tasks.add(new SKUTask("SKU1", Priority.MEDIUM, "2026-01-01", "med"));

        TaskSorter sorter = new TaskSorter(tasks, "priority", true);
        List<SKUTask> sorted = sorter.getSortedTasks();

        assertEquals(Priority.HIGH, sorted.get(0).getSKUTaskPriority());
        assertEquals(Priority.MEDIUM, sorted.get(1).getSKUTaskPriority());
        assertEquals(Priority.LOW, sorted.get(2).getSKUTaskPriority());
    }

    @Test
    public void sortByPriority_descending_lowFirst() {
        List<SKUTask> tasks = new ArrayList<>();
        tasks.add(new SKUTask("SKU1", Priority.HIGH, "2026-01-01", "high"));
        tasks.add(new SKUTask("SKU1", Priority.LOW, "2026-01-01", "low"));

        TaskSorter sorter = new TaskSorter(tasks, "priority", false);
        List<SKUTask> sorted = sorter.getSortedTasks();

        assertEquals(Priority.LOW, sorted.get(0).getSKUTaskPriority());
        assertEquals(Priority.HIGH, sorted.get(1).getSKUTaskPriority());
    }

    @Test
    public void sortByStatus_ascending_incompleteFirst() {
        List<SKUTask> tasks = new ArrayList<>();
        SKUTask doneTask = new SKUTask("SKU1", Priority.HIGH, "2026-01-01", "done");
        doneTask.mark();
        SKUTask pendingTask = new SKUTask("SKU1", Priority.HIGH, "2026-01-01", "pending");
        tasks.add(doneTask);
        tasks.add(pendingTask);

        TaskSorter sorter = new TaskSorter(tasks, "status", true);
        List<SKUTask> sorted = sorter.getSortedTasks();

        assertEquals(false, sorted.get(0).isDone());
        assertEquals(true, sorted.get(1).isDone());
    }

    @Test
    public void sortEmptyList_returnsEmpty() {
        List<SKUTask> tasks = new ArrayList<>();
        TaskSorter sorter = new TaskSorter(tasks, "date", true);
        assertTrue(sorter.getSortedTasks().isEmpty());
    }

    @Test
    public void sortSingleTask_returnsSameTask() {
        List<SKUTask> tasks = new ArrayList<>();
        tasks.add(new SKUTask("SKU1", Priority.HIGH, "2026-01-01", "only"));

        TaskSorter sorter = new TaskSorter(tasks, "date", true);
        assertEquals(1, sorter.getSortedTasks().size());
        assertEquals("only", sorter.getSortedTasks().get(0).getSKUTaskDescription());
    }

    @Test
    public void sortByUnknownField_noSortingApplied() {
        List<SKUTask> tasks = new ArrayList<>();
        tasks.add(new SKUTask("SKU1", Priority.LOW, "2026-12-01", "second"));
        tasks.add(new SKUTask("SKU1", Priority.HIGH, "2026-01-01", "first"));

        TaskSorter sorter = new TaskSorter(tasks, "invalid", true);
        List<SKUTask> sorted = sorter.getSortedTasks();

        assertEquals("second", sorted.get(0).getSKUTaskDescription());
        assertEquals("first", sorted.get(1).getSKUTaskDescription());
    }

    @Test
    public void isValidSortField_validFields_returnsTrue() {
        assertTrue(TaskSorter.isValidSortField("date"));
        assertTrue(TaskSorter.isValidSortField("priority"));
        assertTrue(TaskSorter.isValidSortField("status"));
        assertTrue(TaskSorter.isValidSortField("DATE"));
    }

    @Test
    public void isValidSortField_invalidField_returnsFalse() {
        assertEquals(false, TaskSorter.isValidSortField("name"));
        assertEquals(false, TaskSorter.isValidSortField(""));
    }

    @Test
    public void sort_doesNotMutateOriginalList() {
        List<SKUTask> original = new ArrayList<>();
        original.add(new SKUTask("SKU1", Priority.LOW, "2026-12-01", "late"));
        original.add(new SKUTask("SKU1", Priority.HIGH, "2026-01-01", "early"));

        TaskSorter sorter = new TaskSorter(original, "priority", true);
        List<SKUTask> sorted = sorter.getSortedTasks();

        assertEquals(Priority.LOW, original.get(0).getSKUTaskPriority());
        assertEquals(Priority.HIGH, sorted.get(0).getSKUTaskPriority());
    }
}
