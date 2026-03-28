package sku;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a list of Stock Keeping Units (SKUs).
 * Provides operations to manage the collection of SKUs, such as adding and deleting.
 */
public class SKUList {
    private static final Logger LOGGER = Logger.getLogger(SKUList.class.getName());
    private final ArrayList<SKU> skuList;

    /**
     * Initializes an empty list to store SKUs.
     */
    public SKUList() {
        this.skuList = new ArrayList<SKU>();
    }

    /**
     * Retrieves the total number of SKUs currently in the list.
     *
     * @return The integer count of SKUs being tracked.
     */
    public int getSize() {
        return this.skuList.size();
    }

    /**
     * Checks whether the SKU list is completely empty.
     *
     * @return True if there are no SKUs in the list, false otherwise.
     */
    public boolean isEmpty() {
        return skuList.isEmpty();
    }

    /**
     * Adds a new SKU with the specified ID and location to the list.
     *
     * @param skuID The unique alphanumeric identifier for the new SKU.
     * @param skuLocation The physical sector location of the new SKU.
     */
    public void addSKU(String skuID, Location skuLocation) {
        SKU sku = new SKU(skuID, skuLocation);
        skuList.add(sku);
        LOGGER.log(Level.INFO, "Successfully added SKU: [" + skuID + "] at Location: " + skuLocation);
        assert skuList.size() > 0 : "SKUList should have size > 0 after adding an SKU";
    }

    /**
     * Removes the SKU from the list that match the specified SKU ID.
     *
     * @param skuID The unique alphanumeric identifier of the SKU to be removed.
     */
    public void deleteSKU(String skuID) {
        boolean isRemoved = skuList.removeIf(sku -> sku.getSKUID().equals(skuID));

        if (isRemoved) {
            LOGGER.log(Level.INFO, "Successfully deleted SKU: [" + skuID + "]");
        } else {
            LOGGER.log(Level.WARNING, "Attempted to delete non-existent SKU: [" + skuID + "]");
        }
    }

    /**
     * Finds and returns the SKU with the given ID (case-insensitive).
     *
     * @param skuId The alphanumeric identifier to search for.
     * @return The matching SKU object, or null if no match is found.
     */
    public SKU findByID(String skuId) {
        for (SKU sku : skuList) {
            if (sku.getSKUID().equalsIgnoreCase(skuId)) {
                return sku;
            }
        }
        return null;
    }

    /**
     * Retrieves the entire list of SKUs.
     *
     * @return The ArrayList containing all tracked SKU objects.
     */
    public ArrayList<SKU> getSKUList() {
        return skuList;
    }
}
