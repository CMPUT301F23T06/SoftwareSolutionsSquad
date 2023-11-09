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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Controller for filtering inventory items by date range.
 */
public class DateFilterController {
    private EditText startDateEditText;
    private EditText endDateEditText;
    private TextView dateFilterButton;
    private TextView keywordFilterButton;
    private TextView makeFilterButton;
    private TextView tagFilterButton;
    private LinearLayout dateFilterLayout;
    private LinearLayout keywordFilterLayout;
    private LinearLayout makeFilterLayout;
    private LinearLayout tagFilterLayout;
    private Context context;
    private InventoryListAdapter inventoryListAdapter;
    private ListView inventoryListView;
    private Predicate<InventoryItem> filterCondition;
    private ArrayList<InventoryItem> inventoryItems;

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
                                ArrayList<InventoryItem> inventoryItems) {
        this.context = context;
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
        initializeDateFilter();
    }

    /**
     * Initializes date filter by setting up listeners for date input fields and buttons.
     */
    private void initializeDateFilter() {
        dateFilterButton.setOnClickListener(view -> toggleFilters());
        startDateEditText.setOnClickListener(view -> showDatePicker(startDateEditText, endDateEditText, true));
        endDateEditText.setOnClickListener(view -> showDatePicker(endDateEditText, startDateEditText, false));
    }

    /**
     * Toggles visibility of filter layouts and resets filter conditions.
     */
    private void toggleFilters() {
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
    private void showDatePicker(final EditText selectedDateEditText, final EditText otherDateEditText, final boolean isStartDate) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
            selectedDateEditText.setText(date.toString());
            if (isStartDate) {
                updateDateFilter(selectedDateEditText, otherDateEditText);
            } else {
                updateDateFilter(otherDateEditText, selectedDateEditText);
            }
        };

        LocalDate currentDate = LocalDate.now();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, currentDate.getYear(), currentDate.getMonthValue() - 1, currentDate.getDayOfMonth());
        datePickerDialog.show();
    }

    /**
     * Updates the list based on the selected date range.
     *
     * @param startDateEditText the EditText containing the start date
     * @param endDateEditText   the EditText containing the end date
     */
    private void updateDateFilter(EditText startDateEditText, EditText endDateEditText) {
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
    private Date convertStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return Date.from(LocalDate.parse(dateString).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Filters the inventory list based on the provided condition and updates the ListView.
     *
     * @param condition the condition to filter the list
     */
    private void displayFilteredResults(Predicate<InventoryItem> condition) {
        ArrayList<InventoryItem> filteredResults = inventoryItems.stream().filter(condition).collect(Collectors.toCollection(ArrayList::new));
        inventoryListView.setAdapter(new InventoryListAdapter(context, filteredResults));
    }

    /**
     * Helper method to set the background of a button to indicate it is active.
     *
     * @param button the button to update
     */
    private void setButtonActiveBackground(TextView button) {
        ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(context.getResources().getColor(R.color.app_blue, null)));
    }

    /**
     * Helper method to reset the background of a button to the default state.
     *
     * @param button the button to reset
     */
    private void resetButtonBackground(TextView button) {
        ViewCompat.setBackgroundTintList(button, null);
    }

    /**
     * Resets the ListView adapter to display all items and clears any filter conditions.
     */
    private void resetListViewAdapter() {
        inventoryListView.setAdapter(inventoryListAdapter);
    }

    /**
     * Sets default dates in the date filter fields when the filter is first activated.
     */
    private void setDefaultDates() {
        startDateEditText.setText("1900-01-01"); // Default start date
        endDateEditText.setText(LocalDate.now().toString()); // Current date as default end date
    }

    /**
     * Resets the backgrounds of all filter buttons to default state.
     */
    private void resetButtonBackgrounds() {
        resetButtonBackground(keywordFilterButton);
        resetButtonBackground(makeFilterButton);
        resetButtonBackground(tagFilterButton);
    }
}
