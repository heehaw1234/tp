package command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

//@@author dorndorn54
public class ParsedCommandTest {

    private ParsedCommand command;

    @BeforeEach
    public void setUp() {
        Map<String, String> args = new HashMap<>();
        args.put("n", "PALLET-A");
        args.put("p", "HIGH");
        args.put("d", "2026-04-01");
        command = new ParsedCommand("addskutask", args);
    }

    // -------------------------------------------------------------------------
    // getCommandWord
    // -------------------------------------------------------------------------

    @Test
    public void getCommandWord_storedWord_returnsCorrectWord() {
        assertEquals("addskutask", command.getCommandWord());
    }

    @Test
    public void getCommandWord_emptyCommandWord_returnsEmptyString() {
        ParsedCommand empty = new ParsedCommand("", new HashMap<>());
        assertEquals("", empty.getCommandWord());
    }

    @Test
    public void getCommandWord_differentCommand_returnsCorrectWord() {
        ParsedCommand other = new ParsedCommand("deletesku", new HashMap<>());
        assertEquals("deletesku", other.getCommandWord());
    }

    // -------------------------------------------------------------------------
    // getArg
    // -------------------------------------------------------------------------

    @Test
    public void getArg_presentKey_returnsValue() {
        assertEquals("PALLET-A", command.getArg("n"));
    }

    @Test
    public void getArg_absentKey_returnsNull() {
        assertNull(command.getArg("z"));
    }

    @Test
    public void getArg_uppercaseKey_isCaseInsensitive() {
        assertEquals("PALLET-A", command.getArg("N"));
    }

    @Test
    public void getArg_mixedCaseKey_isCaseInsensitive() {
        assertEquals("HIGH", command.getArg("P"));
    }

    @Test
    public void getArg_emptyStringKey_returnsNull() {
        assertNull(command.getArg(""));
    }

    // -------------------------------------------------------------------------
    // hasArg
    // -------------------------------------------------------------------------

    @Test
    public void hasArg_presentKey_returnsTrue() {
        assertTrue(command.hasArg("n"));
    }

    @Test
    public void hasArg_absentKey_returnsFalse() {
        assertFalse(command.hasArg("z"));
    }

    @Test
    public void hasArg_uppercaseKey_isCaseInsensitive() {
        assertTrue(command.hasArg("N"));
    }

    @Test
    public void hasArg_mixedCaseKey_isCaseInsensitive() {
        assertTrue(command.hasArg("D"));
    }

    @Test
    public void hasArg_emptyArgs_returnsFalse() {
        ParsedCommand noArgs = new ParsedCommand("listtasks", new HashMap<>());
        assertFalse(noArgs.hasArg("n"));
    }

    // -------------------------------------------------------------------------
    // combined / edge cases
    // -------------------------------------------------------------------------

    @Test
    public void getArg_nullValueStoredForKey_returnsNull() {
        Map<String, String> args = new HashMap<>();
        args.put("t", null);
        ParsedCommand cmd = new ParsedCommand("addskutask", args);
        assertNull(cmd.getArg("t"));
    }

    @Test
    public void hasArg_nullValueStoredForKey_returnsTrue() {
        Map<String, String> args = new HashMap<>();
        args.put("t", null);
        ParsedCommand cmd = new ParsedCommand("addskutask", args);
        assertTrue(cmd.hasArg("t"));
    }

    @Test
    public void getArg_multipleKeys_eachKeyReturnsCorrectValue() {
        assertEquals("PALLET-A", command.getArg("n"));
        assertEquals("HIGH", command.getArg("p"));
        assertEquals("2026-04-01", command.getArg("d"));
    }
}
