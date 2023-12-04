package com.example.softwaresolutionssquad;

import static org.junit.Assert.*;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.models.InventoryModel;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class InventoryModelTest {

    private InventoryModel inventoryModel;
    private InventoryItem item1, item2, item3;

    /**
     * Sets up the test environment before each test.
     */
    @Before
    public void setUp() {
        ArrayList<String> imagesItem1 = new ArrayList<>();
        ArrayList<String> imagesItem2 = new ArrayList<>();
        ArrayList<String> imagesItem3 = new ArrayList<>();

        imagesItem1.add("content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F1000000093/ORIGINAL/NONE/image%2Fjpeg/564331500");
        imagesItem2.add("content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F1000000093/ORIGINAL/NONE/image%2Fjpeg/564331501");
        imagesItem3.add("content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F1000000093/ORIGINAL/NONE/image%2Fjpeg/564331502");

        // Creating sample inventory items
        item1 = new InventoryItem(new Date(1000000000000L), "Laptop", "Dell", "XPS", "12345XYZ", 1200.00, "Office use", "DOC123456", imagesItem1, "");
        item2 = new InventoryItem(new Date(1000000002000L), "Monitor", "LG", "Ultra", "98765ZYX", 300.00, "Office use", "DOC654321", imagesItem2, "");
        item3 = new InventoryItem(new Date(1000000001000L), "Keyboard", "Logitech", "K120", "11122AAA", 50.00, "Office use", "DOC112233", imagesItem3, "");

        ArrayList<InventoryItem> items = new ArrayList<>(Arrays.asList(item1, item2, item3));
        inventoryModel = new InventoryModel(items);
    }

    /**
     * Tests various getter and setter methods of the InventoryModel class.
     */
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
        inventoryModel.removeItems(Collections.singletonList(item2));
        assertFalse(inventoryModel.getInventoryItems().contains(item2));
    }

    @Test
    public void testGetItemsMarkedForDeletion() {
        item1.setSelected(true);
        List<InventoryItem> markedItems = inventoryModel.getItemsMarkedForDeletion();
        assertTrue(markedItems.contains(item1) && !markedItems.contains(item2));
    }
}
