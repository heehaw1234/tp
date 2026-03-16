package sku;

import skutask.SKUTaskList;

/**
 * Represents a Stock Keeping Unit (SKU) in the inventory ticketing system.
 * Contains the SKU identifier, its physical location in the warehouse, and a list of tasks assigned to it.
 */
public class SKU {
    private String skuID;
    private Location skuLocation;
    private SKUTaskList skuTaskList;

    /**
     * Initializes a new SKU with the specified ID and location.
     * An empty task list is automatically created for the SKU upon instantiation.
     *
     * @param skuID The unique alphanumeric identifier for the SKU.
     * @param skuLocation The physical sector location of the SKU in the warehouse.
     */
    public SKU(String skuID, Location skuLocation) {
        this.skuID = skuID;
        this.skuLocation = skuLocation;
        this.skuTaskList = new SKUTaskList();

        assert this.skuID != null && !this.skuID.trim().isEmpty() : "SKU ID cannot be null or empty";
        assert this.skuLocation != null : "SKU Location cannot be null";
    }

    /**
     * Retrieves the identifier of the SKU.
     *
     * @return The SKU's unique ID.
     */
    public String getSKUID() {
        return skuID;
    }

    /**
     * Retrieves the physical warehouse location of the SKU.
     *
     * @return The Location enum representing the SKU's placement.
     */
    public Location getSKULocation() {
        return skuLocation;
    }

    /**
     * Retrieves the list of tasks assigned to this SKU.
     *
     * @return The SKUTaskList containing all tasks associated with the SKU.
     */
    public SKUTaskList getSKUTaskList() {
        return skuTaskList;
    }
}