package exception;

/**
 * Represents an error when a required argument (like an index or description) is missing.
 */
//@@author omcodedthis
public class MissingArgumentException extends ItemTaskerException {

    /**
     * Constructs a MissingArgumentException with a specified detail message.
     *
     * @param message The detail message explaining which argument is missing.
     */
    public MissingArgumentException(String message) {
        super(message);
    }
}
