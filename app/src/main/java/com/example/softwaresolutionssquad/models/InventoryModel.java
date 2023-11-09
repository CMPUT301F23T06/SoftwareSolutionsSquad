package com.example.softwaresolutionssquad.models;

import com.example.softwaresolutionssquad.models.InventoryItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryModel {

    private ArrayList<InventoryItem> inventoryItems; // This list holds inventory item objects

    public ArrayList<InventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    // Comparator examples
    public Comparator<InventoryItem> dateComparator = new Comparator<InventoryItem>() {
        @Override
        public int compare(InventoryItem item1, InventoryItem item2) {
            // Compare items by date
            return item1.getPurchaseDate().compareTo(item2.getPurchaseDate());
        }
    };
    public Comparator<InventoryItem> descriptionComparator = new Comparator<InventoryItem>() {
        @Override
        public int compare(InventoryItem item1, InventoryItem item2) {
            // Compare items by description
            return item1.getDescription().compareTo(item2.getDescription());
        }
    };

    public Comparator<InventoryItem> estimatedValueComparator = new Comparator<InventoryItem>() {
        @Override
        public int compare(InventoryItem item1, InventoryItem item2) {
            // Compare items by estimated value
            return Double.compare(item1.getEstimatedValue(), item2.getEstimatedValue());
        }
    };
    public Comparator<InventoryItem> makeComparator = new Comparator<InventoryItem>() {
        @Override
        public int compare(InventoryItem item1, InventoryItem item2) {
            // Compare items by make
            return item1.getMake().compareTo(item2.getMake());
        }
    };
    public InventoryModel(ArrayList<InventoryItem> Items) {
        inventoryItems = Items;
        // Initialize the comparators and the inventory list
        // This could also involve retrieving data from a database or API
    }

    // Getters for the comparators

    // ... other getters for comparators

    // Method to sort inventory items
    public void sortInventoryItems(Comparator<InventoryItem> comparator, boolean isAscending) {
        if(isAscending) {
            Collections.sort(inventoryItems, comparator);
        } else {
            Collections.sort(inventoryItems, comparator.reversed());
        }

        // After sorting, you may need to update any observers/views that the data has changed
    }

    public void removeItems(List<InventoryItem> itemsToRemove) {
        for (InventoryItem item : itemsToRemove) {
            inventoryItems.remove(item);
        }
    }
    // Add a method to get selected items for deletion
    public List<InventoryItem> getItemsMarkedForDeletion() {
        return inventoryItems.stream()
                .filter(InventoryItem::getSelected)
                .collect(Collectors.toList());
    }

    // ... other data-related methods, like adding, removing items, etc.
}
