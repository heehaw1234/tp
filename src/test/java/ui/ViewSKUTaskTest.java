package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sku.Location;
import sku.SKU;
import sku.SKUList;
import skutask.Priority;
import skutask.SKUTask;
import skutask.ViewSKUTask;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewSKUTaskTest {

    private ViewSKUTask viewer;
    private SKUList skuList;

    @BeforeEach
    public void setUp() {
        viewer = new ViewSKUTask();
        skuList = new SKUList();

        // Setup Pallet A at A1 (0,0) with 2 tasks directly inside the SKU
        skuList.addSKU("PALLET-A", Location.A1);
        SKU palletA = skuList.getSKUList().get(0);
        palletA.getSKUTaskList().addSKUTask("PALLET-A", Priority.HIGH, "2026-04-01", "high priority task");
        palletA.getSKUTaskList().addSKUTask("PALLET-A", Priority.MEDIUM, "2026-04-05", "medium priority task");

        // Setup Pallet B at B1 (1,0) with 1 task directly inside the SKU
        skuList.addSKU("PALLET-B", Location.B1);
        SKU palletB = skuList.getSKUList().get(1);
        palletB.getSKUTaskList().addSKUTask("PALLET-B", Priority.HIGH, "2026-04-10", "pallet b task");
    }

    @Test
    public void listTasks_noFilters_returnsAllTasks() {
        List<SKUTask> results = viewer.listTasks(skuList);
        assertEquals(3, results.size());
    }

    @Test
    public void listTasks_skuFilter_returnsOnlyMatchingSku() {
        viewer.setSkuFilter("PALLET-A");
        List<SKUTask> results = viewer.listTasks(skuList);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> t.getSKUTaskID().equals("PALLET-A")));
    }

    @Test
    public void listTasks_priorityFilter_returnsOnlyMatchingPriority() {
        viewer.setPriorityFilter("HIGH");
        List<SKUTask> results = viewer.listTasks(skuList);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> t.getSKUTaskPriority() == Priority.HIGH));
    }

    @Test
    public void listTasks_locationFilter_sortsByDistance() {
        // A1 is at (0,0). PALLET-A is at A1 (dist 0), PALLET-B is at B1 (dist 1)
        viewer.setLocationFilter("A1");
        List<SKUTask> results = viewer.listTasks(skuList);

        assertEquals(3, results.size());
        // First two should be PALLET-A (dist 0)
        assertEquals("PALLET-A", results.get(0).getSKUTaskID());
        assertEquals("PALLET-A", results.get(1).getSKUTaskID());
        // Last should be PALLET-B (dist 1)
        assertEquals("PALLET-B", results.get(2).getSKUTaskID());
    }

    @Test
    public void calculateDistance_validLocations_returnsDistance() {
        // Retrieve taskB strictly from the nested SKU object architecture
        SKUTask taskB = skuList.getSKUList().get(1).getSKUTaskList().getSKUTaskList().get(0);
        int distance = viewer.calculateDistance(taskB, "A1", skuList);

        assertEquals(1, distance);

        // Distance from C3 (2,2) to B1 (1,0) is |2-1| + |2-0| = 3
        int distanceToC3 = viewer.calculateDistance(taskB, "C3", skuList);
        assertEquals(3, distanceToC3);
    }

    @Test
    public void listTasks_emptySkuList_returnsEmptyList() {
        SKUList emptyList = new SKUList();
        List<SKUTask> results = viewer.listTasks(emptyList);
        assertTrue(results.isEmpty());
    }

    @Test
    public void listTasks_priorityFilterCaseInsensitive_returnsCorrectTasks() {
        viewer.setPriorityFilter("high");
        List<SKUTask> results = viewer.listTasks(skuList);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> t.getSKUTaskPriority() == Priority.HIGH));
    }

    @Test
    public void listTasks_multipleFiltersSet_prioritizesSkuFilter() {

        viewer.setSkuFilter("PALLET-B");
        viewer.setPriorityFilter("MEDIUM");
        List<SKUTask> results = viewer.listTasks(skuList);

        assertEquals(1, results.size());
        assertEquals("PALLET-B", results.get(0).getSKUTaskID());
    }

    @Test
    public void listTasks_locationFilterFromFarEnd_sortsCorrectly() {
        // Origin C3 (2,2).
        // PALLET-B at B1 (1,0) dist: |2-1| + |2-0| = 3
        // PALLET-A at A1 (0,0) dist: |2-0| + |2-0| = 4
        viewer.setLocationFilter("C3");
        List<SKUTask> results = viewer.listTasks(skuList);

        // PALLET-B should come first now as it is closer to C3 than A1 is
        assertEquals("PALLET-B", results.get(0).getSKUTaskID());
        assertEquals("PALLET-A", results.get(2).getSKUTaskID());
    }

    @Test
    public void listTasks_clearingFilters_returnsAllTasksAgain() {
        viewer.setSkuFilter("PALLET-B");
        viewer.listTasks(skuList); // Run once with filter

        viewer.setSkuFilter(null); // Clear filter
        List<SKUTask> results = viewer.listTasks(skuList);

        assertEquals(3, results.size());
    }

}
