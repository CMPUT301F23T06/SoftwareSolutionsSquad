package com.example.softwaresolutionssquad.views;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.softwaresolutionssquad.R;
import com.example.softwaresolutionssquad.controllers.DateFilterController;
import com.example.softwaresolutionssquad.controllers.KeywordFilterController;
import com.example.softwaresolutionssquad.controllers.MakeFilterController;
import com.example.softwaresolutionssquad.controllers.TagFilterController;
import com.example.softwaresolutionssquad.controllers.SortController;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class HomeFragment extends Fragment implements InventoryListAdapter.OnCheckedItemShowButtonsListener, AddItemTagFragment.OnFragmentInteractionListener {
    private ListView inventoryListView;
    private ArrayList<InventoryItem> inventoryItems;
    private InventoryListAdapter inventoryListAdapter;
    private CollectionReference itemsRef;
    private Button deleteButton;
    private ProgressBar loadingSpinner;
    private final ArrayList<String> set_of_checked_tags = new ArrayList<>();

    private TextView welcomeTextView;

    private Button tagBtn;
    private LinearLayout buttonsLayout;
    private Context context;
    // Buttons used to enable filtering based on date, keyword, make, and tags
    private TextView dateButton, keywordButton, makeButton, tagButton;
    // Linear layouts containing necessary elements for filtering types
    private LinearLayout dateFilter, keyFilter, makeFilter, tagFilter;
    // Predicate for filters to use when determining which items match conditions
    private Predicate<InventoryItem> filterCondition;

    private TextView estimatedValue;

    public View view;

<<<<<<< HEAD
=======
    /**
     * Required empty constructor for instantiating the fragment.
     */
>>>>>>> main
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Called when the fragment is first attached to its context. Sets up the context field.
     * @param context The context to which the fragment is attached.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    // Member to keep track of current sort order, default is true for ascending
    private boolean isAscendingOrder = true;

    /**
     * Creates and returns the view hierarchy associated with the fragment. Initializes UI components and sets up interactions.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_layout, container, false);
        String username = ((MyApp) requireActivity().getApplication()).getUserViewModel().getUsername();

        welcomeTextView = view.findViewById(R.id.Welcome);
        welcomeTextView.setText(String.format("Welcome, %s!", username));


        itemsRef =  ((MainActivity)getActivity()).getDb().collection("Item");
        estimatedValue = view.findViewById(R.id.total_estimated_value);

        loadingSpinner = view.findViewById(R.id.loading_spinner);
        loadingSpinner.setVisibility(View.VISIBLE);

        inventoryItems = new ArrayList<>();     // Initialize the ArrayList for inventory items


        // Initialize the custom adapter and assign it to the ListView
        inventoryListAdapter = new InventoryListAdapter(context, inventoryItems);
        inventoryListView = view.findViewById(R.id.inventory_list_view);
        inventoryListView.setAdapter(inventoryListAdapter);

        buttonsLayout = view.findViewById(R.id.buttons_layout); // Assign ID to your LinearLayout


        Spinner spinnerOrder = view.findViewById(R.id.spinner_order); // init Spinner to sort items
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.order_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrder.setAdapter(adapter);   // adapter for the spinner


        itemsRef.whereEqualTo("username", username).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("FireStore", error.toString());
                    loadingSpinner.setVisibility(View.GONE); // Hide spinner on error
                } else {
                    inventoryItems.clear();
                    ArrayList<InventoryItem> origData = new ArrayList<>();
                    for (QueryDocumentSnapshot doc: value) {
                        origData.add(doc.toObject(InventoryItem.class));
                        inventoryItems.add(doc.toObject(InventoryItem.class));
                    }
                    inventoryListAdapter.addToOriginal(origData);
                    inventoryListAdapter.notifyDataSetChanged();
                    updateTotalValue();
                    loadingSpinner.setVisibility(View.GONE); // Hide spinner after loading data
                }
            }
        });



<<<<<<< HEAD
        SortController sortController = new SortController(inventoryListAdapter, inventoryItems, true);
=======
        SortController sortController = new SortController(inventoryListAdapter, inventoryItems);
>>>>>>> main

        // Set the listener for when an item is selected in the Spinner
        spinnerOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                sortController.onItemSelected(position*2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // If no item is selected, no action is needed
            }
        });

        ImageView SortOrderIcon = view.findViewById(R.id.sort_view); // Replace with your actual ImageView ID

        // OnClickListener for sort icon
        SortOrderIcon.setOnClickListener(v -> {
            // Toggle the sort order
            isAscendingOrder = !isAscendingOrder;

            // Perform sorting with the current criterion and order
            int criterionPosition = spinnerOrder.getSelectedItemPosition();
            sortController.onItemSelected(getSortPositionFromSpinner(criterionPosition));
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
        tagBtn = view.findViewById(R.id.add_tags_button);

        tagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the deletion logic here
                AddItemTagFragment addItemTagFragment = new AddItemTagFragment();
                addItemTagFragment.setListener(HomeFragment.this);
                addItemTagFragment.show(getActivity().getSupportFragmentManager(), "ADD_ITEM_TAG");
            }
        });

        deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedItems();
            }
        });

        inventoryListAdapter.setOnButtonsShowListener(this);       // Set listener on adapter

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


        // Allow user to filter all items in an inputted date range. we only need instantiation because it helps with onclick
        new DateFilterController(
                context, // Context
                dateFilter,
                estimatedValue,
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
                inventoryItems, // The data list
                spinnerOrder,
<<<<<<< HEAD
                SortOrderIcon,
                sortController
=======
                SortOrderIcon
>>>>>>> main
        );

        new KeywordFilterController(
                context, // context
                keyFilter,
                keywords,
                estimatedValue,
                keywordButton,
                dateButton,
                makeButton,
                tagButton,
                dateFilter,
                makeFilter,
                tagFilter,
                inventoryListAdapter,
                inventoryListView,
                inventoryItems,
                spinnerOrder,
<<<<<<< HEAD
                SortOrderIcon,
                sortController
=======
                SortOrderIcon
>>>>>>> main
        );
        // Allow user to filter items based on the presence of keywords in description
        TextView makes = view.findViewById(R.id.make);
        MakeFilterController makeFilterController = new MakeFilterController(
                context,
                estimatedValue,
                makes,
                makeFilter,
                keywordButton,
                dateButton,
                makeButton,
                tagButton,
                inventoryListAdapter,
                inventoryListView,
                inventoryItems,
                sortController
        );
        // Allow user to filter items based on specified makes
        makeButton.setOnClickListener(v -> {
            // Hide other filters here if they are part of MainActivity
            dateFilter.setVisibility(View.GONE);
            keyFilter.setVisibility(View.GONE);
            tagFilter.setVisibility(View.GONE);
            makeFilterController.toggleMakeFilterVisibility();
        });
        // Allow user to filter items based on tags associated with items
        TextView tags = view.findViewById(R.id.tag);
        TagFilterController tagFilterController = new TagFilterController(
                context,
                tags,
                estimatedValue,
                tagFilter,
                keywordButton,
                dateButton,
                makeButton,
                tagButton,
                inventoryListAdapter,
                inventoryListView,
                inventoryItems,
                sortController
        );
        tagButton.setOnClickListener(v -> {
            // Hide other filters here if they are part of MainActivity
            dateFilter.setVisibility(View.GONE);
            keyFilter.setVisibility(View.GONE);
            makeFilter.setVisibility(View.GONE);
            tagFilterController.toggleTagFilterVisibility();
        });

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Maps the spinner position to the corresponding position in the SortController.
     * @param spinnerPosition The position selected in the spinner.
     * @return The corresponding position for sorting.
     */
    private int getSortPositionFromSpinner(int spinnerPosition) {
        // Map spinner position to SortController's expected position
        // Assuming the spinner positions align with the SortController cases
        return isAscendingOrder ? spinnerPosition * 2 : spinnerPosition * 2 + 1;
    }

    /**
     * Controls the visibility of delete button based on the selection state of items.
     */
    public void showButtonsIfNeeded() {
        buttonsLayout.setVisibility(inventoryItems.stream().anyMatch(InventoryItem::getSelected) ? View.VISIBLE : View.GONE);
    }

    /**
     * Deletes the selected items from the Firestore and updates the UI accordingly.
     */
    private void deleteSelectedItems() {
//        getlatestListOfItems();
        // Collect all items that are marked for deletion
        List<InventoryItem> itemsToRemove = inventoryListAdapter.getItems().stream()
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
        buttonsLayout.setVisibility(View.GONE);
        HomeFragment homeFragment = new HomeFragment();
        ((MainActivity) getActivity()).setFragment(homeFragment);
        Toast.makeText(getActivity(), "Item(s) deleted successfully!", Toast.LENGTH_SHORT).show();
<<<<<<< HEAD
//        inventoryListView.setAdapter(inventoryListAdapter);


=======
>>>>>>> main
    }

    /**
     * Updates the total estimated value of the items and displays it in the UI.
     */
    private void updateTotalValue() {
<<<<<<< HEAD
//        getlatestListOfItems();
=======
>>>>>>> main
        double totalSum = inventoryListAdapter.getItems().stream()
                .mapToDouble(InventoryItem::getEstimatedValue)
                .sum();
        estimatedValue.setText(String.format(Locale.US, "$ %.2f", totalSum));
    }

    /***
     * Takes the selected tags, finds the selected items and adds each tag to each selected item
     * @param selectedTags
     */
    @Override
    public void onOkPressed(ArrayList<String> selectedTags) {
        List<InventoryItem> itemsToAddTagsTo = inventoryListAdapter.getItems().stream()
                .filter(InventoryItem::getSelected)
                .collect(Collectors.toList());

        itemsToAddTagsTo.forEach(item -> {
            boolean isUpdated = false;
            item.setSelected(false);
            for (String tag : selectedTags) {
                if (!item.getTags().contains(tag)) {
                    item.addTag(tag);
                    isUpdated = true;
                }
            }

            if (isUpdated) {
                updateItemInFirestore(item);
            }
        });
        HomeFragment homeFragment = new HomeFragment();
        ((MainActivity) getActivity()).setFragment(homeFragment);
        Toast.makeText(getActivity(), "Tag[s) added successfully!", Toast.LENGTH_SHORT).show();
    }

    /***
     * Makes sure to update one item at a time
     * @param item
     */
    private void updateItemInFirestore(InventoryItem item) {
        itemsRef.document(item.getDocId()).set(item)
                .addOnSuccessListener(aVoid -> Log.d("UpdateItem", "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w("UpdateItem", "Error updating document", e));
    }

    public void getlatestListOfItems() {
        int itemCount = inventoryListAdapter.getCount();
        ArrayList<InventoryItem> items = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            InventoryItem item = inventoryListAdapter.getItem(i);
            items.add(item);
        }
        this.inventoryItems = items;
    }
<<<<<<< HEAD

=======
>>>>>>> main
}
