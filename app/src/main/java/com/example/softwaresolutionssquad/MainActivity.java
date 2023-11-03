package com.example.softwaresolutionssquad;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.softwaresolutionssquad.AddItemFragment;
import com.example.softwaresolutionssquad.InventoryItem;
import com.example.softwaresolutionssquad.InventoryListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;

// Define the MainActivity class which extends AppCompatActivity to inherit common app behaviors
// and implements the OnNewItemSubmission interface for communication with AddItemFragment
public class MainActivity extends AppCompatActivity implements AddItemFragment.OnNewItemSubmission {

    private FirebaseFirestore db;

    // ListView for displaying inventory items
    private ListView inventoryListView;
    // ArrayList to store InventoryItem objects
    private ArrayList<InventoryItem> inventoryItems;
    // Custom adapter for converting an ArrayList of items into View items for the ListView
    private InventoryListAdapter inventoryListAdapter;

    // The onCreate method is called when the Activity is starting
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // Set the user interface layout for this Activity
        setContentView(R.layout.activity_main);

        // Find the ListView by its ID and initialize it
        inventoryListView = findViewById(R.id.inventory_list_view);

        // Initialize the ArrayList for inventory items
        inventoryItems = new ArrayList<>();
        // Populate the ArrayList with initial items
        populateInitialItems();

        // Initialize the custom adapter and assign it to the ListView
        inventoryListAdapter = new InventoryListAdapter(this, inventoryItems);
        inventoryListView.setAdapter(inventoryListAdapter);

        // Initialize the Spinner to sort inventory items
        Spinner spinnerOrder = findViewById(R.id.spinner_order);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerOrder.setAdapter(adapter);

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

        // Set up the ImageView that will act as the 'Add' button
        ImageView myIcon = findViewById(R.id.add_icon);
        // Set a click listener on the 'Add' button
        myIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make the fragment container visible
                FrameLayout fragmentContainer = findViewById(R.id.frag_container);
                fragmentContainer.setVisibility(View.VISIBLE);

                // Create an instance of AddItemFragment and display it using FragmentTransaction
                AddItemFragment addItemFragment = new AddItemFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frag_container, addItemFragment);
                // Optionally add the transaction to the back stack
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
            }
        });

    }

    // Helper method to add initial InventoryItem objects to the inventoryItems list
    private void populateInitialItems() {
        // Create and add InventoryItem instances to the ArrayList
        // Each item has properties like date, name, brand, model, serial number, price, and description
        inventoryItems.add(new InventoryItem(new Date(), "Laptop", "Dell", "Inspiron 5000", "SN12345", 800.00, "Work laptop"));
        inventoryItems.add(new InventoryItem(new Date(), "Phone", "Apple", "iPhone 13", "SN67890", 1200.00, "Personal phone"));
        inventoryItems.add(new InventoryItem(new Date(), "Headphones", "Sony", "WH-1000XM4", "SN11121", 300.00, "Noise-cancelling headphones"));
        // More items can be added here as needed
    }

    // This method is called when the OK button is pressed in the AddItemFragment
    // It adds the new item to the inventory list and updates the adapter
    @Override
    public void onOKPressed(InventoryItem newItem) {
        // Add the new InventoryItem to the list
        inventoryItems.add(newItem);
        // Notify the adapter that the underlying dataset has changed to update the ListView
        inventoryListAdapter.notifyDataSetChanged();
    }
}