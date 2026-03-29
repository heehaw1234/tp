package ui;

import command.CommandRunner;
import command.ParsedCommand;
import exception.InvalidIndexException;
import exception.ItemTaskerException;
import exception.MissingArgumentException;
import exception.SKUNotFoundException;
import sku.Location;
import sku.SKU;
import sku.SKUList;
import skutask.Priority;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FindCommandTest {

    private CommandRunner runner;
    private SKUList skuList;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        skuList = new SKUList();

        skuList.addSKU("WIDGET-A1", Location.A1);
        SKU widgetA = skuList.getSKUList().get(0);
        widgetA.getSKUTaskList().addSKUTask("WIDGET-A1", Priority.HIGH, "2026-05-01", "restock shelf");
        widgetA.getSKUTaskList().addSKUTask("WIDGET-A1", Priority.MEDIUM, "2026-06-01", "check inventory");

        skuList.addSKU("GADGET-B2", Location.B2);
        SKU gadgetB = skuList.getSKUList().get(1);
        gadgetB.getSKUTaskList().addSKUTask("GADGET-B2", Priority.LOW, "2026-07-01", "restock back");

        runner = new CommandRunner(skuList);

        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    private String getOutput() {
        return outputStream.toString();
    }

    private ParsedCommand buildFindCommand(String nVal, String tVal, String iVal) {
        Map<String, String> args = new HashMap<>();
        if (nVal != null) {
            args.put("n", nVal);
        }
        if (tVal != null) {
            args.put("t", tVal);
        }
        if (iVal != null) {
            args.put("i", iVal);
        }
        return new ParsedCommand("find", args);
    }

    @Test
    public void find_bySkuId_showsMatchingTasks() throws ItemTaskerException, IOException {
        runner.run(buildFindCommand("WIDGET-A1", null, null));
        String output = getOutput();
        assertTrue(output.contains("WIDGET-A1"));
        assertTrue(output.contains("restock shelf"));
        assertTrue(output.contains("check inventory"));
        assertFalse(output.contains("GADGET-B2"));
    }

    @Test
    public void find_byDescription_showsMatchingTasks() throws ItemTaskerException, IOException {
        runner.run(buildFindCommand(null, "restock", null));
        String output = getOutput();
        assertTrue(output.contains("restock shelf"));
        assertTrue(output.contains("restock back"));
        assertFalse(output.contains("check inventory"));
    }

    @Test
    public void find_byIndex_showsTaskAtIndex() throws ItemTaskerException, IOException {
        runner.run(buildFindCommand("WIDGET-A1", null, "1"));
        String output = getOutput();
        assertTrue(output.contains("restock shelf"));
        assertFalse(output.contains("check inventory"));
    }

    @Test
    public void find_bySkuAndDescription_narrowsResults() throws ItemTaskerException, IOException {
        runner.run(buildFindCommand("WIDGET-A1", "check", null));
        String output = getOutput();
        assertTrue(output.contains("check inventory"));
        assertFalse(output.contains("restock shelf"));
    }

    @Test
    public void find_noMatchingDescription_showsNoResults() throws ItemTaskerException, IOException {
        runner.run(buildFindCommand(null, "nonexistent", null));
        String output = getOutput();
        assertTrue(output.contains("No matching tasks found"));
    }

    @Test
    public void find_descriptionCaseInsensitive_matchesRegardlessOfCase() throws ItemTaskerException, IOException {
        runner.run(buildFindCommand(null, "RESTOCK", null));
        String output = getOutput();
        assertTrue(output.contains("restock shelf"));
        assertTrue(output.contains("restock back"));
    }

    @Test
    public void find_allThreeFilters_narrowsToExactTask() throws ItemTaskerException, IOException {
        runner.run(buildFindCommand("WIDGET-A1", "restock", "1"));
        String output = getOutput();
        assertTrue(output.contains("restock shelf"));
        assertFalse(output.contains("check inventory"));
        assertFalse(output.contains("GADGET-B2"));
    }

    @Test
    public void find_noFilters_throwsMissingArgumentException() {
        assertThrows(MissingArgumentException.class, () -> {
            runner.run(buildFindCommand(null, null, null));
        });
    }

    @Test
    public void find_nonExistentSku_throwsSKUNotFoundException() {
        assertThrows(SKUNotFoundException.class, () -> {
            runner.run(buildFindCommand("GHOST-SKU", null, null));
        });
    }

    @Test
    public void find_indexOutOfRange_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () -> {
            runner.run(buildFindCommand("WIDGET-A1", null, "99"));
        });
    }

    @Test
    public void find_byIndexOnly_showsTaskAtIndexAcrossAllSKUs() throws ItemTaskerException, IOException {
        runner.run(buildFindCommand(null, null, "1"));
        String output = getOutput();
        assertTrue(output.contains("restock shelf"));
        assertTrue(output.contains("restock back"));
    }

    @Test
    public void find_byDescAndIndex_narrowsResults() throws ItemTaskerException, IOException {
        runner.run(buildFindCommand(null, "restock", "1"));
        String output = getOutput();
        assertTrue(output.contains("restock shelf"));
        assertTrue(output.contains("restock back"));
        assertFalse(output.contains("check inventory"));
    }
}
