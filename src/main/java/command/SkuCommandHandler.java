package command;

import exception.MissingArgumentException;
import exception.SKUNotFoundException;

import sku.Location;
import sku.SKU;
import sku.SKUList;
import ui.Ui;

/**
 * Handles all SKU-level commands: adding, editing, and deleting SKUs.
 * Each public method corresponds to a single user command.
 */
public class SkuCommandHandler {

    private final SKUList skuList;

    public SkuCommandHandler(SKUList skuList) {
        this.skuList = skuList;
    }

    /**
     * Adds a new SKU to the warehouse after validating the ID and location.
     *
     * @param cmd The parsed command containing the SKU ID and location.
     * @throws SKUNotFoundException If an issue related to finding the SKU occurs during validation.
     */
    public void handleAddSku(ParsedCommand cmd) throws SKUNotFoundException {
        String skuId = cmd.getArg("n");
        String locationStr = cmd.getArg("l");

        if (skuId == null || locationStr == null) {
            Ui.printError("Usage: addsku n/SKU_ID l/LOCATION  (e.g. addsku n/WIDGET-A1 l/B2)");
            return;
        }

        Location location = CommandHelper.parseLocation(locationStr);
        if (location == null) {
            return;
        }

        if (skuList.findByID(skuId) != null) {
            Ui.printError("SKU already exists: " + skuId);
            return;
        }

        skuList.addSKU(skuId, location);
        Ui.printSuccess("Added SKU [" + skuId.toUpperCase() + "] at location " + location);
    }

    /**
     * Updates the warehouse location of an existing SKU.
     *
     * @param cmd The parsed command containing the SKU ID and new location.
     * @throws SKUNotFoundException If the specified SKU does not exist in the warehouse.
     */
    public void handleEditSku(ParsedCommand cmd) throws SKUNotFoundException {
        String skuId = cmd.getArg("n");
        String locationStr = cmd.getArg("l");

        if (skuId == null || locationStr == null) {
            Ui.printError("Usage: editsku n/SKU_ID l/NEW_LOCATION");
            return;
        }

        SKU targetSku = CommandHelper.findSkuOrError(skuList, skuId);
        if (targetSku == null) {
            return;
        }

        Location newLocation = CommandHelper.parseLocation(locationStr);
        if (newLocation == null) {
            return;
        }

        targetSku.setLocation(newLocation);
        Ui.printSuccess("Updated location of SKU [" + skuId.toUpperCase() + "] to " + newLocation + ".");
    }

    /**
     * Deletes an existing SKU and all its tasks from the warehouse.
     *
     * @param cmd The parsed command containing the SKU ID to delete.
     * @throws MissingArgumentException If the SKU ID is not provided.
     * @throws SKUNotFoundException     If the specified SKU does not exist in the warehouse.
     */
    public void handleDeleteSku(ParsedCommand cmd) throws MissingArgumentException, SKUNotFoundException {
        String skuId = cmd.getArg("n");
        if (skuId == null) {
            Ui.printError("Usage: deletesku n/SKU_ID");
            return;
        }
        if (CommandHelper.findSkuOrError(skuList, skuId) == null) {
            return;
        }

        skuList.deleteSKU(skuId);
        Ui.printSuccess("Deleted SKU [" + skuId.toUpperCase() + "] and all its tasks.");
    }
}
