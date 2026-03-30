package sku;

import skutask.SKUTaskList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a Stock Keeping Unit (SKU) in the inventory ticketing system.
 */
//@@author omcodedthis
public class SKU {
    private static final Logger LOGGER = Logger.getLogger(SKU.class.getName());
    private final String skuID;
    private final SKUTaskList skuTaskList;
    private Location skuLocation;

    public SKU(String skuID, Location skuLocation) {
        if (skuID == null || skuID.trim().isEmpty()) {
            LOGGER.log(Level.SEVERE, "Failed to instantiate SKU: ID is null or empty.");
            throw new IllegalArgumentException("Internal Error: SKU ID cannot be null or empty");
        }
        if (skuLocation == null) {
            LOGGER.log(Level.SEVERE, "Failed to instantiate SKU: Location is null.");
            throw new IllegalArgumentException("Internal Error: SKU Location cannot be null");
        }

        this.skuID = skuID.trim().toUpperCase();
        this.skuLocation = skuLocation;
        this.skuTaskList = new SKUTaskList();

        assert this.skuTaskList != null : "SKUTaskList failed to initialize.";

        LOGGER.log(Level.INFO, "Instantiated new SKU object: [" + this.skuID + "] at " + this.skuLocation);
    }

    public String getSKUID() {
        return skuID;
    }

    public Location getSKULocation() {
        return skuLocation;
    }

    public void setLocation(Location location) {
        if (location == null) {
            LOGGER.log(Level.SEVERE, "Failed to update location for SKU [" + this.skuID + "]: Location is null.");
            throw new IllegalArgumentException("Internal Error: Cannot set SKU Location to null");
        }

        if (this.skuLocation == location) {
            return;
        }

        Location oldLocation = this.skuLocation;
        this.skuLocation = location;

        LOGGER.log(Level.INFO, "Moved SKU [" + skuID + "] from " + oldLocation + " to " + location);
    }

    public SKUTaskList getSKUTaskList() {
        return skuTaskList;
    }
}
