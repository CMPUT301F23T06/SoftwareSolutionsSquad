package com.example.softwaresolutionssquad.controllers;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DatabaseController {
    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private ArrayList<InventoryItem> inventoryItems;
    private InventoryListAdapter inventoryListAdapter;

    public DatabaseController(InventoryListAdapter adapter, ArrayList<InventoryItem> items) {
        this.db = FirebaseFirestore.getInstance();
        this.itemsRef = db.collection("Item");
        this.inventoryListAdapter = adapter;
        this.inventoryItems = items;
    }

    public void addNewItem(InventoryItem newItem, DatabaseActionListener listener) {
        DocumentReference newDocRef = itemsRef.document();
        newItem.setDocId(newDocRef.getId());

        newDocRef.set(newItem)
                .addOnSuccessListener(aVoid -> {
                    inventoryItems.add(newItem);
                    inventoryListAdapter.notifyDataSetChanged();
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> listener.onFailure(e));
    }

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
                .addOnFailureListener(e -> listener.onFailure(e));
    }

    public void deleteItems(List<InventoryItem> itemsToDelete, DatabaseActionListener listener) {
        for (InventoryItem item : itemsToDelete) {
            itemsRef.document(item.getDocId()).delete()
                    .addOnSuccessListener(aVoid -> {
                        inventoryItems.remove(item);
                        inventoryListAdapter.notifyDataSetChanged();
                        listener.onSuccess();
                    })
                    .addOnFailureListener(e -> listener.onFailure(e));
        }
    }

    public void loadInitialItems(DatabaseActionListener listener) {
        itemsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
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

    // You will need to create a DatabaseActionListener interface
    // that has onSuccess and onFailure methods to handle callbacks
    public interface DatabaseActionListener {
        void onSuccess();
        void onFailure(Exception e);
    }
}
