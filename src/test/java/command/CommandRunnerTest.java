package command;

import exception.InvalidIndexException;
import exception.ItemTaskerException;
import exception.MissingArgumentException;
import exception.SKUNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sku.Location;
import sku.SKU;
import sku.SKUList;
import skutask.Priority;
import skutask.SKUTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration-level tests for {@link CommandRunner}.
 *
 * Each test group exercises one command word end-to-end by building a
 * {@link ParsedCommand} and calling {@link CommandRunner#run}.  Output is
 * captured from {@code System.out} so we can assert on success/error messages
 * without requiring a real terminal.
 */

public class CommandRunnerTest {

    private CommandRunner runner;
    private SKUList skuList;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    // -----------------------------------------------------------------------
    // Fixtures
    // -----------------------------------------------------------------------

    @BeforeEach
    public void setUp() {
        skuList = new SKUList();
        // Seed a base SKU + one task used by most tests
        skuList.addSKU("WIDGET-A1", Location.A1);
        SKU widget = skuList.getSKUList().get(0);
        widget.getSKUTaskList().addSKUTask("WIDGET-A1", Priority.HIGH, "2026-05-01", "restock shelf");

        runner = new CommandRunner(skuList);

        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    private String output() {
        return outputStream.toString();
    }

    /** Minimal helper to build any ParsedCommand with a key→value varargs map. */
    private ParsedCommand cmd(String word, String... kvPairs) {
        Map<String, String> args = new HashMap<>();
        for (int i = 0; i + 1 < kvPairs.length; i += 2) {
            args.put(kvPairs[i], kvPairs[i + 1]);
        }
        return new ParsedCommand(word, args);
    }

    // -----------------------------------------------------------------------
    // isRunning()
    // -----------------------------------------------------------------------

    @Test
    public void isRunning_initiallyTrue() {
        assertTrue(runner.isRunning());
    }

    @Test
    public void isRunning_afterByeCommand_returnsFalse() throws ItemTaskerException, IOException {
        runner.run(cmd("bye"));
        assertFalse(runner.isRunning());
    }

    @Test
    public void isRunning_afterExitCommand_returnsFalse() throws ItemTaskerException, IOException {
        runner.run(cmd("exit"));
        assertFalse(runner.isRunning());
    }

    // -----------------------------------------------------------------------
    // addsku
    // -----------------------------------------------------------------------

    @Test
    public void addsku_validIdAndLocation_skuAppearsInList() throws ItemTaskerException, IOException {
        runner.run(cmd("addsku", "n", "GADGET-B2", "l", "B2"));
        boolean found = skuList.getSKUList().stream()
                .anyMatch(s -> s.getSKUID().equalsIgnoreCase("GADGET-B2"));
        assertTrue(found);
        assertTrue(output().contains("[OK]"));
    }

    @Test
    public void addsku_missingSkuId_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("addsku", "l", "A1"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void addsku_missingLocation_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("addsku", "n", "NEW-SKU"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void addsku_invalidLocation_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("addsku", "n", "NEW-SKU", "l", "Z9"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void addsku_duplicateSku_printsError() throws ItemTaskerException, IOException {
        // WIDGET-A1 was added in setUp()
        runner.run(cmd("addsku", "n", "WIDGET-A1", "l", "A2"));
        assertTrue(output().contains("[ERROR]"));
    }

    // -----------------------------------------------------------------------
    // deletesku
    // -----------------------------------------------------------------------

    @Test
    public void deletesku_existingSku_skuRemovedFromList() throws ItemTaskerException, IOException {
        runner.run(cmd("deletesku", "n", "WIDGET-A1"));
        boolean found = skuList.getSKUList().stream()
                .anyMatch(s -> s.getSKUID().equalsIgnoreCase("WIDGET-A1"));
        assertFalse(found);
        assertTrue(output().contains("[OK]"));
    }

    @Test
    public void deletesku_missingSkuId_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("deletesku"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void deletesku_nonExistentSku_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("deletesku", "n", "GHOST-SKU"));
        assertTrue(output().contains("[ERROR]"));
    }

    // -----------------------------------------------------------------------
    // editsku
    // -----------------------------------------------------------------------

    //    @Test
    //    public void editsku_validSkuAndLocation_locationUpdated() throws ItemTaskerException, IOException {
    //        runner.run(cmd("editsku", "n", "WIDGET-A1", "l", "C3"));
    //        SKU sku = skuList.getSKUList().get(0);
    //        assertEquals(Location.C3, sku.getLocation());
    //        assertTrue(output().contains("[OK]"));
    //    }

    @Test
    public void editsku_missingArgs_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("editsku", "n", "WIDGET-A1"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void editsku_invalidLocation_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("editsku", "n", "WIDGET-A1", "l", "Z9"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void editsku_nonExistentSku_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("editsku", "n", "GHOST-SKU", "l", "A1"));
        assertTrue(output().contains("[ERROR]"));
    }

    // -----------------------------------------------------------------------
    // addskutask
    // -----------------------------------------------------------------------

    @Test
    public void addskutask_validArgs_taskAddedToSku() throws ItemTaskerException, IOException {
        runner.run(cmd("addskutask", "n", "WIDGET-A1", "d", "2026-12-01",
                "p", "LOW", "t", "annual check"));
        SKU sku = skuList.getSKUList().get(0);
        assertEquals(2, sku.getSKUTaskList().getSize());
        assertTrue(output().contains("[OK]"));
    }

    @Test
    public void addskutask_defaultPriorityHigh_whenPriorityFlagAbsent()
            throws ItemTaskerException, IOException {
        runner.run(cmd("addskutask", "n", "WIDGET-A1", "d", "2026-12-01"));
        SKU sku = skuList.getSKUList().get(0);
        SKUTask newTask = sku.getSKUTaskList().getSKUTaskList().get(1);
        assertEquals(Priority.HIGH, newTask.getSKUTaskPriority());
    }

    @Test
    public void addskutask_missingSkuId_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("addskutask", "d", "2026-12-01"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void addskutask_missingDueDate_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("addskutask", "n", "WIDGET-A1"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void addskutask_nonExistentSku_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("addskutask", "n", "GHOST-SKU", "d", "2026-12-01"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void addskutask_invalidPriority_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("addskutask", "n", "WIDGET-A1", "d", "2026-12-01", "p", "URGENT"));
        assertTrue(output().contains("[ERROR]"));
    }

    // -----------------------------------------------------------------------
    // edittask
    // -----------------------------------------------------------------------

    @Test
    public void edittask_updateDate_dateChanged() throws ItemTaskerException, IOException {
        runner.run(cmd("edittask", "n", "WIDGET-A1", "i", "1", "d", "2027-01-01"));
        SKUTask task = skuList.getSKUList().get(0).getSKUTaskList().getSKUTaskList().get(0);
        assertEquals("2027-01-01", task.getSKUTaskDueDate());
    }

    @Test
    public void edittask_updatePriority_priorityChanged() throws ItemTaskerException, IOException {
        runner.run(cmd("edittask", "n", "WIDGET-A1", "i", "1", "p", "LOW"));
        SKUTask task = skuList.getSKUList().get(0).getSKUTaskList().getSKUTaskList().get(0);
        assertEquals(Priority.LOW, task.getSKUTaskPriority());
    }

    @Test
    public void edittask_updateDescription_descChanged() throws ItemTaskerException, IOException {
        runner.run(cmd("edittask", "n", "WIDGET-A1", "i", "1", "t", "new desc"));
        SKUTask task = skuList.getSKUList().get(0).getSKUTaskList().getSKUTaskList().get(0);
        assertEquals("new desc", task.getSKUTaskDescription());
    }

    @Test
    public void edittask_noFieldsProvided_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("edittask", "n", "WIDGET-A1", "i", "1"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void edittask_missingSkuId_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("edittask", "i", "1", "d", "2027-01-01"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void edittask_invalidIndex_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () ->
                runner.run(cmd("edittask", "n", "WIDGET-A1", "i", "99", "d", "2027-01-01")));
    }

    @Test
    public void edittask_nonNumericIndex_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("edittask", "n", "WIDGET-A1", "i", "abc", "d", "2027-01-01"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void edittask_invalidPriority_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("edittask", "n", "WIDGET-A1", "i", "1", "p", "URGENT"));
        assertTrue(output().contains("[ERROR]"));
    }

    // -----------------------------------------------------------------------
    // deletetask
    // -----------------------------------------------------------------------

    @Test
    public void deletetask_validIndex_taskRemoved() throws ItemTaskerException, IOException {
        runner.run(cmd("deletetask", "n", "WIDGET-A1", "i", "1"));
        assertEquals(0, skuList.getSKUList().get(0).getSKUTaskList().getSize());
        assertTrue(output().contains("[OK]"));
    }

    @Test
    public void deletetask_missingSkuId_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("deletetask", "i", "1"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void deletetask_missingIndex_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("deletetask", "n", "WIDGET-A1"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void deletetask_outOfRangeIndex_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("deletetask", "n", "WIDGET-A1", "i", "99"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void deletetask_nonExistentSku_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("deletetask", "n", "GHOST-SKU", "i", "1"));
        assertTrue(output().contains("[ERROR]"));
    }

    // -----------------------------------------------------------------------
    // marktask / unmarktask
    // -----------------------------------------------------------------------

    @Test
    public void marktask_validIndex_taskMarkedDone() throws ItemTaskerException, IOException {
        runner.run(cmd("marktask", "n", "WIDGET-A1", "i", "1"));
        SKUTask task = skuList.getSKUList().get(0).getSKUTaskList().getSKUTaskList().get(0);
        assertTrue(task.isDone());
        assertTrue(output().contains("[OK]"));
    }

    @Test
    public void marktask_alreadyDone_printsInfo() throws ItemTaskerException, IOException {
        runner.run(cmd("marktask", "n", "WIDGET-A1", "i", "1")); // first mark
        outputStream.reset();
        runner.run(cmd("marktask", "n", "WIDGET-A1", "i", "1")); // second mark
        assertTrue(output().contains("already marked"));
    }

    @Test
    public void marktask_invalidIndex_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () ->
                runner.run(cmd("marktask", "n", "WIDGET-A1", "i", "99")));
    }

    @Test
    public void marktask_missingArgs_printsError() throws ItemTaskerException, IOException {
        runner.run(cmd("marktask"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void unmarktask_markedTask_taskUnmarked() throws ItemTaskerException, IOException {
        runner.run(cmd("marktask", "n", "WIDGET-A1", "i", "1"));
        outputStream.reset();
        runner.run(cmd("unmarktask", "n", "WIDGET-A1", "i", "1"));
        SKUTask task = skuList.getSKUList().get(0).getSKUTaskList().getSKUTaskList().get(0);
        assertFalse(task.isDone());
        assertTrue(output().contains("[OK]"));
    }

    @Test
    public void unmarktask_alreadyUnmarked_printsInfo() throws ItemTaskerException, IOException {
        runner.run(cmd("unmarktask", "n", "WIDGET-A1", "i", "1"));
        assertTrue(output().contains("already unmarked"));
    }

    @Test
    public void unmarktask_invalidIndex_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () ->
                runner.run(cmd("unmarktask", "n", "WIDGET-A1", "i", "99")));
    }

    // -----------------------------------------------------------------------
    // listtasks
    // -----------------------------------------------------------------------

    @Test
    public void listtasks_noFilter_printsAllSkus() throws ItemTaskerException, IOException {
        runner.run(cmd("listtasks"));
        assertTrue(output().contains("WIDGET-A1"));
    }

    @Test
    public void listtasks_filterBySkuId_showsOnlyThatSku() throws ItemTaskerException, IOException {
        skuList.addSKU("GADGET-B2", Location.B2);
        runner.run(cmd("listtasks", "n", "WIDGET-A1"));
        assertTrue(output().contains("WIDGET-A1"));
        assertFalse(output().contains("GADGET-B2"));
    }

    @Test
    public void listtasks_filterByPriority_showsOnlyMatchingPriority()
            throws ItemTaskerException, IOException {
        runner.run(cmd("listtasks", "p", "HIGH"));
        assertTrue(output().contains("HIGH"));
    }

    @Test
    public void listtasks_invalidPriorityFilter_printsError()
            throws ItemTaskerException, IOException {
        runner.run(cmd("listtasks", "p", "URGENT"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void listtasks_filterByLocation_sortsResultsByDistance()
            throws ItemTaskerException, IOException {
        runner.run(cmd("listtasks", "l", "A1"));
        // Output should mention distance header
        assertTrue(output().contains("dist="));
    }

    @Test
    public void listtasks_invalidLocationFilter_printsError()
            throws ItemTaskerException, IOException {
        runner.run(cmd("listtasks", "l", "Z9"));
        assertTrue(output().contains("[ERROR]"));
    }

    @Test
    public void listtasks_skuWithNoTasks_showsNoTasksMessage()
            throws ItemTaskerException, IOException {
        skuList.addSKU("EMPTY-SKU", Location.C1);
        runner.run(cmd("listtasks", "n", "EMPTY-SKU"));
        assertTrue(output().contains("No tasks"));
    }

    // -----------------------------------------------------------------------
    // help
    // -----------------------------------------------------------------------

    @Test
    public void help_command_printsHelpContent() throws ItemTaskerException, IOException {
        runner.run(cmd("help"));
        // Help should print something non-empty
        assertFalse(output().isBlank());
    }

    // -----------------------------------------------------------------------
    // Unknown / empty command
    // -----------------------------------------------------------------------

    @Test
    public void unknownCommand_printsUnknownMessage() throws ItemTaskerException, IOException {
        runner.run(cmd("nonsense"));
        // Ui.printUnknownCommand() should produce some output
        assertFalse(output().isBlank());
    }

    @Test
    public void emptyCommandWord_doesNothing_noExceptionThrown()
            throws ItemTaskerException, IOException {
        runner.run(cmd(""));
        assertTrue(output().isBlank());
    }

    // -----------------------------------------------------------------------
    // find
    // -----------------------------------------------------------------------

    @Test
    public void find_noFilters_throwsMissingArgumentException() {
        assertThrows(MissingArgumentException.class, () -> runner.run(cmd("find")));
    }

    @Test
    public void find_nonExistentSku_throwsSKUNotFoundException() {
        assertThrows(SKUNotFoundException.class, () ->
                runner.run(cmd("find", "n", "GHOST-SKU")));
    }

    @Test
    public void find_byDescription_printsMatchingTask()
            throws ItemTaskerException, IOException {
        runner.run(cmd("find", "t", "restock"));
        assertTrue(output().contains("restock shelf"));
    }

    @Test
    public void find_descriptionCaseInsensitive_matchesRegardlessOfCase()
            throws ItemTaskerException, IOException {
        runner.run(cmd("find", "t", "RESTOCK"));
        assertTrue(output().contains("restock shelf"));
    }

    @Test
    public void find_byIndexOutOfRange_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () ->
                runner.run(cmd("find", "n", "WIDGET-A1", "i", "99")));
    }

    @Test
    public void find_descriptionNotMatching_showsNoResults()
            throws ItemTaskerException, IOException {
        runner.run(cmd("find", "t", "zzz-nonexistent"));
        assertTrue(output().contains("No matching tasks found"));
    }
}
