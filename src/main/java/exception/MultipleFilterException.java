package exception;

/**
 * Represents an error when multiple conflicting filters are applied
 * to an operation that only supports a single filter criteria.
 */
public class MultipleFilterException extends ItemTaskerException {
    /**
     * Constructs a MultipleFilterException with a specific error message.
     *
     * @param message A description of the filter conflict or violation.
     */
    public MultipleFilterException(String message) {
        super(message);
    }
}
