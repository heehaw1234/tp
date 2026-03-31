package exception;

/**
 * Represents an error when a filter criteria provided by the user is
 * syntactically incorrect or contains invalid parameters.
 */
public class InvalidFilterException extends ItemTaskerException {
    /**
     * Constructs an InvalidFilterException with a specific error message.
     *
     * @param message A detailed description of why the filter is invalid.
     */
    public InvalidFilterException(String message) {
        super(message);
    }
}
