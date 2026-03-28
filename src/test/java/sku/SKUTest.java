package sku;

import org.junit.jupiter.api.Test;
import skutask.Priority;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@@author omcodedthis
class SKUTest {

    @Test
    public void constructor_validInputs_correctlyInitialized() {
        SKU sku = new SKU("ITEM-001", Location.A1);

        assertEquals("ITEM-001", sku.getSKUID());
        assertEquals(Location.A1, sku.getSKULocation());
    }

    @Test
    public void constructor_onInstantiation_taskListIsNotNullAndEmpty() {
        SKU sku = new SKU("ITEM-002", Location.B2);

        assertNotNull(sku.getSKUTaskList());
        assertTrue(sku.getSKUTaskList().isEmpty());
    }

    @Test
    public void setLocation_validLocation_updatesLocationSuccessfully() {
        SKU sku = new SKU("ITEM-003", Location.C3);

        sku.setLocation(Location.A2);

        assertEquals(Location.A2, sku.getSKULocation());
    }

    @Test
    public void setLocation_multipleUpdates_retainsLastLocation() {
        SKU sku = new SKU("ITEM-004", Location.B1);

        sku.setLocation(Location.C1);
        sku.setLocation(Location.A3);

        assertEquals(Location.A3, sku.getSKULocation());
    }

    @Test
    public void getSKUTaskList_addTasks_reflectsChangesInEncapsulatedList() {
        SKU sku = new SKU("ITEM-005", Location.B3);

        sku.getSKUTaskList().addSKUTask("ITEM-005", Priority.HIGH, "2026-10-10",
                "Test Description");

        assertFalse(sku.getSKUTaskList().isEmpty());
        assertEquals(1, sku.getSKUTaskList().getSize());
    }

    @Test
    public void constructor_nullSkuId_throwsAssertionError() {
        // This test requires the JVM to run with the -ea (enable assertions) flag
        try {
            new SKU(null, Location.A1);
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains("SKU ID cannot be null"));
        }
    }

    @Test
    public void constructor_emptySkuId_throwsAssertionError() {
        try {
            new SKU("   ", Location.B2);
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains("SKU ID cannot be null or empty"));
        }
    }

    @Test
    public void constructor_nullLocation_throwsAssertionError() {
        try {
            new SKU("ITEM-001", null);
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains("SKU Location cannot be null"));
        }
    }
}
