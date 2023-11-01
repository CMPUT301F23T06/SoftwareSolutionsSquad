package com.example.softwaresolutionssquad;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddItemFragment extends Fragment {

    private EditText edtPurchaseDate;
    private EditText edtDescription;
    private EditText edtMake;
    private EditText edtModel;
    private EditText edtSerialNumber;
    private EditText edtEstimatedValue;
    private Button btnNext;
    private Button btnCancel;

    final Calendar calendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        edtPurchaseDate = view.findViewById(R.id.edtPurchaseDate);
        edtDescription = view.findViewById(R.id.edtDescription);
        edtMake = view.findViewById(R.id.edtMake);
        edtModel = view.findViewById(R.id.edtModel);
        edtSerialNumber = view.findViewById(R.id.edtSerialNumber);
        edtEstimatedValue = view.findViewById(R.id.edtEstimatedValue);
        btnNext = view.findViewById(R.id.btnNext);
        btnCancel = view.findViewById(R.id.btnCancel);

        // Date picker for purchase date
        edtPurchaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Next button click event
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Logic for saving the data and transitioning to the next screen
            }
        });

        // Cancel button click event
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Logic for canceling the addition and going back
            }
        });

        return view;
    }

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

    private void updateLabel() {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        edtPurchaseDate.setText(sdf.format(calendar.getTime()));
    }
}
