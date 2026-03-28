package storage;

import sku.SKU;
import sku.SKUList;
import skutask.SKUTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the exportation of the warehouse inventory and tasks
 * into a human-readable text file.
 */

//@@author omcodedthis
public class Export {
    private static final Logger LOGGER = Logger.getLogger(Export.class.getName());
    private static final String EXPORT_FILE_PATH = "Data/ItemTasker_Export.txt";

    public static void exportToTextFile(SKUList skuList) throws IOException {
        assert skuList != null : "Internal Error: Cannot export a null SKUList";

        LOGGER.log(Level.INFO, "Initiating warehouse export to " + EXPORT_FILE_PATH);

        File dataDir = new File("Data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            LOGGER.log(Level.INFO, "Created missing Data/ directory for export.");
        }

        try (FileWriter writer = new FileWriter(EXPORT_FILE_PATH)) {
            writer.write("==========================================================================\n");
            writer.write("                        WAREHOUSE INVENTORY EXPORT                        \n");
            writer.write("==========================================================================\n\n");

            if (skuList.isEmpty()) {
                writer.write("The warehouse is currently empty. No SKUs to report.\n");
                LOGGER.log(Level.INFO, "Exported empty warehouse state.");
                return;
            }

            for (SKU sku : skuList.getSKUList()) {
                writer.write("SKU: [" + sku.getSKUID().toUpperCase() + "] | Location: " + sku.getSKULocation() + "\n");

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
            LOGGER.log(Level.INFO, "Successfully exported " + skuList.getSize() + " SKUs to text file.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to write export file at " + EXPORT_FILE_PATH, e);
            throw e;
        }
    }
}
