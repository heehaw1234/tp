package storage;

import sku.SKU;
import sku.SKUList;
import skutask.SKUTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Export {
    private static final Logger LOGGER = Logger.getLogger(Export.class.getName());
    private static final String EXPORT_FILE_PATH = Paths.get("Data", "ItemTasker_Export.txt").toString();
    private static final String LS = System.lineSeparator();

    public static void exportToTextFile(SKUList skuList) throws IOException {
        if (skuList == null) {
            LOGGER.log(Level.SEVERE, "Export failed: Provided SKUList is null.");
            throw new IllegalArgumentException("Internal Error: Cannot export a null SKUList");
        }

        assert EXPORT_FILE_PATH != null && !EXPORT_FILE_PATH.trim().isEmpty() : "Export file path must be defined";

        LOGGER.log(Level.INFO, "Initiating warehouse export to " + EXPORT_FILE_PATH);

        File exportFile = new File(EXPORT_FILE_PATH);
        File dataDir = exportFile.getParentFile();

        if (dataDir != null) {
            if (!dataDir.exists()) {
                if (!dataDir.mkdirs()) {
                    LOGGER.log(Level.SEVERE, "Failed to create missing directory for export: " + dataDir.getPath());
                    throw new IOException("Failed to create directory: " + dataDir.getPath());
                }
                LOGGER.log(Level.INFO, "Created missing directory for export: " + dataDir.getPath());
            } else if (!dataDir.isDirectory()) {
                LOGGER.log(Level.SEVERE, "Cannot export: '" + dataDir.getPath()
                        + "' exists but is not a directory.");
                throw new IOException("Target path '" + dataDir.getPath() + "' exists but is not a directory.");
            }
        }

        try (FileWriter writer = new FileWriter(EXPORT_FILE_PATH)) {
            writer.write("==========================================================================" + LS);
            writer.write("                        WAREHOUSE INVENTORY EXPORT                        " + LS);
            writer.write("==========================================================================" + LS + LS);

            if (skuList.isEmpty()) {
                writer.write("The warehouse is currently empty. No SKUs to report." + LS);
                LOGGER.log(Level.INFO, "Exported empty warehouse state.");
                return;
            }

            for (SKU sku : skuList.getSKUList()) {
                assert sku != null : "SKU object in the list should never be null";
                assert sku.getSKUTaskList() != null : "SKU Task List should never be null";

                writer.write("SKU: [" + sku.getSKUID().toUpperCase() + "] | Location: " + sku.getSKULocation()
                        + LS);

                if (sku.getSKUTaskList().isEmpty()) {
                    writer.write("  -> No tasks assigned." + LS);
                } else {
                    int taskNumber = 1;
                    for (SKUTask task : sku.getSKUTaskList().getSKUTaskList()) {
                        writer.write("  " + taskNumber + ". " + task.toString() + LS);
                        taskNumber++;
                    }
                }
                writer.write("--------------------------------------------------------------------------" + LS);
            }
            LOGGER.log(Level.INFO, "Successfully exported " + skuList.getSize() + " SKUs to text file.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to write export file at " + EXPORT_FILE_PATH, e);
            throw e;
        }
    }
}
