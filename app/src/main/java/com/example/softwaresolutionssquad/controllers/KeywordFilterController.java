package com.example.softwaresolutionssquad.controllers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Controller for managing keyword filters in the inventory list.
 */
public class KeywordFilterController {
    private final EditText keywords;
    private final TextView keywordButton;
    public final Spinner spinnerOrder;
    public final ImageView sortordericon;
    private final TextView dateButton;
    private final TextView makeButton;
    private final TextView tagButton;
    private final LinearLayout keyFilter;
    private final LinearLayout dateFilter;
    private final LinearLayout makeFilter;
    private final LinearLayout tagFilter;
    private final Context context;
    private final InventoryListAdapter inventoryListAdapter;
    private final ListView inventoryListView;
    private ArrayList<InventoryItem> inventoryItems;

    private final SortController sortController;

    private final TextView estimatedValue;


    /**
     * Constructs a KeywordFilterController.
     *
     * @param context                the application context
     * @param keyFilter              the keyword filter layout
     * @param keywords               the EditText for keyword input
     * @param keywordButton          the button to activate keyword filtering
     * @param dateButton             the button to activate date filtering
     * @param makeButton             the button to activate make filtering
     * @param tagButton              the button to activate tag filtering
     * @param dateFilter             the date filter layout
     * @param makeFilter             the make filter layout
     * @param tagFilter              the tag filter layout
     * @param inventoryListAdapter   the adapter for the inventory list
     * @param inventoryListView      the ListView for inventory display
     * @param inventoryItems         the list of inventory items
     * @param sortController         sortController for sorting
     */
    public KeywordFilterController(Context context,
                                   LinearLayout keyFilter,
                                   EditText keywords,
                                   TextView estimatedValue,
                                   TextView keywordButton,
                                   TextView dateButton,
                                   TextView makeButton,
                                   TextView tagButton,
                                   LinearLayout dateFilter,
                                   LinearLayout makeFilter,
                                   LinearLayout tagFilter,
                                   InventoryListAdapter inventoryListAdapter,
                                   ListView inventoryListView,
                                   ArrayList<InventoryItem> inventoryItems,
                                   Spinner spinnerOrder,
                                   ImageView sortOrderIcon,
                                   SortController sortController
    ) {
        this.context = context;
        this.keyFilter = keyFilter;
        this.keywords = keywords;
        this.keywordButton = keywordButton;
        this.dateButton = dateButton;
        this.makeButton = makeButton;
        this.tagButton = tagButton;
        this.dateFilter = dateFilter;
        this.makeFilter = makeFilter;
        this.tagFilter = tagFilter;
        this.inventoryListAdapter = inventoryListAdapter;
        this.inventoryListView = inventoryListView;
        this.inventoryItems = inventoryItems;
        this.spinnerOrder = spinnerOrder;
        this.sortordericon = sortOrderIcon;
        this.estimatedValue = estimatedValue;
        this.sortController = sortController;
        setupKeywordFilter();
    }

    /**
     * Initializes the keyword filter functionality.
     */
    private void setupKeywordFilter() {
        keywordButton.setOnClickListener(v -> toggleFilters());
        keywords.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                applyKeywordFilter(s.toString());
            }
        });
    }

    private void updateTotalValue(InventoryListAdapter items) {
        double totalSum = items.getItems().stream()
                .mapToDouble(InventoryItem::getEstimatedValue)
                .sum();
        estimatedValue.setText(String.format(Locale.US, "$ %.2f", totalSum));
    }

    /**
     * Toggles the visibility of keyword filter and resets other filters.
     */
    public void toggleFilters() {
        // Hide other filters
        dateFilter.setVisibility(View.GONE);
        makeFilter.setVisibility(View.GONE);
        tagFilter.setVisibility(View.GONE);

        // Toggle keyword filter visibility and reset its state
        boolean isKeywordFilterVisible = keyFilter.getVisibility() == View.VISIBLE;
        keyFilter.setVisibility(isKeywordFilterVisible ? View.GONE : View.VISIBLE);
        keywordButton.setTextColor(isKeywordFilterVisible ? Color.BLACK : Color.WHITE);
        ViewCompat.setBackgroundTintList(keywordButton, isKeywordFilterVisible ? null :
                ColorStateList.valueOf(context.getResources().getColor(R.color.app_blue, null)));
        resetFilterButtons();
    }

    /**
     * Applies the keyword filter to the inventory list based on the entered text.
     *
     * @param keywordText the text to filter by
     */
    public void applyKeywordFilter(String keywordText) {
        String[] keywordArray = keywordText.toLowerCase().split("\\s+");
        Predicate<InventoryItem> filterCondition = item ->
                Arrays.stream(keywordArray).anyMatch(keyword -> item.getDescription().toLowerCase().contains(keyword));
        filteredResults(filterCondition);
    }

    /**
     * Updates the inventory list with the results that match the filter condition.
     *
     * @param condition the filter condition to apply
     */
    private void filteredResults(Predicate<InventoryItem> condition) {
        inventoryItems = inventoryListAdapter.getOriginalItems();
        ArrayList<InventoryItem> filteredResults = inventoryItems.stream()
                .filter(condition)
                .collect(Collectors.toCollection(ArrayList::new));
        inventoryListAdapter.updateItems(filteredResults);
        updateTotalValue(inventoryListAdapter);
        sortController.setInventoryItems(filteredResults);
        sortController.setFromHome(false);

    }



    /**
     * Resets the filter buttons to their default state.
     */
    private void resetFilterButtons() {
        keywords.setText("");
        inventoryListAdapter.resetItems();
        sortController.setInventoryItems(inventoryListAdapter.getOriginalItems());
        sortController.setFromHome(false);
        updateTotalValue(inventoryListAdapter);
        dateButton.setTextColor(Color.BLACK);
        makeButton.setTextColor(Color.BLACK);
        tagButton.setTextColor(Color.BLACK);
        ViewCompat.setBackgroundTintList(dateButton, null);
        ViewCompat.setBackgroundTintList(makeButton, null);
        ViewCompat.setBackgroundTintList(tagButton, null);
    }


    /**
     * Adapter for TextWatcher with no-op implementations of beforeTextChanged and onTextChanged.
     */
    private abstract static class TextWatcherAdapter implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // No-op
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // No-op
        }
    }
}