package com.example.softwaresolutionssquad.models;

import java.util.Comparator;

/**
 * Factory class for creating various comparators for InventoryItem objects.
 */
public class ComparatorFactory {

    /**
     * Creates a comparator for InventoryItem that compares by purchase date.
     *
     * @return Comparator for InventoryItem
     */
    public static Comparator<InventoryItem> getDateComparator() {
        return Comparator.comparing(InventoryItem::getPurchaseDate);
    }

    /**
     * Creates a comparator for InventoryItem that compares by description.
     *
     * @return Comparator for InventoryItem
     */
    public static Comparator<InventoryItem> getDescriptionComparator() {
        return Comparator.comparing(InventoryItem::getDescription);
    }

    /**
     * Creates a comparator for InventoryItem that compares by make.
     *
     * @return Comparator for InventoryItem
     */
    public static Comparator<InventoryItem> getMakeComparator() {
        return Comparator.comparing(InventoryItem::getMake);
    }

    /**
     * Creates a comparator for InventoryItem that compares by estimated value.
     *
     * @return Comparator for InventoryItem
     */
    public static Comparator<InventoryItem> getEstimatedValueComparator() {
        return Comparator.comparingDouble(InventoryItem::getEstimatedValue);
    }
}
