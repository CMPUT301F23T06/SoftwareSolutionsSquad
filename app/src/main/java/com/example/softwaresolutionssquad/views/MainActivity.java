package com.example.softwaresolutionssquad.views;
import com.example.softwaresolutionssquad.R;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Collections;
import java.util.Comparator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentTransaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.example.softwaresolutionssquad.controllers.DateFilterController;
import com.example.softwaresolutionssquad.controllers.KeywordFilterController;
import com.example.softwaresolutionssquad.controllers.MakeFilterController;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.AddItemFragment;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;





// Define the MainActivity class which extends AppCompatActivity to inherit common app behaviors
// and implements the OnNewItemSubmission interface for communication with AddItemFragment
public class MainActivity extends AppCompatActivity implements AddItemFragment.OnNewItemSubmission, InventoryListAdapter.OnDeleteButtonShowListener {

    private FirebaseFirestore db;
    private ListView inventoryListView;
    private ArrayList<InventoryItem> inventoryItems;
    private InventoryListAdapter inventoryListAdapter;
    private CollectionReference itemsRef;
    private Button deleteButton;
    // Buttons used to enable filtering based on date, keyword, make, and tags
    private TextView dateButton, keywordButton, makeButton, tagButton;
    // Linear layouts containing necessary elements for filtering types
    private LinearLayout dateFilter, keyFilter, makeFilter, tagFilter;
    // Predicate for filters to use when determining which items match conditions
    private Predicate<InventoryItem> filterCondition;

    // The onCreate method is called when the Activity is starting
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Set the user interface layout for this Activity

        db = FirebaseFirestore.getInstance();
        itemsRef = db.collection("Item");

        inventoryItems = new ArrayList<>();     // Initialize the ArrayList for inventory items

        // Initialize the custom adapter and assign it to the ListView
        inventoryListAdapter = new InventoryListAdapter(this, inventoryItems);
        inventoryListView = findViewById(R.id.inventory_list_view);
        inventoryListView.setAdapter(inventoryListAdapter);

        populateInitialItems();     // Populate the ArrayList with initial items

        updateTotalValue();     // Update the total estimated value

        Spinner spinnerOrder = findViewById(R.id.spinner_order); // init Spinner to sort items
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrder.setAdapter(adapter);   // adapter for the spinner

        // Set the listener for when an item is selected in the Spinner
        spinnerOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // When an item is selected, perform sorting or other actions based on the selected item
                switch (position) {
                    case 0:
                        sortInventoryItems(dateComparator, true); // Sort by date in ascending order
                        break;
                    case 1:
                        sortInventoryItems(dateComparator, false); // Sort by date in descending order
                        break;
                    case 2:
                        sortInventoryItems(descriptionComparator, true); // Sort by description in ascending order
                        break;
                    case 3:
                        sortInventoryItems(descriptionComparator, false); // Sort by description in descending order
                        break;
                    case 4:
                        sortInventoryItems(makeComparator, true); // Sort by make in ascending order
                        break;
                    case 5:
                        sortInventoryItems(makeComparator, false); // Sort by make in descending order
                        break;
                    case 6:
                        sortInventoryItems(estimatedValueComparator, true); // Sort by estimated value in ascending order
                        break;
                    case 7:
                        sortInventoryItems(estimatedValueComparator, false); // Sort by estimated value in descending order
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // If no item is selected, no action is needed
            }
        });

        ImageView myIcon = findViewById(R.id.add_icon);     // ImageView will act as 'Add' button
        myIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout fragmentContainer = findViewById(R.id.frag_container);
                fragmentContainer.setVisibility(View.VISIBLE);

                // Create an instance of AddItemFragment and display it using FragmentTransaction
                AddItemFragment addItemFragment = new AddItemFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frag_container, addItemFragment);

                transaction.addToBackStack(null);   // Optionally add transaction to stack
                transaction.commit();       // Commit the transaction
            }
        });

        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InventoryItem selectedItem = inventoryItems.get(position);
                AddItemFragment addItemFragment = AddItemFragment.newInstance(selectedItem);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frag_container, addItemFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                FrameLayout fragmentContainer = findViewById(R.id.frag_container);
                fragmentContainer.setVisibility(View.VISIBLE);
            }
        });

        deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedItems();      // Implement the deletion logic here
            }
        });

        inventoryListAdapter.setOnDeleteButtonShowListener(this);       // Set listener on adapter

        // Buttons that trigger the date, keyword, make, and tag filters
        dateButton = findViewById(R.id.date_btn);
        keywordButton = findViewById(R.id.keyword_btn);
        makeButton = findViewById(R.id.make_btn);
        tagButton = findViewById(R.id.tag_btn);

        // Linear layouts that are shown when filter buttons are clicked
        dateFilter = findViewById(R.id.date_filter);
        keyFilter = findViewById(R.id.keyword_filter);
        makeFilter = findViewById(R.id.make_filter);
        tagFilter = findViewById(R.id.tag_filter);
        EditText startDate = findViewById(R.id.dateStart);
        EditText endDate = findViewById(R.id.dateEnd);
        EditText keywords = findViewById(R.id.keywords);


        // Allow user to filter all items in an inputted date range
        DateFilterController dateFilterController = new DateFilterController(
                this, // Context
                dateFilter,
                startDate,
                endDate,
                dateButton,
                keywordButton,
                makeButton,
                tagButton,
                keyFilter,
                makeFilter,
                tagFilter,
                inventoryListAdapter,
                inventoryListView,
                inventoryItems // The data list
        );

        KeywordFilterController keywordFilterController = new KeywordFilterController(
                this, // context
                keyFilter,
                keywords,
                keywordButton,
                dateButton,
                makeButton,
                tagButton,
                dateFilter,
                makeFilter,
                tagFilter,
                inventoryListAdapter,
                inventoryListView,
                inventoryItems
        );
        // Allow user to filter items based on the presence of keywords in description
        TextView makes = findViewById(R.id.make);
        MakeFilterController makeFilterController = new MakeFilterController(
                this,
                makes,
                makeFilter,
                keywordButton,
                dateButton,
                makeButton,
                tagButton,
                inventoryListAdapter,
                inventoryListView,
                inventoryItems
        );
        // Allow user to filter items based on specified makes
        TextView makeButton = findViewById(R.id.make_btn);
        makeButton.setOnClickListener(v -> {
            // Hide other filters here if they are part of MainActivity
            // ...
            makeFilterController.toggleMakeFilterVisibility();
        });
        // Allow user to filter items based on tags associated with items
        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyFilter.setVisibility(View.GONE);
                makeFilter.setVisibility(View.GONE);
                dateFilter.setVisibility(View.GONE);

                // Enable filter if button is pressed while filters are not visible
                if (tagFilter.getVisibility() == View.GONE) {
                    // Show the LinearLayout with the necessary filtering elements
                    tagFilter.setVisibility(View.VISIBLE);

                    ViewCompat.setBackgroundTintList(dateButton, null);
                    ViewCompat.setBackgroundTintList(makeButton, null);
                    ViewCompat.setBackgroundTintList(keywordButton, null);
                    ViewCompat.setBackgroundTintList(tagButton,
                            ColorStateList.valueOf(getColor(R.color.app_blue)));

                    EditText tags = findViewById(R.id.tag);
                } else {
                    // Disable the filter when button is clicked while filter is visible
                    tagFilter.setVisibility(View.GONE);
                    // Reset ListView to default, unfiltered list of items
                    inventoryListView.setAdapter(inventoryListAdapter);
                }
            }
        });

    }

    // Define comparators for different sorting criteria
    private Comparator<InventoryItem> dateComparator = new Comparator<InventoryItem>() {
        @Override
        public int compare(InventoryItem item1, InventoryItem item2) {
            // Compare items by date
            return item1.getPurchaseDate().compareTo(item2.getPurchaseDate());
        }
    };

    private Comparator<InventoryItem> descriptionComparator = new Comparator<InventoryItem>() {
        @Override
        public int compare(InventoryItem item1, InventoryItem item2) {
            // Compare items by description
            return item1.getDescription().compareTo(item2.getDescription());
        }
    };

    private Comparator<InventoryItem> makeComparator = new Comparator<InventoryItem>() {
        @Override
        public int compare(InventoryItem item1, InventoryItem item2) {
            // Compare items by make
            return item1.getMake().compareTo(item2.getMake());
        }
    };

    private Comparator<InventoryItem> estimatedValueComparator = new Comparator<InventoryItem>() {
        @Override
        public int compare(InventoryItem item1, InventoryItem item2) {
            // Compare items by estimated value
            return Double.compare(item1.getEstimatedValue(), item2.getEstimatedValue());
        }
    };

    private void sortInventoryItems(Comparator<InventoryItem> comparator, boolean ascending) {
        if (ascending) {
            Collections.sort(inventoryItems, comparator);
        } else {
            // Reverse the order if sorting in descending order
            Collections.sort(inventoryItems, Collections.reverseOrder(comparator));
        }

        // Notify the adapter that the data has changed
        inventoryListAdapter.notifyDataSetChanged();
    }


    // Set the listener for the make button
//    makeButton.(v -> {
//        // Hide other filters here if they are part of MainActivity
//        // ...
//        makeFilterController.toggleMakeFilterVisibility();
//    });
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
                    runOnUiThread(() -> {
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
        ((TextView) findViewById(R.id.total_estimated_value)).setText(String.format(Locale.US, "%.2f", totalSum));
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

    // This method gathers the start and end date specified by the user when choosing to filter by
    // date and retrieves all entries within the inclusive range of these dates.


    // This method displays a DatePicker to the user when they attempt to set start and end dates
    // for the date filter. Then it calls dateFilterUpdate to retrieve the matching items.


    // This method determines gathers all the items that match the conditions of any of the filters
    // created by the user. It sets the ListView object to the resulting list of items for the
    // filter.
    private void filteredResults(Predicate<InventoryItem> condition) {
        // Filter the full list of items based on the provided conditions
        ArrayList<InventoryItem> filteredResults = inventoryItems.stream()
                .filter(filterCondition)
                .collect(Collectors.toCollection(ArrayList::new));
        // Create an adapter to set the ListView to, while maintaining the unfiltered ListView
        InventoryListAdapter filterListAdapter = new InventoryListAdapter(this, filteredResults);
        inventoryListView.setAdapter(filterListAdapter);
    }
}