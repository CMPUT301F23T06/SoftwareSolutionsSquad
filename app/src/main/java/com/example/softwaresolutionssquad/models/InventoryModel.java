package com.example.softwaresolutionssquad.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Model representing the inventory containing a list of inventory items.
 */
public class InventoryModel {

    private final ArrayList<InventoryItem> inventoryItems;

    /**
     * Constructs an InventoryModel with the specified list of inventory items.
     *
     * @param items The list of inventory items to initialize the model with.
     */
    public InventoryModel(ArrayList<InventoryItem> items) {
        this.inventoryItems = items;
    }

    /**
     * Returns the list of all inventory items.
     *
     * @return The current list of inventory items.
     */
    public ArrayList<InventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    /**
     * Comparator to sort inventory items by date of purchase.
     */
    public static final Comparator<InventoryItem> dateComparator = Comparator.comparing(InventoryItem::getPurchaseDate);

    /**
     * Comparator to sort inventory items by description.
     */
    public static final Comparator<InventoryItem> descriptionComparator = Comparator.comparing(InventoryItem::getDescription);

    /**
     * Comparator to sort inventory items by their estimated value.
     */
    public static final Comparator<InventoryItem> estimatedValueComparator = Comparator.comparingDouble(InventoryItem::getEstimatedValue);

    /**
     * Comparator to sort inventory items by make.
     */
    public static final Comparator<InventoryItem> makeComparator = Comparator.comparing(InventoryItem::getMake);

    /**
     * Sorts the inventory items based on the provided comparator and order.
     *
     * @param comparator The comparator to define the sort order.
     * @param isAscending A boolean flag for sorting in ascending order.
     */
    public void sortInventoryItems(Comparator<InventoryItem> comparator, boolean isAscending) {
        inventoryItems.sort(isAscending ? comparator : comparator.reversed());
    }

    /**
     * Removes a list of items from the inventory.
     *
     * @param itemsToRemove The list of items to remove from the inventory.
     */
    public void removeItems(List<InventoryItem> itemsToRemove) {
        inventoryItems.removeAll(itemsToRemove);
    }

    /**
     * Retrieves a list of inventory items that are marked for deletion.
     *
     * @return A list of items marked for deletion.
     */
    public List<InventoryItem> getItemsMarkedForDeletion() {
        return inventoryItems.stream()
                .filter(InventoryItem::getSelected)
                .collect(Collectors.toList());
    }
}
