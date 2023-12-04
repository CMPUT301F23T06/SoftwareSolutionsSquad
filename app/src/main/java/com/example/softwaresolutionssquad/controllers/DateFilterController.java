package com.example.softwaresolutionssquad.controllers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.example.softwaresolutionssquad.controllers.SortController;

/**
 * Controller for filtering inventory items by date range.
 */
public class DateFilterController {
    public final EditText startDateEditText;
    public final EditText endDateEditText;
    public final TextView dateFilterButton;
    public final Spinner spinnerOrder;
    public final ImageView sortordericon;
    public final TextView keywordFilterButton;
    public final TextView makeFilterButton;
    public final TextView tagFilterButton;
    public final LinearLayout dateFilterLayout;
    public final LinearLayout keywordFilterLayout;
    public final LinearLayout makeFilterLayout;
    public final LinearLayout tagFilterLayout;
    public final Context context;
    public final InventoryListAdapter inventoryListAdapter;
    public final ListView inventoryListView;
    public Predicate<InventoryItem> filterCondition;
    public final ArrayList<InventoryItem> inventoryItems;

    private TextView estimatedValue;

    private boolean isAscendingOrder = true;

    /**
     * Constructs a new DateFilterController.
     *
     * @param context              the current context
     * @param dateFilterLayout     layout containing date filter controls
     * @param startDateEditText    text field for start date input
     * @param endDateEditText      text field for end date input
     * @param dateFilterButton     button to apply date filter
     * @param keywordFilterButton  button to apply keyword filter
     * @param makeFilterButton     button to apply make filter
     * @param tagFilterButton      button to apply tag filter
     * @param keywordFilterLayout  layout containing keyword filter controls
     * @param makeFilterLayout     layout containing make filter controls
     * @param tagFilterLayout      layout containing tag filter controls
     * @param inventoryListAdapter adapter for the inventory list
     * @param inventoryListView    view representing the list of inventory items
     * @param inventoryItems       list of all inventory items
     */
    public DateFilterController(Context context,
                                LinearLayout dateFilterLayout,
                                TextView estimatedValue,
                                EditText startDateEditText,
                                EditText endDateEditText,
                                TextView dateFilterButton,
                                TextView keywordFilterButton,
                                TextView makeFilterButton,
                                TextView tagFilterButton,
                                LinearLayout keywordFilterLayout,
                                LinearLayout makeFilterLayout,
                                LinearLayout tagFilterLayout,
                                InventoryListAdapter inventoryListAdapter,
                                ListView inventoryListView,
                                ArrayList<InventoryItem> inventoryItems,
                                Spinner spinnerOrder,
                                ImageView sortOrderIcon) {
        this.context = context;
        this.estimatedValue = estimatedValue;
        this.dateFilterLayout = dateFilterLayout;
        this.startDateEditText = startDateEditText;
        this.endDateEditText = endDateEditText;
        this.dateFilterButton = dateFilterButton;
        this.keywordFilterButton = keywordFilterButton;
        this.makeFilterButton = makeFilterButton;
        this.tagFilterButton = tagFilterButton;
        this.keywordFilterLayout = keywordFilterLayout;
        this.makeFilterLayout = makeFilterLayout;
        this.tagFilterLayout = tagFilterLayout;
        this.inventoryListAdapter = inventoryListAdapter;
        this.inventoryListView = inventoryListView;
        this.inventoryItems = inventoryItems;
        this.spinnerOrder = spinnerOrder;
        this.sortordericon = sortOrderIcon;
        initializeDateFilter();
    }

    /**
     * Initializes date filter by setting up listeners for date input fields and buttons.
     */
    public void initializeDateFilter() {
        dateFilterButton.setOnClickListener(view -> toggleFilters());
        startDateEditText.setOnClickListener(view -> showDatePicker(startDateEditText, endDateEditText, true));
        endDateEditText.setOnClickListener(view -> showDatePicker(endDateEditText, startDateEditText, false));
    }

    private void updateTotalValue(InventoryListAdapter items) {
        double totalSum = items.getItems().stream()
                .mapToDouble(InventoryItem::getEstimatedValue)
                .sum();
        estimatedValue.setText(String.format(Locale.US, "$ %.2f", totalSum));
    }



    /**
     * Toggles visibility of filter layouts and resets filter conditions.
     */
    public void toggleFilters() {
        // Hide other filters when date filter is active
        keywordFilterLayout.setVisibility(View.GONE);
        makeFilterLayout.setVisibility(View.GONE);
        tagFilterLayout.setVisibility(View.GONE);
        resetButtonBackgrounds();

        // Toggle the visibility of the date filter layout
        if (dateFilterLayout.getVisibility() == View.GONE) {
            dateFilterLayout.setVisibility(View.VISIBLE);
            setButtonActiveBackground(dateFilterButton);
            resetListViewAdapter();
            setDefaultDates();
        } else {
            dateFilterLayout.setVisibility(View.GONE);
            resetListViewAdapter();
            resetButtonBackground(dateFilterButton);
        }
    }



    /**
     * Displays date picker dialog and updates date fields accordingly.
     *
     * @param selectedDateEditText the EditText to update with the selected date
     * @param otherDateEditText    the other date EditText that is not being updated
     * @param isStartDate          flag to indicate if the start date is being set
     */
    public void showDatePicker(final EditText selectedDateEditText, final EditText otherDateEditText, final boolean isStartDate) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
            selectedDateEditText.setText(date.toString());
            if (isStartDate) {
                updateDateFilter(selectedDateEditText, otherDateEditText);
            } else {
                updateDateFilter(otherDateEditText, selectedDateEditText);
            }
        };

        LocalDate setDate = LocalDate.parse(selectedDateEditText.getText());
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.DatePickerDialogTheme, dateSetListener, setDate.getYear(), setDate.getMonthValue() - 1, setDate.getDayOfMonth());
        Instant otherDateInstant = LocalDate.parse(otherDateEditText.getText()).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant currentDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();

        if (isStartDate) {
            datePickerDialog.getDatePicker().setMaxDate(otherDateInstant.toEpochMilli());
        } else {
            datePickerDialog.getDatePicker().setMinDate(otherDateInstant.toEpochMilli());
            datePickerDialog.getDatePicker().setMaxDate(currentDate.toEpochMilli());
        }

        datePickerDialog.show();
    }

    /**
     * Updates the list based on the selected date range.
     *
     * @param startDateEditText the EditText containing the start date
     * @param endDateEditText   the EditText containing the end date
     */
    public void updateDateFilter(EditText startDateEditText, EditText endDateEditText) {
        Date startDate = convertStringToDate(startDateEditText.getText().toString());
        Date endDate = convertStringToDate(endDateEditText.getText().toString());
        filterCondition = item -> !item.getPurchaseDate().before(startDate) && !item.getPurchaseDate().after(endDate);
        displayFilteredResults(filterCondition);
    }

    /**
     * Helper method to convert a date string to a Date object.
     *
     * @param dateString the date string to convert
     * @return the Date object
     */
    public Date convertStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return Date.from(LocalDate.parse(dateString).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Filters the inventory list based on the provided condition and updates the ListView.
     *
     * @param condition the condition to filter the list
     */
    public void displayFilteredResults(Predicate<InventoryItem> condition) {
        ArrayList<InventoryItem> filteredResults = inventoryItems.stream().filter(condition).collect(Collectors.toCollection(ArrayList::new));
        inventoryListAdapter.updateItems(filteredResults);
        updateTotalValue(inventoryListAdapter);
        SortController sortController = new SortController(inventoryListAdapter, filteredResults);
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

        sortordericon.setOnClickListener(v -> {
            // Toggle the sort order
            isAscendingOrder = !isAscendingOrder;

            // Perform sorting with the current criterion and order
            int criterionPosition = spinnerOrder.getSelectedItemPosition();
            sortController.onItemSelected(getSortPositionFromSpinner(criterionPosition));
        });
    }

    // Helper method to map spinner position to SortController position
    private int getSortPositionFromSpinner(int spinnerPosition) {
        // Map spinner position to SortController's expected position
        // Assuming the spinner positions align with the SortController cases
        return isAscendingOrder ? spinnerPosition * 2 : spinnerPosition * 2 + 1;
    }

    /**
     * Helper method to set the background of a button to indicate it is active.
     *
     * @param button the button to update
     */
    public void setButtonActiveBackground(TextView button) {
        button.setTextColor(Color.WHITE);
        ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(context.getResources().getColor(R.color.app_blue, null)));
    }

    /**
     * Helper method to reset the background of a button to the default state.
     *
     * @param button the button to reset
     */
    public void resetButtonBackground(TextView button) {
        button.setTextColor(Color.BLACK);
        ViewCompat.setBackgroundTintList(button, null);
    }


    /**
     * Resets the ListView adapter to display all items and clears any filter conditions.
     */
    public void resetListViewAdapter() {

        inventoryListAdapter.resetItems();
        updateTotalValue(inventoryListAdapter);
    }

    /**
     * Sets default dates in the date filter fields when the filter is first activated.
     */
    public void setDefaultDates() {
        startDateEditText.setText("1900-01-01"); // Default start date
        endDateEditText.setText(LocalDate.now().toString()); // Current date as default end date
    }

    /**
     * Resets the backgrounds of all filter buttons to default state.
     */
    public void resetButtonBackgrounds() {
        resetButtonBackground(keywordFilterButton);
        resetButtonBackground(makeFilterButton);
        resetButtonBackground(tagFilterButton);
    }
}
