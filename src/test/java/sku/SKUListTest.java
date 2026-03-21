package sku;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SKUListTest {

    private sku.SKUList skuList;

    @BeforeEach
    public void setUp() {
        // Initialize a fresh list before each test
        skuList = new SKUList();
    }

    @Test
    public void constructor_newSKUList_isEmpty() {
        assertTrue(skuList.isEmpty());
        assertEquals(0, skuList.getSize());
    }

    @Test
    public void addSKU_singleSKU_sizeIncreasesByOne() {
        skuList.addSKU("ITEM-001", Location.A1);

        assertFalse(skuList.isEmpty());
        assertEquals(1, skuList.getSize());

        // Verify the added SKU's properties
        SKU addedSku = skuList.getSKUList().get(0);
        assertEquals("ITEM-001", addedSku.getSKUID());
        assertEquals(Location.A1, addedSku.getSKULocation());
    }

    @Test
    public void addSKU_multipleSKUs_sizeMatchesCount() {
        skuList.addSKU("ITEM-001", Location.A1);
        skuList.addSKU("ITEM-002", Location.B2);
        skuList.addSKU("ITEM-003", Location.C3);

        assertEquals(3, skuList.getSize());
    }

    @Test
    public void deleteSKU_existingSKU_removesSuccessfully() {
        skuList.addSKU("ITEM-001", Location.A1);
        skuList.addSKU("ITEM-002", Location.B2);

        skuList.deleteSKU("ITEM-002");

        assertEquals(1, skuList.getSize());
        // Verify the remaining SKU is the correct one
        assertEquals("ITEM-001", skuList.getSKUList().get(0).getSKUID());
    }

    @Test
    public void deleteSKU_nonExistingSKU_listRemainsUnchanged() {
        skuList.addSKU("ITEM-001", Location.A1);

        // Attempting to delete an ID that doesn't exist
        skuList.deleteSKU("ITEM-003");

        // The list size should remain exactly the same
        assertEquals(1, skuList.getSize());
    }
}
