package com.example.softwaresolutionssquad.models;

import java.util.ArrayList;
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

    /**
     * Creates a comparator for InventoryItem that compares by tags.
     *
     * @return Comparator for InventoryItem
     */
    public static Comparator<InventoryItem> getTagComparator() {
        return (item1, item2) -> {
            ArrayList<String> tags1 = item1.getSortedTags();
            ArrayList<String> tags2 = item2.getSortedTags();

            // Check if one of the lists is empty
            if (tags1.isEmpty() && tags2.isEmpty()) {
                return 0; // Both are empty, consider them equal
            } else if (tags1.isEmpty()) {
                return -1; // Only tags1 is empty, consider it smaller
            } else if (tags2.isEmpty()) {
                return 1; // Only tags2 is empty, consider it smaller
            }

            // Compare the strings element-wise
            for (int i = 0; i < Math.min(tags1.size(), tags2.size()); i++) {
                int comparison = tags1.get(i).compareTo(tags2.get(i));
                if (comparison != 0) {
                    return comparison;
                }
            }

            return Integer.compare(tags1.size(), tags2.size());
        };
    }

}
