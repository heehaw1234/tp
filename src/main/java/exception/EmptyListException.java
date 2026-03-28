package exception;

/**
 * Represents an error when an operation requires items but the target list is empty.
 */
//@@author omcodedthis
public class EmptyListException extends ItemTaskerException {

    /**
     * Constructs an EmptyListException specifying which list is empty.
     *
     * @param listType A description of the empty list (e.g., "warehouse SKU list" or "task list").
     */
    public EmptyListException(String listType) {
        super("The " + listType + " is empty. Please add items first.");
    }
}
