package SKUTask;

/* import SKU.SKU;
import SKU.SKUList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;
*/

public class ViewSKUTask {
    private String skuFilter;
    private String priorityFilter;
    private String locationFilter;

   /*public void listTasks(SKUList fullList) {
        List<SKUTask> allTasks = new ArrayList<>();
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
            result = allTasks.stream()
                    .sorted((t1, t2) -> compareByPriority(t1, t2, priorityFilter))
                    .collect(Collectors.toList());

        // Handle Distance Filter: "listtasks l/B2"
        } else if (locationFilter != null) {
            result = allTasks.stream()
                    .sorted(Comparator.comparingInt(t -> calculateDistance(t, locationFilter, fullList)))
                    .collect(Collectors.toList());
        } else {
            result = allTasks;
        }
    }

    private int compareByPriority(SKUTask t1, SKUTask t2, String filter) {
        int p1 = getPriorityValue(t1.getSKUTaskPriority().toString());
        int p2 = getPriorityValue(t2.getSKUTaskPriority().toString());
        if (filter.equalsIgnoreCase("HIGH")) {
            return Integer.compare(p2, p1);
        } else {
            return Integer.compare(p1, p2);
        }
    }

    private int getPriorityValue(String p) {
        switch (p.toUpperCase()) {
        case "HIGH":
            return 3;
        case "MEDIUM":
            return 2;
        case "LOW":
            return 1;
        default:
            return 0;
        }
    }

    private int calculateDistance(SKUTask task, String currentPos, SKUList fullList) {
        SKU parentSku = null;
        for (SKU s : fullList.getSKUList()) {
            if (s.getSKUID().equals(task.getSKUTaskID())) {
                parentSku = s;
                break;
            }
        }
        String targetName = parentSku.getSKULocation().name();
        int targetX = targetName.charAt(0) - 'A';
        int targetY = Character.getNumericValue(targetName.charAt(1)) - 1;
        int currentX = currentPos.toUpperCase().charAt(0) - 'A';
        int currentY = Character.getNumericValue(currentPos.charAt(1)) - 1;
        return Math.abs(targetX - currentX) + Math.abs(targetY - currentY);
    } */
}
