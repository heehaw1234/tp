package skutask;

import sku.SKU;
import sku.SKUList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * Handles the logic for filtering and sorting tasks based on SKU ID, priority, or location.
 * This class acts as a view processor to decouple the data storage from the presentation layer.
 */
public class ViewSKUTask {
    private String skuFilter;
    private String priorityFilter;
    private String locationFilter;

    /**
     * Processes and returns a list of tasks from the warehouse based on the currently set filters.
     * Filters are applied in a specific order: SKU ID takes precedence, followed by Priority,
     * then Location-based distance sorting.
     *
     * @param fullList The master list of all SKUs in the system.
     * @return A filtered or sorted list of {@code SKUTask} objects.
     */
    public List<SKUTask> listTasks(SKUList fullList) {
        List<SKUTask> allTasks = new ArrayList<>();

        // ARCHITECTURE FIX: Extract all tasks directly from the encapsulated SKU objects
        for (SKU sku : fullList.getSKUList()) {
            allTasks.addAll(sku.getSKUTaskList().getSKUTaskList());
        }

        List<SKUTask> result;

        // Handle SKU Filter: "listtasks n/SKU_ID"
        if (skuFilter != null) {
            result = allTasks.stream()
                    .filter(t -> t.getSKUTaskID().equalsIgnoreCase(skuFilter))
                    .collect(Collectors.toList());

            // Handle Priority Filter: "listtasks p/HIGH"
        } else if (priorityFilter != null) {
            return allTasks.stream()
                    .filter(t -> t.getSKUTaskPriority().toString().equalsIgnoreCase(priorityFilter))
                    .collect(Collectors.toList());

            // Handle Distance Filter: "listtasks l/B2"
        } else if (locationFilter != null) {
            result = allTasks.stream()
                    .sorted(Comparator.comparingInt(t -> calculateDistance(t, locationFilter, fullList)))
                    .collect(Collectors.toList());
        } else {
            result = allTasks;
        }
        return result;
    }

    public void setSkuFilter(String skuFilter) {
        this.skuFilter = skuFilter;
    }

    public void setPriorityFilter(String priorityFilter) {
        this.priorityFilter = priorityFilter;
    }

    public void setLocationFilter(String locationFilter) {
        this.locationFilter = locationFilter;
    }

    /**
     * Calculates the distance between a task's physical location and a given point.
     *
     * @param task       The task whose location needs to be calculated.
     * @param currentPos The starting position string (e.g., "A1").
     * @param fullList   The master SKU list used to find the physical location of the task's SKU.
     * @return The integer distance between the two points. Returns 0 if SKU is not found.
     */
    public int calculateDistance(SKUTask task, String currentPos, SKUList fullList) {
        SKU parentSku = null;
        for (SKU s : fullList.getSKUList()) {
            if (s.getSKUID().equals(task.getSKUTaskID())) {
                parentSku = s;
                break;
            }
        }
        if (parentSku == null) {
            return 0;
        }
        String targetName = parentSku.getSKULocation().name();
        int targetX = targetName.charAt(0) - 'A';
        int targetY = Character.getNumericValue(targetName.charAt(1)) - 1;
        int currentX = currentPos.toUpperCase().charAt(0) - 'A';
        int currentY = Character.getNumericValue(currentPos.charAt(1)) - 1;
        return Math.abs(targetX - currentX) + Math.abs(targetY - currentY);
    }
}
