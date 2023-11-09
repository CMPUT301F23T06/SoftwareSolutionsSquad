package com.example.softwaresolutionssquad.controllers;

import com.example.softwaresolutionssquad.models.ComparatorFactory;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortController {
    private InventoryListAdapter inventoryListAdapter;
    private List<InventoryItem> inventoryItems;

    public SortController(InventoryListAdapter inventoryListAdapter, List<InventoryItem> inventoryItems) {
        this.inventoryListAdapter = inventoryListAdapter;
        this.inventoryItems = inventoryItems;
    }

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
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    private void sortInventoryItems(Comparator<InventoryItem> comparator, boolean ascending) {
        if (ascending) {
            Collections.sort(inventoryItems, comparator);
        } else {
            Collections.sort(inventoryItems, Collections.reverseOrder(comparator));
        }
        inventoryListAdapter.notifyDataSetChanged();
    }
}
