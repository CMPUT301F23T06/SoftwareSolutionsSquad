package com.example.softwaresolutionssquad;

// Import statements for necessary Android components
import android.app.DatePickerDialog;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private OnNewItemSubmission listener;

    // Initialize a Calendar instance to manage dates
    final Calendar calendar = Calendar.getInstance();

    // This is the constructor for the AddItemFragment class
    public AddItemFragment() {
        // Required empty public constructor
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
        edtPurchaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Set an onClickListener for the Next button
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                // Call the listener's method to notify the parent activity
                listener.onOKPressed(new InventoryItem(officialDate, description, make, model, serialNumber, official_estimated_value, comm));
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(AddItemFragment.this).commit();
                fragmentManager.popBackStack();
                FrameLayout fragmentContainer = getActivity().findViewById(R.id.frag_container);
                fragmentContainer.setVisibility(View.GONE);
            }
        });

        // Set an onClickListener for the Cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Logic for canceling the addition and going back
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(AddItemFragment.this).commit();
                fragmentManager.popBackStack();
                FrameLayout fragmentContainer = getActivity().findViewById(R.id.frag_container);
                fragmentContainer.setVisibility(View.GONE);
            }
        });

        return view;
    }

    // Listener for date selection in the DatePickerDialog
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    // Interface for communication with the parent activity
    public interface OnNewItemSubmission {
        void onOKPressed(InventoryItem item);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNewItemSubmission) {
            listener = (OnNewItemSubmission) context;
        } else {
            throw new RuntimeException(context.toString() + " OnFragmentInteractionListener is not implemented");
        }
    }

    // Helper method to update the date EditText with the selected date
    private void updateLabel() {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        edtPurchaseDate.setText(sdf.format(calendar.getTime()));
    }
}
