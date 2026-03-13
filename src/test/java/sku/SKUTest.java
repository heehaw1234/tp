package sku;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertNotNull(sku.getSKUTaskList(), "Task list should be initialized upon SKU creation.");
        assertTrue(sku.getSKUTaskList().isEmpty(), "Newly created SKU should have an empty task list.");
    }
}