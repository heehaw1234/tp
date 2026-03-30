package sku;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

//@@author omcodedthis
class SKUListTest {
    private SKUList skuList;

    @BeforeEach
    public void setUp() {
        skuList = new SKUList();
    }

    @Test
    public void constructor_newSKUList_isEmptyAndSizeZero() {
        assertTrue(skuList.isEmpty());
        assertEquals(0, skuList.getSize());
        assertNotNull(skuList.getSKUList());
    }

    @Test
    public void addSKU_singleSKU_updatesSizeAndProperties() {
        skuList.addSKU("PALLET-A", Location.A1);

        assertFalse(skuList.isEmpty());
        assertEquals(1, skuList.getSize());

        SKU addedSku = skuList.getSKUList().get(0);
        // Note: Expecting normalized ID if normalization was implemented in SKU constructor
        assertEquals("PALLET-A", addedSku.getSKUID());
        assertEquals(Location.A1, addedSku.getSKULocation());
        assertNotNull(addedSku.getSKUTaskList());
    }

    @Test
    public void addSKU_multipleSKUs_maintainsInsertionOrder() {
        skuList.addSKU("PALLET-A", Location.A1);
        skuList.addSKU("PALLET-B", Location.B2);
        skuList.addSKU("PALLET-C", Location.C3);

        assertEquals(3, skuList.getSize());
        assertEquals("PALLET-A", skuList.getSKUList().get(0).getSKUID());
        assertEquals("PALLET-B", skuList.getSKUList().get(1).getSKUID());
        assertEquals("PALLET-C", skuList.getSKUList().get(2).getSKUID());
    }

    @Test
    public void addSKU_duplicateId_throwsException() {
        skuList.addSKU("PALLET-A", Location.A1);
        assertThrows(IllegalArgumentException.class, () -> skuList.addSKU("PALLET-A", Location.B2));
    }

    @Test
    public void findByID_existingSku_returnsCorrectSku() {
        skuList.addSKU("WIDGET-X", Location.A1);
        SKU found = skuList.findByID("WIDGET-X");
        assertNotNull(found);
        assertEquals("WIDGET-X", found.getSKUID());
    }

    @Test
    public void findByID_caseInsensitive_returnsCorrectSku() {
        skuList.addSKU("WIDGET-X", Location.A1);
        SKU found = skuList.findByID("widget-x");
        assertNotNull(found, "Should find SKU regardless of case input.");
        assertEquals("WIDGET-X", found.getSKUID());
    }

    @Test
    public void findByID_nonExistentSku_returnsNull() {
        skuList.addSKU("WIDGET-X", Location.A1);
        assertNull(skuList.findByID("GHOST-SKU"));
    }

    @Test
    public void deleteSKU_existingSKU_removesCorrectly() {
        skuList.addSKU("PALLET-A", Location.A1);
        skuList.addSKU("PALLET-B", Location.B2);

        skuList.deleteSKU("PALLET-A");

        assertEquals(1, skuList.getSize());
        assertEquals("PALLET-B", skuList.getSKUList().get(0).getSKUID());
    }

    @Test
    public void deleteSKU_caseInsensitive_removesCorrectly() {
        skuList.addSKU("PALLET-A", Location.A1);
        // Verification of the bug fix: delete should be case-insensitive
        skuList.deleteSKU("pallet-a");
        assertTrue(skuList.isEmpty(), "SKU should be deleted even if case differs.");
    }

    @Test
    public void deleteSKU_middleSKU_shiftsRemainingElements() {
        skuList.addSKU("PALLET-A", Location.A1);
        skuList.addSKU("PALLET-B", Location.B2);
        skuList.addSKU("PALLET-C", Location.C3);

        skuList.deleteSKU("PALLET-B");

        assertEquals(2, skuList.getSize());
        assertEquals("PALLET-A", skuList.getSKUList().get(0).getSKUID());
        assertEquals("PALLET-C", skuList.getSKUList().get(1).getSKUID());
    }

    @Test
    public void deleteSKU_emptyList_doesNotThrowException() {
        assertDoesNotThrow(() -> skuList.deleteSKU("PALLET-A"));
        assertEquals(0, skuList.getSize());
    }

    @Test
    public void getSKUList_returnsModifiableList() {
        skuList.addSKU("PALLET-A", Location.A1);
        ArrayList<SKU> list = skuList.getSKUList();
        assertEquals(1, list.size());

        list.clear();
        assertTrue(skuList.isEmpty());
    }

    @Test
    public void addSKU_stressTest_handlesLargeVolume() {
        for (int i = 0; i < 1000; i++) {
            skuList.addSKU("ITEM-" + i, Location.C1);
        }
        assertEquals(1000, skuList.getSize());
        assertEquals("ITEM-999", skuList.getSKUList().get(999).getSKUID());
    }

    @Test
    public void addSKU_nullOrEmptyParams_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> skuList.addSKU(null, Location.A1));
        assertThrows(IllegalArgumentException.class, () -> skuList.addSKU("", Location.A1));
        assertThrows(IllegalArgumentException.class, () -> skuList.addSKU("TEST", null));
    }
}
