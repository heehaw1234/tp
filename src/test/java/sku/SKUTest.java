package sku;

import org.junit.jupiter.api.Test;
import skutask.Priority;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void constructor_lowercaseId_convertsToUppercase() {
        SKU sku = new SKU("item-abc", Location.A1);
        assertEquals("ITEM-ABC", sku.getSKUID(), "SKU ID should be normalized to uppercase.");
    }

    @Test
    public void constructor_untrimmedId_trimsWhitespace() {
        SKU sku = new SKU("  item-001  ", Location.A1);
        assertEquals("ITEM-001", sku.getSKUID(), "SKU ID should be trimmed and uppercased.");
    }

    @Test
    public void setLocation_validLocation_updatesLocationSuccessfully() {
        SKU sku = new SKU("ITEM-003", Location.C3);
        sku.setLocation(Location.A2);
        assertEquals(Location.A2, sku.getSKULocation());
    }

    @Test
    public void setLocation_sameLocation_doesNotChangeState() {
        SKU sku = new SKU("ITEM-SAME", Location.B1);
        sku.setLocation(Location.B1);
        assertEquals(Location.B1, sku.getSKULocation());
    }

    @Test
    public void setLocation_allEnumValues_accepted() {
        SKU sku = new SKU("LOC-TEST", Location.A1);
        for (Location loc : Location.values()) {
            sku.setLocation(loc);
            assertEquals(loc, sku.getSKULocation());
        }
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
    public void constructor_nullSkuId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new SKU(null, Location.A1),
                "Constructor should throw exception for null SKU ID.");
    }

    @Test
    public void constructor_emptySkuId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new SKU("", Location.B2),
                "Constructor should throw exception for empty SKU ID.");
    }

    @Test
    public void constructor_whitespaceOnlySkuId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new SKU("   ", Location.B2),
                "Constructor should throw exception for whitespace-only SKU ID.");
    }

    @Test
    public void constructor_nullLocation_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new SKU("ITEM-001", null),
                "Constructor should throw exception for null Location.");
    }

    @Test
    public void setLocation_nullLocation_throwsIllegalArgumentException() {
        SKU sku = new SKU("ITEM-001", Location.A1);
        assertThrows(IllegalArgumentException.class, () -> sku.setLocation(null),
                "setLocation should throw exception for null Location.");
    }
}
