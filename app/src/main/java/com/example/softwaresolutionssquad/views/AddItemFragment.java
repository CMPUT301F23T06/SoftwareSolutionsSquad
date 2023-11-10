package com.example.softwaresolutionssquad.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A fragment for adding a new item to the inventory or editing an existing one.
 */
public class AddItemFragment extends Fragment {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String ITEM_KEY = "item";

    private EditText purchaseDateEditText;
    private EditText descriptionEditText;
    private EditText makeEditText;
    private EditText modelEditText;

    private CollectionReference itemsRef;
    private EditText serialNumberEditText;
    private EditText estimatedValueEditText;
    private EditText commentEditText;
    private Button nextButton;
    private Button cancelButton;
    private InventoryItem currentItem;
    private OnNewItemSubmission listener;
    private final LocalDate currentDate = LocalDate.now();

    public AddItemFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of AddItemFragment with an InventoryItem pre-populated if provided.
     * @param item InventoryItem to be edited, null if it's a new item.
     * @return A new instance of fragment AddItemFragment.
     */
    public static AddItemFragment newInstance(InventoryItem item) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ITEM_KEY, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentItem = (InventoryItem) getArguments().getSerializable(ITEM_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);
        initializeUiElements(view);
        setOnClickListeners();
        itemsRef =  ((MainActivity)getActivity()).getDb().collection("Item");
        // Initialize UI elements
        purchaseDateEditText = view.findViewById(R.id.edtPurchaseDate);
        descriptionEditText = view.findViewById(R.id.edtDescription);
        makeEditText = view.findViewById(R.id.edtMake);
        modelEditText = view.findViewById(R.id.edtModel);
        serialNumberEditText = view.findViewById(R.id.edtSerialNumber);
        estimatedValueEditText = view.findViewById(R.id.editTextNumberDecimal);
        commentEditText = view.findViewById(R.id.edtCommentTitle);
        nextButton = view.findViewById(R.id.btnNext);
        cancelButton = view.findViewById(R.id.btnCancel);


        // Set an onClickListener for the purchase date EditText to show a date picker
        purchaseDateEditText.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), dateSetListener, currentDate.getYear(),
                    currentDate.getMonthValue(),
                    currentDate.getDayOfMonth()).show();
        });

        // Set an onClickListener for the Next button
        nextButton.setOnClickListener(v -> {
                // TODO: Logic for saving the data and transitioning to the next screen

                // Extract data from UI elements
                String date = purchaseDateEditText.getText().toString().trim();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Replace with your date format
                Date officialDate = null;
                try {
                    officialDate = dateFormat.parse(date);
                } catch (ParseException e) {
                    officialDate = null;
                }
                String description = descriptionEditText.getText().toString().trim();

                String make = makeEditText.getText().toString().trim();
                String model = modelEditText.getText().toString().trim();
                String serialNumber = serialNumberEditText.getText().toString().trim();
                String estimated_val = estimatedValueEditText.getText().toString().trim();

                // Validate data
                if (officialDate == null || estimated_val.equals("")) { // Use .equals() for string comparison
                    // Show an alert dialog if validation fails
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Alert Title"); // Optional title
                    builder.setMessage("Please at least fill in the value and date!");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                }
                Double official_estimated_value = Double.parseDouble(estimated_val);
                String comm = commentEditText.getText().toString().trim();
                String documentID = retrieveDocId(currentItem);

                InventoryItem itemToSave;
                if(currentItem != null) {
                    // Update the existing item's properties
                    currentItem.setPurchaseDate(officialDate);
                    currentItem.setDescription(description);
                    currentItem.setMake(make);
                    currentItem.setModel(model);
                    currentItem.setSerialNumber(serialNumber);
                    currentItem.setEstimatedValue(official_estimated_value);
                    currentItem.setComment(comm);
                    currentItem.setDocId(documentID);
                    itemToSave = currentItem;
                    listener.onUpdatePressed(itemToSave);
                } else {
                    // It's a new item
                    itemToSave = new InventoryItem(officialDate, description, make, model, serialNumber, official_estimated_value, comm, documentID);
                    createNewItem(itemToSave);
                }

            // Close the fragment
            // Show the HomeFragment
            if (getActivity() instanceof MainActivity) {
                HomeFragment homeFragment = new HomeFragment();
                ((MainActivity) getActivity()).setFragment(homeFragment);
            }
        });

        // Set an onClickListener for the Cancel button
        cancelButton.setOnClickListener(v -> {


            // Show the HomeFragment
            if (getActivity() instanceof MainActivity) {
                HomeFragment homeFragment = new HomeFragment();
                ((MainActivity) getActivity()).setFragment(homeFragment);
            }
        });

        // Prepopulate fields if currentItem is not null (i.e., we're editing an existing item)
        if (currentItem != null) {
            prepopulateFields(currentItem);
        }
        return view;
    }

    /**
     * Initializes the UI elements of the fragment.
     * @param view The inflated view of the fragment.
     */
    private void initializeUiElements(View view) {
        purchaseDateEditText = view.findViewById(R.id.edtPurchaseDate);
        descriptionEditText = view.findViewById(R.id.edtDescription);
        makeEditText = view.findViewById(R.id.edtMake);
        modelEditText = view.findViewById(R.id.edtModel);
        serialNumberEditText = view.findViewById(R.id.edtSerialNumber);
        estimatedValueEditText = view.findViewById(R.id.editTextNumberDecimal);
        commentEditText = view.findViewById(R.id.edtCommentTitle);
        nextButton = view.findViewById(R.id.btnNext);
        cancelButton = view.findViewById(R.id.btnCancel);
    }

    /**
     * Sets on click listeners for interactive UI elements.
     */
    private void setOnClickListeners() {
        purchaseDateEditText.setOnClickListener(v -> showDatePicker());
        nextButton.setOnClickListener(v -> saveItem());
    }

    /**
     * Shows a date picker dialog for selecting a purchase date.
     */
    private void showDatePicker() {
        new DatePickerDialog(getContext(), dateSetListener, currentDate.getYear(),
                currentDate.getMonthValue() - 1, // Month is 0-indexed in DatePickerDialog
                currentDate.getDayOfMonth()).show();
    }

    /**
     * Saves the item to the inventory, either by creating a new entry or updating an existing one.
     */
    private void saveItem() {
        String date = purchaseDateEditText.getText().toString().trim();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        Date officialDate = null;
        try {
            officialDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // Handle the error state
        }

        if (officialDate == null || estimatedValueEditText.getText().toString().trim().isEmpty()) {
            showAlertDialog("Error", "Please fill in the value and date!");
            return;
        }

        Double estimatedValue = Double.parseDouble(estimatedValueEditText.getText().toString().trim());
        InventoryItem itemToSave = createOrUpdateItem(officialDate, estimatedValue);
        if (itemToSave != null) {
            if (currentItem != null) {
                listener.onUpdatePressed(itemToSave);
            } else {
                createNewItem(itemToSave);
            }
        }
    }

    /**
     * Shows an alert dialog with a specified title and message.
     * @param title   The title of the alert dialog.
     * @param message The message of the alert dialog.
     */
    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Creates or updates an InventoryItem with the provided details.
     * @param officialDate    The official purchase date of the item.
     * @param estimatedValue  The estimated value of the item.
     * @return The created or updated InventoryItem.
     */
    private InventoryItem createOrUpdateItem(Date officialDate, double estimatedValue) {
        String description = descriptionEditText.getText().toString().trim();
        String make = makeEditText.getText().toString().trim();
        String model = modelEditText.getText().toString().trim();
        String serialNumber = serialNumberEditText.getText().toString().trim();
        String comment = commentEditText.getText().toString().trim();
        String documentID = retrieveDocId(currentItem);

        if (currentItem != null) {
            // Update the existing item's properties
            currentItem.setPurchaseDate(officialDate);
            currentItem.setDescription(description);
            currentItem.setMake(make);
            currentItem.setModel(model);
            currentItem.setSerialNumber(serialNumber);
            currentItem.setEstimatedValue(estimatedValue);
            currentItem.setComment(comment);
            currentItem.setDocId(documentID);
            return currentItem;
        } else {
            // It's a new item
            return new InventoryItem(officialDate, description, make, model, serialNumber, estimatedValue, comment, new ArrayList<String>(), documentID);
        }
    }

    private String retrieveDocId(InventoryItem item) {
        if (item != null) {
            return item.getDocId();
        } else {
            DocumentReference newDocRef = ((MainActivity)getActivity()).getDb().collection("Item").document();
            return newDocRef.getId();
        }
    }

    private void prepopulateFields(InventoryItem item) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        purchaseDateEditText.setText(sdf.format(item.getPurchaseDate()));
        descriptionEditText.setText(item.getDescription());
        makeEditText.setText(item.getMake());
        modelEditText.setText(item.getModel());
        serialNumberEditText.setText(item.getSerialNumber());
        estimatedValueEditText.setText(String.valueOf(item.getEstimatedValue()));
        commentEditText.setText(item.getComment());
    }

    private final DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
        LocalDate dateSet = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
        updateLabel(dateSet);
    };

    private void updateLabel(LocalDate dateSet) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        purchaseDateEditText.setText(dateSet.format(dtf));
    }



    public void createNewItem(InventoryItem newItem) {
        // Get a new document reference from Firestore, which has an auto-generated ID
        DocumentReference newDocRef = itemsRef.document();

        // Set the document ID inside the new item object
        newItem.setDocId(newDocRef.getId()); // Make sure InventoryItem has a method to set its ID

        // Set the new item in the Firestore document
        newDocRef.set(newItem)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AddItem", "DocumentSnapshot written with ID: " + newDocRef.getId());
                });
    }


    /**
     * Interface for communicating with the activity when an item is saved.
     */
    public interface OnNewItemSubmission {
        void onUpdatePressed(InventoryItem updatedItem);
    }
    public void setListener(OnNewItemSubmission listener) {
        this.listener = listener;
    }
}
