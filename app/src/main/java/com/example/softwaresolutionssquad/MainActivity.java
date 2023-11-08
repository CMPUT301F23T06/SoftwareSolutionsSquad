package com.example.softwaresolutionssquad;

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

        // Allow user to filter all items in an inputted date range
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable all other filters that may be present
                keyFilter.setVisibility(View.GONE);
                makeFilter.setVisibility(View.GONE);
                tagFilter.setVisibility(View.GONE);

                ViewCompat.setBackgroundTintList(keywordButton, null);
                ViewCompat.setBackgroundTintList(makeButton, null);
                ViewCompat.setBackgroundTintList(tagButton, null);
                ViewCompat.setBackgroundTintList(dateButton,
                        ColorStateList.valueOf(getColor(R.color.app_blue)));

                // Enable filter if button is clicked while filter is not visible
                if (dateFilter.getVisibility() == View.GONE) {
                    // Show the LinearLayout with the necessary filtering elements
                    dateFilter.setVisibility(View.VISIBLE);

                    EditText startDate = findViewById(R.id.dateStart);
                    EditText endDate = findViewById(R.id.dateEnd);

                    // Reset ListView to default, unfiltered list of items
                    inventoryListView.setAdapter(inventoryListAdapter);
                    // Set default start and end date when filter is enabled
                    startDate.setText("1900-01-01");
                    endDate.setText(LocalDate.now().toString());

                    // Let the user select a start date for filtering
                    startDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDatePicker(startDate, endDate, true);
                        }
                    });

                    // Let the user select an end date for filtering
                    endDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDatePicker(endDate, startDate, false);
                        }
                    });
                } else {
                    // Disable the date filter if the button is clicked while filter if visible
                    dateFilter.setVisibility(View.GONE);
                    // Reset ListView to default, unfiltered list of items
                    inventoryListView.setAdapter(inventoryListAdapter);
                    ViewCompat.setBackgroundTintList(dateButton, null);
                }
            }
        });

        // Allow user to filter items based on the presence of keywords in description
        keywordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable all other filters that may be present
                dateFilter.setVisibility(View.GONE);
                makeFilter.setVisibility(View.GONE);
                tagFilter.setVisibility(View.GONE);

                // Enable filter if button is pressed while filters are not visible
                if (keyFilter.getVisibility() == View.GONE) {
                    // Show the LinearLayout with the necessary filtering elements
                    keyFilter.setVisibility(View.VISIBLE);

                    ViewCompat.setBackgroundTintList(dateButton, null);
                    ViewCompat.setBackgroundTintList(makeButton, null);
                    ViewCompat.setBackgroundTintList(tagButton, null);
                    ViewCompat.setBackgroundTintList(keywordButton,
                            ColorStateList.valueOf(getColor(R.color.app_blue)));

                    EditText keywords = findViewById(R.id.keywords);

                    // Reset ListView to default, unfiltered list of items
                    inventoryListView.setAdapter(inventoryListAdapter);
                    // Reset text box
                    keywords.setText("");

                    // Allow user to type in space-separated keywords and filter
                    keywords.addTextChangedListener(new TextWatcher() {
                        // Not used
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        // Not used
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}
                        // When text is entered into the text box, the ListView is updated with
                        // items that have matching keywords in descriptions
                        @Override
                        public void afterTextChanged(Editable s) {
                            // Users can enter multiple space-separated keywords to search for
                            String[] keywords = s.toString().split(" ");
                            // Create a predicate to filter all items by the specified keywords above
                            filterCondition = obj -> Arrays.stream(keywords).anyMatch(word ->
                                    obj.getDescription().toLowerCase().contains(word.toLowerCase()));
                            // Display the filtered list of items to the user
                            filteredResults(filterCondition);
                        }
                    });
                } else {
                    // Disable the filter when button is clicked while filter is visible
                    keyFilter.setVisibility(View.GONE);
                    // Reset ListView to default, unfiltered list of items
                    inventoryListView.setAdapter(inventoryListAdapter);

                    ViewCompat.setBackgroundTintList(keywordButton, null);
                }
            }
        });

        // Allow user to filter items based on specified makes
        makeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable all other filters that may be present
                keyFilter.setVisibility(View.GONE);
                dateFilter.setVisibility(View.GONE);
                tagFilter.setVisibility(View.GONE);

                // Enable filter if button is pressed while filters are not visible
                if (makeFilter.getVisibility() == View.GONE) {
                    // Show the LinearLayout with the necessary filtering elements
                    makeFilter.setVisibility(View.VISIBLE);

                    ViewCompat.setBackgroundTintList(keywordButton, null);
                    ViewCompat.setBackgroundTintList(dateButton, null);
                    ViewCompat.setBackgroundTintList(tagButton, null);
                    ViewCompat.setBackgroundTintList(makeButton,
                            ColorStateList.valueOf(getColor(R.color.app_blue)));

                    TextView makes = findViewById(R.id.make);

                    // Reset ListView to default, unfiltered list of items
                    inventoryListView.setAdapter(inventoryListAdapter);
                    // Clear the TextView
                    makes.setText("");
                    // Save all the makes that are present in the list of items
                    ArrayList<String> allMakesList = new ArrayList<>();
                    // Save all the indices of the items selected to filter by
                    ArrayList<Integer> selectedMakesIndices = new ArrayList<>();

                    // Allow user to select the make(s) they would like to filter by
                    makes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get every make present in the inventory
                            for (InventoryItem item: inventoryItems) {
                                String make = item.getMake().trim();
                                if (!allMakesList.contains(make)) {
                                    allMakesList.add(make);
                                }
                            }

                            // Convert ArrayList of makes to an Array to be used with setMultiChoiceItems()
                            String[] allMakesArray = allMakesList.toArray(new String[allMakesList.size()]);
                            // Store whether a make is selected or not
                            boolean[] selected = new boolean[allMakesArray.length];

                            // Create a multi-selection dialog for user to select makes
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Select Make(s)");
                            builder.setCancelable(false);
                            // Reset dialog to be fully unselected
                            selectedMakesIndices.clear();

                            builder.setMultiChoiceItems(allMakesArray, selected, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    // If the make is checked, save its index
                                    if (isChecked) {
                                        selectedMakesIndices.add(which);
                                        // If the make is unselected, do not save its index
                                    } else {
                                        selectedMakesIndices.remove(which);
                                    }
                                }
                            });

                            // Filter list of items when user confirms selection
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    ArrayList<String> selectedMakesList = new ArrayList<>();
                                    for (int i = 0; i < selectedMakesIndices.size(); i++) {
                                        // Get the names of the makes from the indices
                                        selectedMakesList.add(allMakesArray[selectedMakesIndices.get(i)]);
                                        // Attach the names of the selected makes to a string
                                        stringBuilder.append(allMakesArray[selectedMakesIndices.get(i)]);
                                        if (i != selectedMakesIndices.size() - 1) {
                                            stringBuilder.append(", ");
                                        }
                                    }
                                    // Display the selected makes in the dropdown bar
                                    makes.setText(stringBuilder.toString());

                                    // Create predicate to filter the full list of items by makes selected
                                    filterCondition = item -> selectedMakesList.contains(item.getMake());
                                    // Display the filtered list of items
                                    filteredResults(filterCondition);

                                }
                            });
                            // Remove dialog when cancelled
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.show();
                        }
                    });
                } else {
                    // Disable the filter when button is clicked while filter is visible
                    makeFilter.setVisibility(View.GONE);
                    // Reset ListView to default, unfiltered list of items
                    inventoryListView.setAdapter(inventoryListAdapter);
                }
            }
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
    private void dateFilterUpdate(EditText startDate, EditText endDate) {
        // Retrieve start and end dates from strings inside EditText objects
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        LocalDate startLD = LocalDate.parse(startDate.getText().toString());
        LocalDate endLD = LocalDate.parse(endDate.getText().toString());
        Date sDate = Date.from(startLD.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date eDate = Date.from(endLD.atTime(23,59,59)
                .toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now())));
        // Create predicate to filter results to an inclusive range of the start and end date
        filterCondition = obj -> !obj.getPurchaseDate().before(sDate) && !obj.getPurchaseDate().after(eDate);
        // Display the filtered list of items
        filteredResults(filterCondition);
    }

    // This method displays a DatePicker to the user when they attempt to set start and end dates
    // for the date filter. Then it calls dateFilterUpdate to retrieve the matching items.
    private void showDatePicker(final EditText changedDate, final EditText unchangedDate, final Boolean isStart) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Set the date selected to the EditText
                LocalDate date = LocalDate.of(year, month+1, dayOfMonth);
                changedDate.setText(date.toString());

                if (isStart) {
                    // Start date was changed
                    dateFilterUpdate(changedDate, unchangedDate);
                } else {
                    // End date was changed
                    dateFilterUpdate(unchangedDate, changedDate);
                }
            }
        };

        // Set the default date on the DatePicker
        LocalDate cDate = LocalDate.now();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, cDate.getYear(), cDate.getMonthValue()-1, cDate.getDayOfMonth());
        datePickerDialog.show();
    }

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