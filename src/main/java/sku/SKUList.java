package sku;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a list of Stock Keeping Units (SKUs).
 * Provides operations to manage the collection of SKUs, such as adding and deleting.
 */

//@@author omcodedthis
public class SKUList {
    private static final Logger LOGGER = Logger.getLogger(SKUList.class.getName());
    private final ArrayList<SKU> skuList;

    public SKUList() {
        this.skuList = new ArrayList<SKU>();
        LOGGER.log(Level.INFO, "Initialized new empty SKUList.");
    }

    public int getSize() {
        return this.skuList.size();
    }

    public boolean isEmpty() {
        return skuList.isEmpty();
    }

    public void addSKU(String skuID, Location skuLocation) {
        if (skuID == null || skuID.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Attempted to add SKU with null/empty ID.");
            throw new IllegalArgumentException("Internal Error: skuID cannot be null or empty");
        }
        if (skuLocation == null) {
            LOGGER.log(Level.WARNING, "Attempted to add SKU with null location.");
            throw new IllegalArgumentException("Internal Error: skuLocation cannot be null");
        }

        if (findByID(skuID) != null) {
            LOGGER.log(Level.WARNING, "Duplicate SKU ID: " + skuID);
            throw new IllegalArgumentException("SKU ID already exists.");
        }

        SKU sku = new SKU(skuID, skuLocation);
        skuList.add(sku);
        LOGGER.log(Level.INFO, "Successfully added SKU: [" + skuID + "] at Location: " + skuLocation);

        assert skuList.size() > 0 : "Internal Error: SKUList should have size > 0 after adding";
    }

    public void deleteSKU(String skuID) {
        assert skuID != null && !skuID.trim().isEmpty() : "Internal Error: skuID to delete cannot be null";

        boolean isRemoved = skuList.removeIf(sku -> sku.getSKUID().equalsIgnoreCase(skuID));

        if (isRemoved) {
            LOGGER.log(Level.INFO, "Successfully deleted SKU: [" + skuID + "]");
        } else {
            LOGGER.log(Level.WARNING, "Attempted to delete non-existent SKU: [" + skuID + "]");
        }
    }

    public SKU findByID(String skuId) {
        assert skuId != null && !skuId.trim().isEmpty() : "Internal Error: skuId to find cannot be null";

        for (SKU sku : skuList) {
            if (sku.getSKUID().equalsIgnoreCase(skuId)) {
                return sku;
            }
        }
        return null;
    }

    public ArrayList<SKU> getSKUList() {
        return skuList;
    }
}
