package exception;

/**
 * Represents the base exception for all ItemTasker-specific errors.
 * All custom exceptions in the application should inherit from this class.
 */
//@@author omcodedthis
public class ItemTaskerException extends Exception {

    /**
     * Constructs an ItemTaskerException with the specified detail message.
     *
     * @param message The detail message describing the error.
     */
    public ItemTaskerException(String message) {
        super(message);
    }

    /**
     * Constructs an ItemTaskerException with the specified detail message and cause.
     *
     * @param message The detail message describing the error.
     * @param cause The underlying cause of the exception.
     */
    public ItemTaskerException(String message, Throwable cause) {
        super(message, cause);
    }
}
