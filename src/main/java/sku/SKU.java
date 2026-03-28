package sku;

import skutask.SKUTaskList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a Stock Keeping Unit (SKU) in the inventory ticketing system.
 */
public class SKU {
    private static final Logger LOGGER = Logger.getLogger(SKU.class.getName());

    private String skuID;
    private Location skuLocation;
    private SKUTaskList skuTaskList;

    public SKU(String skuID, Location skuLocation) {
        assert skuID != null && !skuID.trim().isEmpty() : "Internal Error: SKU ID cannot be null or empty";
        assert skuLocation != null : "Internal Error: SKU Location cannot be null";

        this.skuID = skuID;
        this.skuLocation = skuLocation;
        this.skuTaskList = new SKUTaskList();

        LOGGER.log(Level.INFO, "Instantiated new SKU object: [" + skuID + "] at " + skuLocation);
    }

    public String getSKUID() {
        return skuID;
    }

    public Location getSKULocation() {
        return skuLocation;
    }

    public void setLocation(Location location) {
        assert location != null : "Internal Error: Cannot set SKU Location to null";

        Location oldLocation = this.skuLocation;
        this.skuLocation = location;

        LOGGER.log(Level.INFO, "Moved SKU [" + skuID + "] from " + oldLocation + " to " + location);
    }

    public SKUTaskList getSKUTaskList() {
        return skuTaskList;
    }
}
