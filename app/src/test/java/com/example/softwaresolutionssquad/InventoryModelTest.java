package com.example.softwaresolutionssquad;

import static org.junit.Assert.*;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.models.InventoryModel;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class InventoryModelTest {

    private InventoryModel inventoryModel;
    private InventoryItem item1, item2, item3;

    @Before
    public void setUp() {
        // Creating sample inventory items
        item1 = new InventoryItem(new Date(1000000000000L), "Laptop", "Dell", "XPS", "12345XYZ", 1200.00, "Office use", "DOC123456");
        item2 = new InventoryItem(new Date(1000000002000L), "Monitor", "LG", "Ultra", "98765ZYX", 300.00, "Office use", "DOC654321");
        item3 = new InventoryItem(new Date(1000000001000L), "Keyboard", "Logitech", "K120", "11122AAA", 50.00, "Office use", "DOC112233");

        ArrayList<InventoryItem> items = new ArrayList<>(Arrays.asList(item1, item2, item3));
        inventoryModel = new InventoryModel(items);
    }

    @Test
    public void testGetInventoryItems() {
        List<InventoryItem> items = inventoryModel.getInventoryItems();
        assertTrue(items.contains(item1) && items.contains(item2) && items.contains(item3));
    }

    @Test
    public void testSortInventoryItemsByDate() {
        inventoryModel.sortInventoryItems(InventoryModel.dateComparator, true);
        assertEquals(item1, inventoryModel.getInventoryItems().get(0));
    }

    @Test
    public void testSortInventoryItemsByDescription() {
        inventoryModel.sortInventoryItems(InventoryModel.descriptionComparator, true);
        assertEquals(item1, inventoryModel.getInventoryItems().get(1));
    }

    @Test
    public void testSortInventoryItemsByEstimatedValue() {
        inventoryModel.sortInventoryItems(InventoryModel.estimatedValueComparator, false);
        assertEquals(item1, inventoryModel.getInventoryItems().get(0));
    }

    @Test
    public void testSortInventoryItemsByMake() {
        inventoryModel.sortInventoryItems(InventoryModel.makeComparator, true);
        assertEquals(item2, inventoryModel.getInventoryItems().get(1));
    }

    @Test
    public void testRemoveItems() {
        inventoryModel.removeItems(Arrays.asList(item2));
        assertFalse(inventoryModel.getInventoryItems().contains(item2));
    }

    @Test
    public void testGetItemsMarkedForDeletion() {
        item1.setSelected(true);
        List<InventoryItem> markedItems = inventoryModel.getItemsMarkedForDeletion();
        assertTrue(markedItems.contains(item1) && !markedItems.contains(item2));
    }
}
