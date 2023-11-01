package com.example.softwaresolutionssquad;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private ListView inventoryListView;
    private ArrayList<InventoryItem> inventoryItems;
    private InventoryListAdapter inventoryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_main);

        inventoryListView = findViewById(R.id.inventory_list_view);

        // Populate initial inventory items
        inventoryItems = new ArrayList<>();
        populateInitialItems();

        inventoryListAdapter = new InventoryListAdapter(this, inventoryItems);
        inventoryListView.setAdapter(inventoryListAdapter);
    }

    private void populateInitialItems() {
        inventoryItems.add(new InventoryItem(new Date(), "Laptop", "Dell", "Inspiron 5000", "SN12345", 800.00, "Work laptop"));
        inventoryItems.add(new InventoryItem(new Date(), "Phone", "Apple", "iPhone 13", "SN67890", 1200.00, "Personal phone"));
        inventoryItems.add(new InventoryItem(new Date(), "Headphones", "Sony", "WH-1000XM4", "SN11121", 300.00, "Noise-cancelling headphones"));
        // Add more items as needed
    }
}