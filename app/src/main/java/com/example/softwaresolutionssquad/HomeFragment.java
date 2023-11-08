package com.example.softwaresolutionssquad;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements AddItemFragment.OnNewItemSubmission, InventoryListAdapter.OnDeleteButtonShowListener  {
    private FirebaseFirestore db;
    private ListView inventoryListView;
    private ArrayList<InventoryItem> inventoryItems;
    private InventoryListAdapter inventoryListAdapter;
    private CollectionReference itemsRef;
    private Button deleteButton;

    private Context context;

    private TextView estimatedValue;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("home", "here");
        View view = inflater.inflate(R.layout.fragment_home_layout, container, false);
        db = FirebaseFirestore.getInstance();
        itemsRef = db.collection("Item");

        inventoryItems = new ArrayList<>();     // Initialize the ArrayList for inventory items

        // Initialize the custom adapter and assign it to the ListView
        inventoryListAdapter = new InventoryListAdapter(context, inventoryItems);
        inventoryListView = view.findViewById(R.id.inventory_list_view);
        inventoryListView.setAdapter(inventoryListAdapter);

        estimatedValue = view.findViewById(R.id.total_estimated_value);

        populateInitialItems();     // Populate the ArrayList with initial items

        updateTotalValue();     // Update the total estimated value

        Spinner spinnerOrder = view.findViewById(R.id.spinner_order); // init Spinner to sort items
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.order_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrder.setAdapter(adapter);   // adapter for the spinner

        // Set the listener for when an item is selected in the Spinner
        spinnerOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // When an item is selected, perform sorting or other actions based on the selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // If no item is selected, no action is needed
            }
        });

        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InventoryItem selectedItem = inventoryItems.get(position);
                AddItemFragment addItemFragment = AddItemFragment.newInstance(selectedItem);

                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frag_container, addItemFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                // Access the frag_container from the activity's layout
                FrameLayout fragmentContainer = getActivity().findViewById(R.id.frag_container);
                if (fragmentContainer != null) {
                    fragmentContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedItems();      // Implement the deletion logic here
            }
        });

        inventoryListAdapter.setOnDeleteButtonShowListener(this);       // Set listener on adapter

        // Inflate the layout for this fragment
        return view;
    }

    // Method to show the delete button if any items are selected
    public void showDeleteButtonIfNeeded() {
        deleteButton.setVisibility(inventoryItems.stream().anyMatch(InventoryItem::getSelected) ? View.VISIBLE : View.GONE);
    }

    // Method to delete selected items
    private void deleteSelectedItems() {
        // Collect all items that are marked for deletion
        List<InventoryItem> itemsToRemove = inventoryItems.stream()
                .filter(InventoryItem::getSelected)
                .collect(Collectors.toList());

        // If no items are selected, simply return
        if(itemsToRemove.isEmpty()) {
            Log.d("DeleteItem", "No items selected for deletion");
            return;
        }

        // Delete each selected item from Firestore and remove it from the local list
        for (InventoryItem item : itemsToRemove) {
            // Delete from Firestore
            itemsRef.document(item.getDocId()).delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("DeleteItem", "DocumentSnapshot successfully deleted!");
                        // Remove from the local list and update the list adapter
                        inventoryItems.remove(item);
                        inventoryListAdapter.notifyDataSetChanged();
                        showDeleteButtonIfNeeded(); // Hide delete button if no items are selected
                        updateTotalValue(); // Update total value display
                    })
                    .addOnFailureListener(e -> Log.w("DeleteItem", "Error deleting document", e));
        }
    }

    // Helper method to add initial InventoryItem objects to the inventoryItems list
    private void populateInitialItems() {
        // Check if adapter is initialized
        if (inventoryListAdapter != null) {
            // Fetch all documents from Firestore and add them to the local list
            itemsRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        InventoryItem item = document.toObject(InventoryItem.class);
                        item.setDocId(document.getId()); // Ensure the document ID is set on the item
                        inventoryItems.add(item);
                    }
                    // Notify adapter about data set changes inside the success block
                    requireActivity().runOnUiThread(() -> {
                        inventoryListAdapter.notifyDataSetChanged();
                        updateTotalValue(); // Update total value after items are loaded
                    });
                } else {
                    Log.d("populateInitialItems", "Error getting documents: ", task.getException());
                }
            });
        } else {
            // Handle the case where the adapter is not initialized
            Log.e("populateInitialItems", "InventoryListAdapter is not initialized.");
        }
    }

    // Method to update total estimated value of InventoryItems
    private void updateTotalValue() {
        double totalSum = inventoryItems.stream()
                .mapToDouble(InventoryItem::getEstimatedValue)
                .sum();
        estimatedValue.setText(String.format(Locale.US, "%.2f", totalSum));
    }

    // This method is called when the OK button is pressed in the AddItemFragment
    // It adds the new item to the inventory list and updates the adapter
    @Override
    public void onOKPressed(InventoryItem newItem) {
        // Get a new document reference from Firestore, which has an auto-generated ID
        DocumentReference newDocRef = itemsRef.document();

        // Set the document ID inside the new item object
        newItem.setDocId(newDocRef.getId()); // Make sure InventoryItem has a method to set its ID

        // Set the new item in the Firestore document
        newDocRef.set(newItem)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AddItem", "DocumentSnapshot written with ID: " + newDocRef.getId());
                    // Add the new item to the local list and notify the adapter
                    inventoryItems.add(newItem);
                    inventoryListAdapter.notifyDataSetChanged();
                    updateTotalValue();
                })
                .addOnFailureListener(e -> Log.w("AddItem", "Error adding document", e));

//        inventoryItems.add(newItem);
//        inventoryListAdapter.notifyDataSetChanged();
//        updateTotalValue();
    }

    // This method updates an existing inventory item
    public void onUpdatePressed(InventoryItem updatedItem) {
        // Use the ID from the updatedItem to reference the Firestore document
        itemsRef.document(updatedItem.getDocId()).set(updatedItem)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UpdateItem", "DocumentSnapshot successfully updated!");

                    // Update the item in the local list and notify the adapter
                    int itemIndex = inventoryItems.indexOf(updatedItem);
                    if (itemIndex != -1) {
                        inventoryItems.set(itemIndex, updatedItem);
                        inventoryListAdapter.notifyDataSetChanged();
                        updateTotalValue();
                    }
                })
                .addOnFailureListener(e -> Log.w("UpdateItem", "Error updating document", e));
    }

    public FirebaseFirestore getDb() {
        return db;
    }
}