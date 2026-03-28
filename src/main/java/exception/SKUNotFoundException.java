package exception;

/**
 * Represents an error when a requested SKU cannot be found in the warehouse.
 */
//@@author omcodedthis
public class SKUNotFoundException extends ItemTaskerException {

    /**
     * Constructs an SKUNotFoundException with the missing SKU ID.
     *
     * @param skuId The ID of the SKU that could not be found.
     */
    public SKUNotFoundException(String skuId) {
        super("SKU '" + skuId + "' not found. Use 'listtasks' to see available SKUs.");
    }
}
