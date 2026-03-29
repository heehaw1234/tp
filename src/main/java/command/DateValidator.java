package command;

import ui.Ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Validates date strings in YYYY-MM-DD format.
 * Rejects invalid formats, impossible calendar dates, and null/empty input.
 *
 * <p>Follows the same validate-and-return pattern as
 * {@link CommandHelper#parseLocation(String)} — returns null on failure
 * and prints an error via {@link Ui#printError(String)}.</p>
 */
//@@author AkshayPranav19
public class DateValidator {
    private static final Logger LOGGER = Logger.getLogger(DateValidator.class.getName());
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    /**
     * Checks whether the given string is a valid date in YYYY-MM-DD format.
     *
     * @param dateStr The date string to validate.
     * @return True if the string is a valid calendar date, false otherwise.
     */
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        if (!DATE_PATTERN.matcher(dateStr.trim()).matches()) {
            return false;
        }
        try {
            LocalDate.parse(dateStr.trim());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validates a date string and prints an error if invalid.
     * Returns the trimmed date on success, or null on failure.
     *
     * @param dateStr The raw date string from user input.
     * @return The validated and trimmed date string, or null if invalid.
     */
    public static String validateDateOrError(String dateStr) {
        assert dateStr != null : "Date string should not be null at this point";

        String trimmed = dateStr.trim();

        if (!DATE_PATTERN.matcher(trimmed).matches()) {
            LOGGER.log(Level.WARNING, "Date format rejected: " + dateStr);
            Ui.printError("Invalid date format '" + dateStr + "'. Use YYYY-MM-DD (e.g. 2026-03-29).");
            return null;
        }

        try {
            LocalDate.parse(trimmed);
            LOGGER.log(Level.FINE, "Date validated successfully: " + trimmed);
            return trimmed;
        } catch (DateTimeParseException e) {
            LOGGER.log(Level.WARNING, "Impossible calendar date: " + dateStr, e);
            Ui.printError("Invalid date '" + dateStr + "'. Date does not exist on the calendar.");
            return null;
        }
    }
}
