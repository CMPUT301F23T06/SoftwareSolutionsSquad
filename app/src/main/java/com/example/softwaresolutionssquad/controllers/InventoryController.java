package com.example.softwaresolutionssquad.controllers;

import android.util.Log;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.models.InventoryModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InventoryController {

    private InventoryModel inventoryModel; // This should be your data model class

    public InventoryController(InventoryModel model) {
        this.inventoryModel = model;
    }

    public void sortInventory(int position) {
        // This method replaces the switch case logic
        Comparator<InventoryItem> comparator;
        boolean isAscending;

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
            case 7:
                inventoryModel.sortInventoryItems(inventoryModel.estimatedValueComparator, false);

            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
        // Notify the view to update, if necessary
    }

    public void deleteSelectedItems(CollectionReference itemsRef) {
        List<InventoryItem> itemsToRemove = inventoryModel.getItemsMarkedForDeletion();

        if (itemsToRemove.isEmpty()) {
            Log.d("DeleteItem", "No items selected for deletion");
            return;
        }

        for (InventoryItem item : itemsToRemove) {
            // Assuming `itemsRef` is a reference to the Firestore collection
            itemsRef.document(item.getDocId()).delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("DeleteItem", "DocumentSnapshot successfully deleted!");

                        // Delegate the removal from the model's list to the model itself
                        inventoryModel.removeItems(Collections.singletonList(item));

                        // Here you may want to inform the view to update itself, perhaps using a callback or an event
                        // e.g., viewCallback.onItemsDeleted(itemsToRemove);
                    })
                    .addOnFailureListener(e -> Log.w("DeleteItem", "Error deleting document", e));
        }
    }

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

    // ... Rest of your InventoryController code...

    // Listener interface to handle the callbacks
    public interface InventoryFetchListener {
        void onItemsFetched(List<InventoryItem> items);
        void onError(Exception e);
    }
}
