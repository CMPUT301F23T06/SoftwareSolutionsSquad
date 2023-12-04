package com.example.softwaresolutionssquad;

import com.example.softwaresolutionssquad.controllers.SortController;
import com.example.softwaresolutionssquad.models.ComparatorFactory;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.mockito.Mockito.*;

public class SortControllerTest {

    @Mock private InventoryListAdapter mockInventoryListAdapter;
    private List<InventoryItem> inventoryItems;
    private SortController controller;

    /**
     * Sets up the test environment before each test. Initializes mock objects and the SortController.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        inventoryItems = new ArrayList<>();

        controller = new SortController(mockInventoryListAdapter, inventoryItems, Boolean.TRUE);
    }

    /**
     * Tests the onItemSelected functionality of the SortController.
     */
    @Test
    public void testOnItemSelected() {
        // Test for each case in the switch statement
        for (int i = 0; i <= 9; i++) {
            // Reset the inventoryItems to the initial state before each sort
            setUpInventoryItems();
            Mockito.reset(mockInventoryListAdapter);
            controller.onItemSelected(i);

            // Verify that Collections.sort was called with the correct comparator
            Comparator<InventoryItem> expectedComparator = getExpectedComparator(i);
            boolean ascending = isAscending(i);
            if (ascending) {
                Collections.sort(inventoryItems, expectedComparator);
            } else {
                Collections.sort(inventoryItems, Collections.reverseOrder(expectedComparator));
            }
            verify(mockInventoryListAdapter).notifyDataSetChanged();

        }

    }

    private void setUpInventoryItems() {
        inventoryItems.clear();

    }

    private Comparator<InventoryItem> getExpectedComparator(int position) {
        switch (position / 2) {
            case 0: return ComparatorFactory.getDateComparator();
            case 1: return ComparatorFactory.getDescriptionComparator();
            case 2: return ComparatorFactory.getMakeComparator();
            case 3: return ComparatorFactory.getEstimatedValueComparator();
            case 4: return ComparatorFactory.getTagComparator();
            default: throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    private boolean isAscending(int position) {
        return position % 2 == 0;
    }

}
