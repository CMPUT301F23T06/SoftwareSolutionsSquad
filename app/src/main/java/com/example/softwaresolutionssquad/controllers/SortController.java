package com.example.softwaresolutionssquad.controllers;

import com.example.softwaresolutionssquad.models.ComparatorFactory;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for handling sorting operations on the inventory list.
 */
public class SortController {
    private final InventoryListAdapter inventoryListAdapter;
    private final List<InventoryItem> inventoryItems;

    /**
     * Constructs a SortController with the specified adapter and inventory items.
     *
     * @param inventoryListAdapter the adapter for the inventory list
     * @param inventoryItems       the list of inventory items to be sorted
     */
    public SortController(InventoryListAdapter inventoryListAdapter, List<InventoryItem> inventoryItems) {
        this.inventoryListAdapter = inventoryListAdapter;
        this.inventoryItems = inventoryItems;
    }

    /**
     * Handles item selection for sorting the inventory list.
     *
     * @param position the position of the selected sorting option
     */
    public void onItemSelected(int position) {
        Comparator<InventoryItem> comparator;

        switch (position) {
            case 0:
                comparator = ComparatorFactory.getDateComparator();
                sortInventoryItems(comparator, true);
                break;
            case 1:
                comparator = ComparatorFactory.getDateComparator();
                sortInventoryItems(comparator, false);
                break;
            case 2:
                comparator = ComparatorFactory.getDescriptionComparator();
                sortInventoryItems(comparator, true);
                break;
            case 3:
                comparator = ComparatorFactory.getDescriptionComparator();
                sortInventoryItems(comparator, false);
                break;
            case 4:
                comparator = ComparatorFactory.getMakeComparator();
                sortInventoryItems(comparator, true);
                break;
            case 5:
                comparator = ComparatorFactory.getMakeComparator();
                sortInventoryItems(comparator, false);
                break;
            case 6:
                comparator = ComparatorFactory.getEstimatedValueComparator();
                sortInventoryItems(comparator, true);
                break;
            case 7:
                comparator = ComparatorFactory.getEstimatedValueComparator();
                sortInventoryItems(comparator, false);
                break;
            case 8:
                comparator = ComparatorFactory.getTagComparator();
                sortInventoryItems(comparator, true);
                break;
            case 9: 
                comparator = ComparatorFactory.getTagComparator();
                sortInventoryItems(comparator, false);
                break;
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    /**
     * Sorts inventory items according to the given comparator and order.
     *
     * @param comparator the comparator to define the sort order
     * @param ascending  true if the sort should be in ascending order, false for descending
     */
    private void sortInventoryItems(Comparator<InventoryItem> comparator, boolean ascending) {
        if (ascending) {
            Collections.sort(inventoryItems, comparator);
        } else {
            Collections.sort(inventoryItems, Collections.reverseOrder(comparator));
        }
        inventoryListAdapter.notifyDataSetChanged();
    }
}
