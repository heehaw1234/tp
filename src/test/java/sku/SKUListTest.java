package sku;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        assertEquals("PALLET-A", addedSku.getSKUID());
        assertEquals(Location.A1, addedSku.getSKULocation());
        // Verify that the task list was properly initialized by the SKU constructor
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
    public void deleteSKU_existingSKU_removesCorrectly() {
        skuList.addSKU("PALLET-A", Location.A1);
        skuList.addSKU("PALLET-B", Location.B2);

        skuList.deleteSKU("PALLET-A");

        assertEquals(1, skuList.getSize());
        assertEquals("PALLET-B", skuList.getSKUList().get(0).getSKUID());
    }

    @Test
    public void deleteSKU_middleSKU_shiftsRemainingElements() {
        skuList.addSKU("PALLET-A", Location.A1);
        skuList.addSKU("PALLET-B", Location.B2);
        skuList.addSKU("PALLET-C", Location.C3);

        skuList.deleteSKU("PALLET-B");

        assertEquals(2, skuList.getSize());
        // Verify the remaining SKUs and their shifted array indices
        assertEquals("PALLET-A", skuList.getSKUList().get(0).getSKUID());
        assertEquals("PALLET-C", skuList.getSKUList().get(1).getSKUID());
    }

    @Test
    public void deleteSKU_nonExistingSKU_listUnchanged() {
        skuList.addSKU("PALLET-A", Location.A1);
        skuList.deleteSKU("GHOST-SKU");

        assertEquals(1, skuList.getSize());
        assertEquals("PALLET-A", skuList.getSKUList().get(0).getSKUID());
    }

    @Test
    public void deleteSKU_emptyList_doesNotThrowException() {
        assertDoesNotThrow(() -> skuList.deleteSKU("PALLET-A"));
        assertEquals(0, skuList.getSize());
    }

    @Test
    public void deleteSKU_caseSensitive_doesNotRemoveIfCaseMismatches() {
        skuList.addSKU("PALLET-A", Location.A1);

        skuList.deleteSKU("pallet-a");

        assertEquals(1, skuList.getSize());
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
        for (int i = 0; i < 10000; i++) {
            skuList.addSKU("BULK-ITEM-" + i, Location.C1);
        }
        assertEquals(10000, skuList.getSize());
        assertEquals("BULK-ITEM-9999", skuList.getSKUList().get(9999).getSKUID());
    }

    @Test
    public void getSKUList_modifyElement_reflectsInList() {
        skuList.addSKU("PALLET-A", Location.A1);
        SKU fetchedSku = skuList.getSKUList().get(0);

        fetchedSku.setLocation(Location.C3);

        assertEquals(Location.C3, skuList.getSKUList().get(0).getSKULocation());
    }
}
