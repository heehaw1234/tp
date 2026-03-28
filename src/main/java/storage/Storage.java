package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sku.SKUList;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

/**
 * Handles persistent storage of the warehouse state by serializing and
 * deserializing the SKU list to and from a JSON file on disk.
 */

//@@author dorndorn54
public class Storage {
    private static final String FILE_PATH = "Data/storage.json";

    /**
     * Serializes the current warehouse state to a JSON file at {@value FILE_PATH}.
     * Creates the {@code Data/} directory if it does not already exist.
     * The entire hierarchy (SKUList -> SKU -> SKUTaskList -> SKUTask) is saved automatically.
     *
     * @param skuList The list of all SKUs currently in the warehouse.
     * @throws IOException If an I/O error occurs while writing the file.
     */
    public static void saveState(SKUList skuList) throws IOException {
        File dataDir = new File("Data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(skuList, writer);
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    /**
     * Deserializes the warehouse state from the JSON file at {@value FILE_PATH}
     * and populates the provided SKU list with the loaded data.
     * Returns without modifying the structure if the file does not exist.
     *
     * @param skuList The SKU list to populate with the saved SKUs.
     */
    public static void loadState(SKUList skuList) {
        File file = new File(FILE_PATH);
        if (!file.exists()){
            return;
        }

        Gson gson = new Gson();
        try (FileReader reader = new FileReader(FILE_PATH)) {
            SKUList loadedSkus = gson.fromJson(reader, SKUList.class);

            if (loadedSkus != null) {
                skuList.getSKUList().addAll(loadedSkus.getSKUList());
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Error loading: " + e.getMessage());
        } catch (com.google.gson.JsonSyntaxException e) {
            System.out.println("[ERROR] Corrupted or outdated JSON format. Please delete!" + FILE_PATH);
        }
    }
}
