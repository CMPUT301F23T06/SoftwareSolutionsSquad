package com.example.softwaresolutionssquad.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.softwaresolutionssquad.R;
import com.example.softwaresolutionssquad.controllers.DatabaseController;
import com.example.softwaresolutionssquad.controllers.DateFilterController;
import com.example.softwaresolutionssquad.controllers.KeywordFilterController;
import com.example.softwaresolutionssquad.controllers.MakeFilterController;
import com.example.softwaresolutionssquad.controllers.TagFilterController;
import com.example.softwaresolutionssquad.controllers.SortController;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements  InventoryListAdapter.OnDeleteButtonShowListener  {
    private ListView inventoryListView;
    private ArrayList<InventoryItem> inventoryItems;
    private DatabaseController databaseController;
    private InventoryListAdapter inventoryListAdapter;
    private CollectionReference itemsRef;
    private Button deleteButton;

    private Context context;
    // Buttons used to enable filtering based on date, keyword, make, and tags
    private TextView dateButton, keywordButton, makeButton, tagButton;
    // Linear layouts containing necessary elements for filtering types
    private LinearLayout dateFilter, keyFilter, makeFilter, tagFilter;
    // Predicate for filters to use when determining which items match conditions
    private Predicate<InventoryItem> filterCondition;

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
        View view = inflater.inflate(R.layout.fragment_home_layout, container, false);
        itemsRef =  ((MainActivity)getActivity()).getDb().collection("Item");
        estimatedValue = view.findViewById(R.id.total_estimated_value);

        inventoryItems = new ArrayList<>();     // Initialize the ArrayList for inventory items


        // Initialize the custom adapter and assign it to the ListView
        inventoryListAdapter = new InventoryListAdapter(context, inventoryItems);
        inventoryListView = view.findViewById(R.id.inventory_list_view);
        inventoryListView.setAdapter(inventoryListAdapter);
        updateTotalValue();

        Spinner spinnerOrder = view.findViewById(R.id.spinner_order); // init Spinner to sort items
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.order_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrder.setAdapter(adapter);   // adapter for the spinner


        itemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("FireStore", error.toString());
                    return;
                } else {
                    inventoryItems.clear();
                    for (QueryDocumentSnapshot doc: value) {
                        Log.d("item", doc.getString("docId"));
                        inventoryItems.add(doc.toObject(InventoryItem.class));
                    }
                    updateTotalValue();
                    inventoryListAdapter.notifyDataSetChanged();
                }
            }
        });

        // Set the listener for when an item is selected in the Spinner
        spinnerOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            SortController sortController = new SortController(inventoryListAdapter, inventoryItems);

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                sortController.onItemSelected(position);
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

        // Buttons that trigger the date, keyword, make, and tag filters
        dateButton = view.findViewById(R.id.date_btn);
        keywordButton = view.findViewById(R.id.keyword_btn);
        makeButton = view.findViewById(R.id.make_btn);
        tagButton = view.findViewById(R.id.tag_btn);

        // Linear layouts that are shown when filter buttons are clicked
        dateFilter = view.findViewById(R.id.date_filter);
        keyFilter = view.findViewById(R.id.keyword_filter);
        makeFilter = view.findViewById(R.id.make_filter);
        tagFilter = view.findViewById(R.id.tag_filter);
        EditText startDate = view.findViewById(R.id.dateStart);
        EditText endDate = view.findViewById(R.id.dateEnd);
        EditText keywords = view.findViewById(R.id.keywords);


        // Allow user to filter all items in an inputted date range
        DateFilterController dateFilterController = new DateFilterController(
                context, // Context
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
                context, // context
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
        TextView makes = view.findViewById(R.id.make);
        MakeFilterController makeFilterController = new MakeFilterController(
                context,
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
        makeButton.setOnClickListener(v -> {
            // Hide other filters here if they are part of MainActivity
            dateFilter.setVisibility(View.GONE);
            keyFilter.setVisibility(View.GONE);
            tagFilter.setVisibility(View.GONE);
            // ...
            makeFilterController.toggleMakeFilterVisibility();
        });
        // Allow user to filter items based on tags associated with items
        TextView tags = view.findViewById(R.id.tag);
        TagFilterController tagFilterController = new TagFilterController(
                context,
                tags,
                tagFilter,
                keywordButton,
                dateButton,
                makeButton,
                tagButton,
                inventoryListAdapter,
                inventoryListView,
                inventoryItems
        );
        tagButton.setOnClickListener(v -> {
            // Hide other filters here if they are part of MainActivity
            dateFilter.setVisibility(View.GONE);
            keyFilter.setVisibility(View.GONE);
            makeFilter.setVisibility(View.GONE);
            // ...
            tagFilterController.toggleTagFilterVisibility();
        });
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
                    })
                    .addOnFailureListener(e -> Log.w("DeleteItem", "Error deleting document", e));
        }
    }

    // Method to update total estimated value of InventoryItems
    private void updateTotalValue() {
        double totalSum = inventoryItems.stream()
                .mapToDouble(InventoryItem::getEstimatedValue)
                .sum();
        estimatedValue.setText(String.format(Locale.US, "%.2f", totalSum));
    }
}