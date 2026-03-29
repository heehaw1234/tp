package command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@@author AkshayPranav19
public class DateValidatorTest {

    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void isValidDate_validFormat_returnsTrue() {
        assertTrue(DateValidator.isValidDate("2026-03-29"));
        assertTrue(DateValidator.isValidDate("2026-12-31"));
        assertTrue(DateValidator.isValidDate("2026-01-01"));
    }

    @Test
    public void isValidDate_invalidFormat_returnsFalse() {
        assertFalse(DateValidator.isValidDate("29-03-2026"));
        assertFalse(DateValidator.isValidDate("2026/03/29"));
        assertFalse(DateValidator.isValidDate("March 29 2026"));
        assertFalse(DateValidator.isValidDate("20260329"));
    }

    @Test
    public void isValidDate_impossibleDate_returnsFalse() {
        assertFalse(DateValidator.isValidDate("2026-02-30"));
        assertFalse(DateValidator.isValidDate("2026-13-01"));
        assertFalse(DateValidator.isValidDate("2026-04-31"));
    }

    @Test
    public void isValidDate_nullInput_returnsFalse() {
        assertFalse(DateValidator.isValidDate(null));
    }

    @Test
    public void isValidDate_emptyString_returnsFalse() {
        assertFalse(DateValidator.isValidDate(""));
        assertFalse(DateValidator.isValidDate("   "));
    }

    @Test
    public void validateDateOrError_validDate_returnsDate() {
        String result = DateValidator.validateDateOrError("2026-06-15");
        assertNotNull(result);
        assertEquals("2026-06-15", result);
    }

    @Test
    public void validateDateOrError_invalidFormat_returnsNullAndPrintsError() {
        String result = DateValidator.validateDateOrError("not-a-date");
        assertNull(result);
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void validateDateOrError_impossibleDate_returnsNullAndPrintsError() {
        String result = DateValidator.validateDateOrError("2026-02-30");
        assertNull(result);
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }
}
