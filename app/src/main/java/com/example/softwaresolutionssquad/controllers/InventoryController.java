package com.example.softwaresolutionssquad.controllers;

import android.util.Log;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.models.InventoryModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controller for managing inventory operations such as sorting and deleting items.
 */
public class InventoryController {

    private final InventoryModel inventoryModel;

    /**
     * Constructor for InventoryController.
     *
     * @param model the InventoryModel to be managed by this controller
     */
    public InventoryController(InventoryModel model) {
        this.inventoryModel = model;
    }

    /**
     * Sorts the inventory items based on the selected criteria.
     *
     * @param position the sort criteria and order
     */
    public void sortInventory(int position) {
        switch (position) {
            case 0:
                inventoryModel.sortInventoryItems(inventoryModel.dateComparator, true);
                break;
            case 1:
                inventoryModel.sortInventoryItems(inventoryModel.dateComparator, false);
                break;
            case 2:
                inventoryModel.sortInventoryItems(inventoryModel.descriptionComparator, true);
                break;
            case 3:
                inventoryModel.sortInventoryItems(inventoryModel.descriptionComparator, false);
                break;
            case 4:
                inventoryModel.sortInventoryItems(inventoryModel.makeComparator, true);
                break;
            case 5:
                inventoryModel.sortInventoryItems(inventoryModel.makeComparator, false);
                break;
            case 6:
                inventoryModel.sortInventoryItems(inventoryModel.estimatedValueComparator, true);
                break;
            case 7:
                inventoryModel.sortInventoryItems(inventoryModel.estimatedValueComparator, false);
                break;
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    /**
     * Deletes selected items from the inventory.
     *
     * @param itemsRef reference to the Firestore collection of items
     */
    public void deleteSelectedItems(CollectionReference itemsRef) {
        List<InventoryItem> itemsToRemove = inventoryModel.getItemsMarkedForDeletion();

        if (itemsToRemove.isEmpty()) {
            Log.d("InventoryController", "No items selected for deletion");
            return;
        }

        for (InventoryItem item : itemsToRemove) {
            itemsRef.document(item.getDocId()).delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("InventoryController", "Item successfully deleted");
                        inventoryModel.removeItems(Collections.singletonList(item));
                    })
                    .addOnFailureListener(e -> Log.w("InventoryController", "Error deleting item", e));
        }
    }

    /**
     * Fetches the initial set of items for the inventory.
     *
     * @param listener callback listener for item fetch events
     * @param itemsRef reference to the Firestore collection of items
     */
    public void fetchInitialItems(final InventoryFetchListener listener, CollectionReference itemsRef) {
        itemsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<InventoryItem> fetchedItems = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    InventoryItem item = document.toObject(InventoryItem.class);
                    item.setDocId(document.getId());
                    fetchedItems.add(item);
                }
                listener.onItemsFetched(fetchedItems);
            } else {
                listener.onError(task.getException());
            }
        });
    }

    // Listener interface for inventory fetch events
    public interface InventoryFetchListener {
        void onItemsFetched(List<InventoryItem> items);

        void onError(Exception e);
    }
}
