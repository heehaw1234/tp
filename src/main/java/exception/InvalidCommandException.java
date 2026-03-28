package exception;

/**
 * Represents an error when a user command is unrecognized, malformed, or has extra arguments.
 */
//@@author omcodedthis
public class InvalidCommandException extends ItemTaskerException {

    /**
     * Constructs an InvalidCommandException with a specific explanation.
     *
     * @param message The detail message explaining the syntax error or unknown command.
     */
    public InvalidCommandException(String message) {
        super(message);
    }
}
