package com.example.softwaresolutionssquad;

// Import statements for necessary Android components
import android.app.DatePickerDialog;
import android.content.Context;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddItemFragment extends Fragment {

    // Declare private member variables for UI elements
    private EditText edtPurchaseDate;
    private EditText edtDescription;
    private EditText edtMake;
    private EditText edtModel;
    private EditText edtSerialNumber;
    private EditText edtEstimatedValue;

    private EditText comment;
    private Button btnNext;
    private Button btnCancel;

    private InventoryItem currentItem;

    private OnNewItemSubmission listener;

    // Initialize a Calendar instance to manage dates
    final LocalDate currentDate = LocalDate.now();

    public AddItemFragment(){}

    // Static method to create a new instance of AddItemFragment
    public static AddItemFragment newInstance(InventoryItem item) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        args.putSerializable("item", item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentItem = (InventoryItem) getArguments().getSerializable("item");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        // Initialize UI elements
        edtPurchaseDate = view.findViewById(R.id.edtPurchaseDate);
        edtDescription = view.findViewById(R.id.edtDescription);
        edtMake = view.findViewById(R.id.edtMake);
        edtModel = view.findViewById(R.id.edtModel);
        edtSerialNumber = view.findViewById(R.id.edtSerialNumber);
        edtEstimatedValue = view.findViewById(R.id.editTextNumberDecimal);
        comment = view.findViewById(R.id.edtCommentTitle);
        btnNext = view.findViewById(R.id.btnNext);
        btnCancel = view.findViewById(R.id.btnCancel);


        // Set an onClickListener for the purchase date EditText to show a date picker
        edtPurchaseDate.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), date, currentDate.getYear(),
                    currentDate.getMonthValue(),
                    currentDate.getDayOfMonth()).show();
        });

        // Set an onClickListener for the Next button
        btnNext.setOnClickListener(v -> {
                // TODO: Logic for saving the data and transitioning to the next screen

                // Extract data from UI elements
                String date = edtPurchaseDate.getText().toString().trim();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Replace with your date format
                Date officialDate = null;
                try {
                    officialDate = dateFormat.parse(date);
                } catch (ParseException e) {
                    officialDate = null;
                }
                String description = edtDescription.getText().toString().trim();

                String make = edtMake.getText().toString().trim();
                String model = edtModel.getText().toString().trim();
                String serialNumber = edtSerialNumber.getText().toString().trim();
                String estimated_val = edtEstimatedValue.getText().toString().trim();

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
                String comm = comment.getText().toString().trim();
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
                    listener.onOKPressed(itemToSave);
                }

            // Close the fragment
            closeFragment();
        });

        // Set an onClickListener for the Cancel button
        btnCancel.setOnClickListener(v -> closeFragment());

        // Prepopulate fields if currentItem is not null (i.e., we're editing an existing item)
        if (currentItem != null) {
            prepopulateFields(currentItem);
        }

        return view;
    }

    private String retrieveDocId(InventoryItem currentItem) {
        if (currentItem != null) {
            // This means we're updating an existing item, so we should use the existing document ID.
            return currentItem.getDocId();
        } else {
            // This is a new item, so generate a new document ID.
            // The actual generation of the document ID will be handled by Firestore when we add the item.
            DocumentReference newDocRef = ((MainActivity)getActivity()).getDb().collection("Item").document();
            return newDocRef.getId();
        }
    }

    private void prepopulateFields(InventoryItem item) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        edtPurchaseDate.setText(sdf.format(item.getPurchaseDate()));
        edtDescription.setText(item.getDescription());
        edtMake.setText(item.getMake());
        edtModel.setText(item.getModel());
        edtSerialNumber.setText(item.getSerialNumber());
        edtEstimatedValue.setText(String.valueOf(item.getEstimatedValue()));
        comment.setText(item.getComment());
    }

    // Listener for date selection in the DatePickerDialog
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            LocalDate dateSet = LocalDate.of(year, monthOfYear, dayOfMonth);
            updateLabel(dateSet);
        }
    };

    private void updateLabel(LocalDate dateSet) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        edtPurchaseDate.setText(dateSet.format(dtf));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNewItemSubmission) {
            listener = (OnNewItemSubmission) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNewItemSubmission");
        }
    }

    private void closeFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(AddItemFragment.this).commit();
        fragmentManager.popBackStack();
        FrameLayout fragmentContainer = getActivity().findViewById(R.id.frag_container);
        fragmentContainer.setVisibility(View.GONE);
    }

    public interface OnNewItemSubmission {
        void onOKPressed(InventoryItem newItem);
        void onUpdatePressed(InventoryItem updatedItem);
    }
}
