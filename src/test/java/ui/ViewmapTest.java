package ui;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import sku.Location;
import sku.SKU;
import sku.SKUList;
import skutask.Priority;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

//@@author SeanTLY23
public class ViewmapTest {
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private ViewMap viewMap;
    private SKUList skuList;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        viewMap = new ViewMap();
        skuList = new SKUList();
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void printTaskMap_emptyList_displaysAllZeros() {
        viewMap.printTaskMap(skuList);

        String output = outputStreamCaptor.toString();

        assertTrue(output.contains("--- Warehouse Task Distribution (A1-C3) ---"));
        assertTrue(output.contains("-------------------------------------------"));

        assertTrue(output.contains("[A1: 00]"));
        assertTrue(output.contains("[B2: 00]"));
        assertTrue(output.contains("[C3: 00]"));
    }

    @Test
    public void printTaskMap_multipleSkusAtSameLocation_aggregatesCorrectly() {
        skuList.addSKU("SKU_001", Location.A1);
        skuList.addSKU("SKU_002", Location.A1);

        SKU sku1 = skuList.findByID("SKU_001");
        SKU sku2 = skuList.findByID("SKU_002");

        sku1.getSKUTaskList().addSKUTask("SKU_001", Priority.HIGH, "2026-12-01", "Task A");
        sku2.getSKUTaskList().addSKUTask("SKU_002", Priority.LOW, "2026-12-05", "Task B");
        sku2.getSKUTaskList().addSKUTask("SKU_002", Priority.MEDIUM, "2026-12-06", "Task C");

        viewMap.printTaskMap(skuList);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("[A1: 03]"));
    }

    @Test
    public void printTaskMap_skusAtDifferentLocations_rendersCorrectSlots() {
        skuList.addSKU("SKU_A1", Location.A1);
        skuList.addSKU("SKU_C3", Location.C3);

        SKU skuTop = skuList.findByID("SKU_A1");
        SKU skuBottom = skuList.findByID("SKU_C3");

        skuTop.getSKUTaskList().addSKUTask("SKU_A1", "2026-01-01");
        skuBottom.getSKUTaskList().addSKUTask("SKU_C3", "2026-01-01");

        viewMap.printTaskMap(skuList);

        String output = outputStreamCaptor.toString();

        assertTrue(output.contains("[A1: 01]"), "A1 should show 01 task.");
        assertTrue(output.contains("[C3: 01]"), "C3 should show 01 task.");
        assertTrue(output.contains("[B2: 00]"), "B2 should show 00 as it has no SKUs.");
    }

    @Test
    public void printTaskMap_nullList_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> {
            viewMap.printTaskMap(null);
        });
    }
}
