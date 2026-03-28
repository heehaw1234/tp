package exception;

/**
 * Represents an error when a provided index is either out of bounds or not a
 * valid integer.
 */
public class InvalidIndexException extends ItemTaskerException {

    /**
     * Constructs an InvalidIndexException when a specific task index is out of
     * range for a particular SKU.
     *
     * @param invalidIndex The index that was out of bounds.
     * @param skuId        The SKU ID associated with the task list.
     */
    public InvalidIndexException(int invalidIndex, String skuId) {
        super("Task index " + invalidIndex + " is out of range for SKU: " + skuId);
    }
}
