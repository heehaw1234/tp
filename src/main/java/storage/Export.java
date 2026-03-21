package storage;

import sku.SKU;
import sku.SKUList;
import skutask.SKUTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Handles the exportation of the warehouse inventory and tasks
 * into a human-readable text file.
 */
public class Export {
    private static final String EXPORT_FILE_PATH = "Data/ItemTasker_Export.txt";

    /**
     * Reads the current system state and writes a formatted report to a text file.
     *
     * @param skuList The master list of SKUs to be exported.
     * @throws IOException If an error occurs during file writing.
     */
    public static void exportToTextFile(SKUList skuList) throws IOException {
        File dataDir = new File("Data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(EXPORT_FILE_PATH)) {
            writer.write("==========================================================================\n");
            writer.write("                        WAREHOUSE INVENTORY EXPORT                        \n");
            writer.write("==========================================================================\n\n");

            if (skuList.isEmpty()) {
                writer.write("The warehouse is currently empty. No SKUs to report.\n");
                return;
            }

            for (SKU sku : skuList.getSKUList()) {
                writer.write("SKU: [" + sku.getSKUID().toUpperCase() + "] | Location: " + sku.getSKULocation()
                        + "\n");

                if (sku.getSKUTaskList().isEmpty()) {
                    writer.write("  -> No tasks assigned.\n");
                } else {
                    int taskNumber = 1;
                    for (SKUTask task : sku.getSKUTaskList().getSKUTaskList()) {
                        writer.write("  " + taskNumber + ". " + task.toString() + "\n");
                        taskNumber++;
                    }
                }
                writer.write("--------------------------------------------------------------------------\n");
            }
        }
    }
}
