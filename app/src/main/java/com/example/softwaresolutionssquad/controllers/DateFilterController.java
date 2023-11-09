package com.example.softwaresolutionssquad.controllers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.example.softwaresolutionssquad.R;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DateFilterController {
    private EditText startDate, endDate;
    private TextView dateButton, keywordButton, makeButton, tagButton;

    private LinearLayout dateFilter, keyFilter, makeFilter, tagFilter;

    private Context context; // Context is needed for resources, etc.
    private InventoryListAdapter inventoryListAdapter;
    private ListView inventoryListView;

    private Predicate<InventoryItem> filterCondition;
    private ArrayList<InventoryItem> InventoryItems;

    public DateFilterController(Context context,
                                LinearLayout dateFilter,
                                EditText startDate,
                                EditText endDate,
                                TextView dateButton,
                                TextView keywordButton,
                                TextView makeButton,
                                TextView tagButton,
                                LinearLayout keyFilter,
                                LinearLayout makeFilter,
                                LinearLayout tagFilter,
                                InventoryListAdapter inventoryListAdapter,
                                ListView inventoryListView,
                                ArrayList<InventoryItem> InventoryItems
                                ) {
        this.context = context;
        this.dateFilter = dateFilter;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dateButton = dateButton;
        this.keywordButton = keywordButton;
        this.makeButton = makeButton;
        this.tagButton = tagButton;
        this.keyFilter = keyFilter;
        this.makeFilter = makeFilter;
        this.tagFilter = tagFilter;
        this.inventoryListAdapter = inventoryListAdapter;
        this.inventoryListView = inventoryListView;
        this.InventoryItems = InventoryItems;
        setupDateFilter();
    }

    private void setupDateFilter() {
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFilters();
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(startDate, endDate, true);
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(endDate, startDate, false);
            }
        });
    }

    private void toggleFilters() {
        // The actual implementation of your hide/show logic
        // Update this logic according to your requirements
        keyFilter.setVisibility(View.GONE);
        makeFilter.setVisibility(View.GONE);
        tagFilter.setVisibility(View.GONE);

        ViewCompat.setBackgroundTintList(keywordButton, null);
        ViewCompat.setBackgroundTintList(makeButton, null);
        ViewCompat.setBackgroundTintList(tagButton, null);
        ViewCompat.setBackgroundTintList(dateButton,
                ColorStateList.valueOf(getColor(R.color.app_blue)));

        if (dateFilter.getVisibility() == View.GONE) {
            // Show the LinearLayout with the necessary filtering elements
            dateFilter.setVisibility(View.VISIBLE);

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
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, cDate.getYear(), cDate.getMonthValue()-1, cDate.getDayOfMonth());
        datePickerDialog.show();
    }

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
    private int getColor(int colorId) {
        // Helper method to get color

        return context.getResources().getColor(colorId, null);
    }
    private void filteredResults(Predicate<InventoryItem> condition) {
        // Filter the full list of items based on the provided conditions
        ArrayList<InventoryItem> filteredResults = InventoryItems.stream()
                .filter(filterCondition)
                .collect(Collectors.toCollection(ArrayList::new));
        // Create an adapter to set the ListView to, while maintaining the unfiltered ListView
        InventoryListAdapter filterListAdapter = new InventoryListAdapter(context, filteredResults);
        inventoryListView.setAdapter(filterListAdapter);
    }

}
