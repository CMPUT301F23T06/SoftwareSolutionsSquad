package com.example.softwaresolutionssquad.models;

import com.example.softwaresolutionssquad.models.InventoryItem;

import java.util.Comparator;

public class ComparatorFactory {
    public static Comparator<InventoryItem> getDateComparator() {
        return new Comparator<InventoryItem>() {
            @Override
            public int compare(InventoryItem item1, InventoryItem item2) {
                return item1.getPurchaseDate().compareTo(item2.getPurchaseDate());
            }
        };
    }

    public static Comparator<InventoryItem> getDescriptionComparator() {
        return new Comparator<InventoryItem>() {
            @Override
            public int compare(InventoryItem item1, InventoryItem item2) {
                return item1.getDescription().compareTo(item2.getDescription());
            }
        };
    }

    public static Comparator<InventoryItem> getMakeComparator() {
        return new Comparator<InventoryItem>() {
            @Override
            public int compare(InventoryItem item1, InventoryItem item2) {
                return item1.getMake().compareTo(item2.getMake());
            }
        };
    }

    public static Comparator<InventoryItem> getEstimatedValueComparator() {
        return new Comparator<InventoryItem>() {
            @Override
            public int compare(InventoryItem item1, InventoryItem item2) {
                return Double.compare(item1.getEstimatedValue(), item2.getEstimatedValue());
            }
        };
    }
}
