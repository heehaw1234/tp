package skutask;

import sku.SKU;
import sku.SKUList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the logic for filtering and sorting tasks based on SKU ID, priority, or location.
 * This class acts as a view processor to decouple the data storage from the presentation layer.
 */

// @@author SeanTLY23
public class ViewSKUTask {

    private static final Logger logger = Logger.getLogger(ViewSKUTask.class.getName());
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

        logger.log(Level.FINE, "Aggregating all tasks from {0} SKUs.", fullList.getSKUList().size());
        for (SKU sku : fullList.getSKUList()) {
            allTasks.addAll(sku.getSKUTaskList().getSKUTaskList());
        }

        List<SKUTask> result;

        // Handle SKU Filter: "listtasks n/SKU_ID"
        if (skuFilter != null) {
            logger.log(Level.INFO, "Applying SKU Filter: {0}", skuFilter);
            result = allTasks.stream()
                    .filter(t -> t.getSKUTaskID().equalsIgnoreCase(skuFilter))
                    .collect(Collectors.toList());

            // Handle Priority Filter: "listtasks p/HIGH"
        } else if (priorityFilter != null) {
            logger.log(Level.INFO, "Applying Priority Filter: {0}", priorityFilter);
            return allTasks.stream()
                    .filter(t -> t.getSKUTaskPriority().toString().equalsIgnoreCase(priorityFilter))
                    .collect(Collectors.toList());

            // Handle Distance Filter: "listtasks l/B2"
        } else if (locationFilter != null) {
            logger.log(Level.INFO, "Applying Distance Sorting from Location: {0}", locationFilter);
            result = allTasks.stream()
                    .sorted(Comparator.comparingInt(t -> calculateDistance(t, locationFilter, fullList)))
                    .collect(Collectors.toList());
        } else {
            logger.log(Level.FINE, "No filters set. Returning all {0} tasks.", allTasks.size());
            result = allTasks;
        }
        logger.log(Level.INFO, "Filter/Sort operation returned {0} results.", result.size());
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
            if (s.getSKUID().equalsIgnoreCase(task.getSKUTaskID())) {
                parentSku = s;
                break;
            }
        }
        if (parentSku == null) {
            return 0;
        }
        String targetName = parentSku.getSKULocation().name();
        String currentPosUpper = currentPos.toUpperCase();
        int targetX = targetName.charAt(0) - 'A';
        int targetY = Character.getNumericValue(targetName.charAt(1)) - 1;
        int currentX = currentPosUpper.charAt(0) - 'A';
        int currentY = Character.getNumericValue(currentPosUpper.charAt(1)) - 1;
        return Math.abs(targetX - currentX) + Math.abs(targetY - currentY);
    }
}
