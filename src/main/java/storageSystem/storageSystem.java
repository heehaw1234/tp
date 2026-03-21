package storageSystem;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sku.SKUList;

import skutask.SKUTaskList;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;


/**
 * Handles persistent storage of the warehouse state by serializing and
 * deserializing the SKU list and task map to and from a JSON file on disk.
 */
public class storageSystem {
    private static final String FILE_PATH = "Data/storage.json";

    /**
     * Serializes the current warehouse state to a JSON file at {@value FILE_PATH}.
     * Creates the {@code Data/} directory if it does not already exist.
     * Both the SKU list and task map are bundled under a single JSON root object.
     *
     * @param skuList The list of all SKUs currently in the warehouse.
     * @param taskMap A mapping of SKU IDs to their respective task lists.
     * @throws IOException If an I/O error occurs while writing the file.
     */
    public static void saveState(SKUList skuList, HashMap<String, SKUTaskList> taskMap) throws IOException{
        File DataDir = new File("Data");
        if(!DataDir.exists()){
            DataDir.mkdirs();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try(FileWriter writer = new FileWriter(FILE_PATH)) {
            JsonObject root = new JsonObject();
            root.add("skuList", gson.toJsonTree(skuList));
            root.add("taskMap", gson.toJsonTree(taskMap));
            gson.toJson(root, writer);
        } catch (IOException e){
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    /**
     * Deserializes the warehouse state from the JSON file at {@value FILE_PATH}
     * and populates the provided SKU list and task map with the loaded data.
     * Returns without modifying either structure if the file does not exist.
     *
     * @param skuList The SKU list to populate with the saved SKUs.
     * @param taskMap The task map to populate with the saved task lists.
     */
    public static void loadState(SKUList skuList, HashMap<String, SKUTaskList> taskMap) {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        Gson gson = new Gson();
        try (FileReader reader = new FileReader(FILE_PATH)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            SKUList loadedSkus = gson.fromJson(root.get("skuList"), SKUList.class);
            Type mapType = new TypeToken<HashMap<String, SKUTaskList>>(){}.getType();
            HashMap<String, SKUTaskList> loadedMap = gson.fromJson(root.get("taskMap"), mapType);

            // Populate the references passed in
            if (loadedSkus != null) skuList.getSKUList().addAll(loadedSkus.getSKUList());
            if (loadedMap != null) taskMap.putAll(loadedMap);
        } catch (IOException e) {
            System.out.println("[ERROR] Error loading: " + e.getMessage());
        }
    }
}
