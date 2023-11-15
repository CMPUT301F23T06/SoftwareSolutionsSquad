package com.example.softwaresolutionssquad;

import static org.junit.Assert.*;

import com.example.softwaresolutionssquad.models.ComparatorFactory;
import com.example.softwaresolutionssquad.models.InventoryItem;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ComparatorFactoryTest {

    private ArrayList<InventoryItem> items;
    private InventoryItem item1, item2, item3;

    @Before
    public void setUp() {
        // Creating inventory items with different attributes for testing
        item1 = new InventoryItem(new Date(1000000000000L), "Laptop", "Dell", "XPS", "12345XYZ", 1200.00, "Office use", "DOC123456", "content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F1000000093/ORIGINAL/NONE/image%2Fjpeg/564331500");
        item2 = new InventoryItem(new Date(1000000002000L), "Monitor", "LG", "Ultra", "98765ZYX", 300.00, "Office use", "DOC654321", "content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F1000000093/ORIGINAL/NONE/image%2Fjpeg/564331501");
        item3 = new InventoryItem(new Date(1000000001000L), "Keyboard", "Logitech", "K120", "11122AAA", 50.00, "Office use", "DOC112233", "content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F1000000093/ORIGINAL/NONE/image%2Fjpeg/564331502");

        items = new ArrayList<>();
        Collections.addAll(items, item1, item2, item3);
    }

    @Test
    public void testDateComparator() {
        Comparator<InventoryItem> dateComparator = ComparatorFactory.getDateComparator();
        Collections.sort(items, dateComparator);
        assertEquals(item1, items.get(0));
        assertEquals(item3, items.get(1));
        assertEquals(item2, items.get(2));
    }

    @Test
    public void testDescriptionComparator() {
        Comparator<InventoryItem> descriptionComparator = ComparatorFactory.getDescriptionComparator();
        Collections.sort(items, descriptionComparator);
        // Assuming items are sorted alphabetically by description
        assertEquals(item3, items.get(0)); // Keyboard
        assertEquals(item1, items.get(1)); // Laptop
        assertEquals(item2, items.get(2)); // Monitor
    }

    @Test
    public void testMakeComparator() {
        Comparator<InventoryItem> makeComparator = ComparatorFactory.getMakeComparator();
        Collections.sort(items, makeComparator);
        // Assuming items are sorted alphabetically by make
        assertEquals(item1, items.get(0)); // Dell
        assertEquals(item2, items.get(1)); // LG
        assertEquals(item3, items.get(2)); // Logitech
    }

    @Test
    public void testEstimatedValueComparator() {
        Comparator<InventoryItem> valueComparator = ComparatorFactory.getEstimatedValueComparator();
        Collections.sort(items, valueComparator);
        // Assuming items are sorted by estimated value in ascending order
        assertEquals(item3, items.get(0)); // 50.00
        assertEquals(item2, items.get(1)); // 300.00
        assertEquals(item1, items.get(2)); // 1200.00
    }
}

