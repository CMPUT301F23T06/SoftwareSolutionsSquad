package com.example.softwaresolutionssquad.controllers;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for database operations related to inventory items.
 */
public class DatabaseController {
    private final FirebaseFirestore db;
    private final CollectionReference itemsRef;
    private final ArrayList<InventoryItem> inventoryItems;
    private final InventoryListAdapter inventoryListAdapter;

    /**
     * Constructs a DatabaseController instance.
     *
     * @param adapter The adapter for the inventory list.
     * @param items   The list of inventory items.
     */
    public DatabaseController(InventoryListAdapter adapter, ArrayList<InventoryItem> items) {
        this.db = FirebaseFirestore.getInstance();
        this.itemsRef = db.collection("Item");
        this.inventoryListAdapter = adapter;
        this.inventoryItems = items;
    }

    /**
     * Adds a new item to the database.
     *
     * @param newItem  The new item to be added.
     * @param listener The listener to handle callbacks.
     */
    public void addNewItem(InventoryItem newItem, DatabaseActionListener listener) {
        DocumentReference newDocRef = itemsRef.document();
        newItem.setDocId(newDocRef.getId());
        newDocRef.set(newItem)
                .addOnSuccessListener(aVoid -> {
                    inventoryItems.add(newItem);
                    inventoryListAdapter.notifyDataSetChanged();
                    listener.onSuccess();
                })
                .addOnFailureListener(listener::onFailure);
    }

    /**
     * Updates an existing item in the database.
     *
     * @param updatedItem The item with updated information.
     * @param listener    The listener to handle callbacks.
     */
    public void updateItem(InventoryItem updatedItem, DatabaseActionListener listener) {
        itemsRef.document(updatedItem.getDocId()).set(updatedItem)
                .addOnSuccessListener(aVoid -> {
                    int itemIndex = inventoryItems.indexOf(updatedItem);
                    if (itemIndex != -1) {
                        inventoryItems.set(itemIndex, updatedItem);
                        inventoryListAdapter.notifyDataSetChanged();
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    /**
     * Deletes a list of items from the database.
     *
     * @param itemsToDelete The list of items to delete.
     * @param listener      The listener to handle callbacks.
     */
    public void deleteItems(List<InventoryItem> itemsToDelete, DatabaseActionListener listener) {
        for (InventoryItem item : itemsToDelete) {
            itemsRef.document(item.getDocId()).delete()
                    .addOnSuccessListener(aVoid -> {
                        inventoryItems.remove(item);
                        inventoryListAdapter.notifyDataSetChanged();
                        listener.onSuccess();
                    })
                    .addOnFailureListener(listener::onFailure);
        }
    }

    /**
     * Loads the initial set of items from the database.
     *
     * @param listener The listener to handle callbacks.
     */
    public void loadInitialItems(DatabaseActionListener listener) {
        itemsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                inventoryItems.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    InventoryItem item = document.toObject(InventoryItem.class);
                    item.setDocId(document.getId());
                    inventoryItems.add(item);
                }
                inventoryListAdapter.notifyDataSetChanged();
                listener.onSuccess();
            } else {
                listener.onFailure(task.getException());
            }
        });
    }

    /**
     * Interface for database action callbacks.
     */
    public interface DatabaseActionListener {
        /**
         * Called when a database action completes successfully.
         */
        void onSuccess();

        /**
         * Called when a database action fails.
         *
         * @param e The exception that caused the failure.
         */
        void onFailure(Exception e);
    }
}
