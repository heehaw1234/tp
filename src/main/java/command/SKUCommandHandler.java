package command;

import exception.MissingArgumentException;
import exception.SKUNotFoundException;

import sku.Location;
import sku.SKU;
import sku.SKUList;
import ui.Ui;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles all SKU-level commands: adding, editing, and deleting SKUs.
 * Each public method corresponds to a single user command.
 */

//@@author omcodedthis
public class SKUCommandHandler {
    private static final Logger LOGGER = Logger.getLogger(SKUCommandHandler.class.getName());
    private final SKUList skuList;

    public SKUCommandHandler(SKUList skuList) {
        if (skuList == null) {
            throw new IllegalArgumentException("SKUCommandHandler requires a non-null SKUList");
        }
        this.skuList = skuList;
    }

    /**
     * Adds a new SKU to the warehouse after validating the ID and location.
     *
     * @param cmd The parsed command containing the SKU ID and location.
     * @throws MissingArgumentException If required arguments are missing or empty.
     */
    public void handleAddSku(ParsedCommand cmd) throws MissingArgumentException {
        assert cmd != null : "Internal Error: ParsedCommand cannot be null";

        String skuId = cmd.getArg("n");
        String locationStr = cmd.getArg("l");

        if (skuId == null || skuId.trim().isEmpty() || locationStr == null || locationStr.trim().isEmpty()) {
            throw new MissingArgumentException("Usage: addsku n/SKU_ID l/LOCATION  (e.g. addsku n/WIDGET-A1 l/B2)");
        }

        Location location = CommandHelper.parseLocation(locationStr);
        if (location == null) {
            return;
        }

        if (skuList.findByID(skuId) != null) {
            Ui.printError("SKU already exists: " + skuId.toUpperCase());
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
    //@@author AkshayPranav19
    public void handleEditSku(ParsedCommand cmd) throws SKUNotFoundException {
        assert cmd != null : "Internal Error: ParsedCommand cannot be null";

        String skuId = cmd.getArg("n");
        String locationStr = cmd.getArg("l");

        if (skuId == null || locationStr == null) {
            LOGGER.log(Level.WARNING, "editsku missing args: skuId={0}, loc={1}",
                    new Object[]{skuId, locationStr});
            Ui.printError("Usage: editsku n/SKU_ID l/NEW_LOCATION");
            return;
        }

        LOGGER.log(Level.INFO, "Attempting to edit SKU [{0}] to location [{1}]",
                new Object[]{skuId, locationStr});

        SKU targetSku = CommandHelper.findSkuOrError(skuList, skuId);
        if (targetSku == null) {
            LOGGER.log(Level.WARNING, "editsku failed: SKU [{0}] not found", skuId);
            return;
        }

        Location newLocation = CommandHelper.parseLocation(locationStr);
        if (newLocation == null) {
            LOGGER.log(Level.WARNING, "editsku failed: invalid location [{0}]", locationStr);
            return;
        }

        targetSku.setLocation(newLocation);
        LOGGER.log(Level.INFO, "SKU [{0}] successfully moved to {1}",
                new Object[]{skuId, newLocation});
        Ui.printSuccess("Updated location of SKU [" + skuId.toUpperCase() + "] to " + newLocation + ".");
    }

    /**
     * Deletes an existing SKU and all its tasks from the warehouse.
     *
     * @param cmd The parsed command containing the SKU ID to delete.
     * @throws MissingArgumentException If the SKU ID is not provided.
     * @throws SKUNotFoundException     If the specified SKU does not exist in the warehouse.
     */
    //@@author omcodedthis
    public void handleDeleteSku(ParsedCommand cmd) throws MissingArgumentException, SKUNotFoundException {
        assert cmd != null : "Internal Error: ParsedCommand cannot be null";

        String skuId = cmd.getArg("n");

        if (skuId == null || skuId.trim().isEmpty()) {
            throw new MissingArgumentException("Usage: deletesku n/SKU_ID");
        }

        if (skuList.findByID(skuId) == null) {
            throw new SKUNotFoundException(skuId);
        }

        skuList.deleteSKU(skuId);
        Ui.printSuccess("Deleted SKU [" + skuId.toUpperCase() + "] and all its tasks.");
    }
}
