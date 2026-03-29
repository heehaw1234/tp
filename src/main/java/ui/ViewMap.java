package ui;

import sku.SKU;
import sku.SKUList;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ViewMap {
    private static final Logger logger = Logger.getLogger(ViewMap.class.getName());

    public void printTaskMap(SKUList fullList) {
        logger.log(Level.INFO, "Starting printTaskMap rendering.");

        if (fullList == null) {
            logger.log(Level.SEVERE, "printTaskMap failed: Received a null SKUList pointer.");
            assert false : "Internal Error: ViewMap received a null SKUList pointer";
            return;
        }

        Map<String, Integer> counts = new HashMap<>();
        for (SKU sku : fullList.getSKUList()) {
            assert sku.getSKULocation() != null : "Data Error: SKU " + sku.getSKUID() + " has no location";

            String loc = sku.getSKULocation().name();
            int size = sku.getSKUTaskList().getSize();
            counts.put(loc, counts.getOrDefault(loc, 0) + size);
        }

        System.out.println("\n--- Warehouse Task Distribution (A1-C3) ---");
        for (char row = 'A'; row <= 'C'; row++) {
            StringBuilder rowDataForLog = new StringBuilder();
            for (int col = 1; col <= 3; col++) {
                String loc = "" + row + col;
                int count = counts.getOrDefault(loc, 0);
                System.out.printf("[%s: %02d] ", loc, counts.getOrDefault(loc, 0));
                rowDataForLog.append(loc).append(":").append(count).append(" ");
            }
            System.out.println();
            logger.log(Level.FINEST, "Rendered Row {0}: {1}",
                    new Object[]{row, rowDataForLog.toString()});
        }
        System.out.println("-------------------------------------------\n");
        logger.log(Level.INFO, "Warehouse map successfully displayed to user.");
    }
}
