package com.example.softwaresolutionssquad.controllers;

import com.example.softwaresolutionssquad.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

/**
 * Controller for managing various filters on an inventory list.
 */
public class FilterController {

    private View dateFilter;
    private View makeFilter;
    private View tagFilter;
    private View keyFilter;
    private TextView dateButton;
    private TextView makeButton;
    private TextView tagButton;
    private TextView keywordButton;
    private ListView inventoryListView;
    private ListAdapter inventoryListAdapter;
    private EditText keywords;
    private Context context;

    /**
     * Constructs a FilterController with references to UI components and the application context.
     *
     * @param dateFilter           The view for the date filter.
     * @param makeFilter           The view for the make filter.
     * @param tagFilter            The view for the tag filter.
     * @param keyFilter            The view for the keyword filter.
     * @param dateButton           The button for triggering the date filter.
     * @param makeButton           The button for triggering the make filter.
     * @param tagButton            The button for triggering the tag filter.
     * @param keywordButton        The button for triggering the keyword filter.
     * @param inventoryListView    The ListView to apply the filters to.
     * @param inventoryListAdapter The adapter for the ListView.
     * @param keywords             The EditText for inputting keywords.
     * @param context              The application context.
     */
    public FilterController(View dateFilter, View makeFilter, View tagFilter, View keyFilter,
                            TextView dateButton, TextView makeButton, TextView tagButton,
                            TextView keywordButton, ListView inventoryListView,
                            ListAdapter inventoryListAdapter, EditText keywords, Context context) {
        this.dateFilter = dateFilter;
        this.makeFilter = makeFilter;
        this.tagFilter = tagFilter;
        this.keyFilter = keyFilter;
        this.dateButton = dateButton;
        this.makeButton = makeButton;
        this.tagButton = tagButton;
        this.keywordButton = keywordButton;
        this.inventoryListView = inventoryListView;
        this.inventoryListAdapter = inventoryListAdapter;
        this.keywords = keywords;
        this.context = context;

        setupKeywordButton();
        setupTextWatcher();
    }

    /**
     * Sets up the keyword button.
     */
    private void setupKeywordButton() {
        keywordButton.setOnClickListener(v -> toggleKeywordFilter());
    }

    /**
     * Sets up a text watcher for the keyword EditText.
     */
    private void setupTextWatcher() {
        keywords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Intentionally left blank
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterResultsByKeywords(s.toString());
            }
        });
    }

    /**
     * Toggles the visibility of the keyword filter.
     */
    private void toggleKeywordFilter() {
        boolean isFilterVisible = keyFilter.getVisibility() == View.VISIBLE;
        keyFilter.setVisibility(isFilterVisible ? View.GONE : View.VISIBLE);
        updateButtonBackgrounds(!isFilterVisible);
        if (isFilterVisible) {
            resetFilter();
        }
    }

    /**
     * Updates the background color of buttons based on filter visibility.
     *
     * @param filterActive Indicates whether a filter is active.
     */
    private void updateButtonBackgrounds(boolean filterActive) {
        int appBlueColor = ContextCompat.getColor(context, R.color.app_blue);
        ColorStateList defaultColor = null;
        ColorStateList activeColor = ColorStateList.valueOf(appBlueColor);

        ViewCompat.setBackgroundTintList(dateButton, defaultColor);
        ViewCompat.setBackgroundTintList(makeButton, defaultColor);
        ViewCompat.setBackgroundTintList(tagButton, defaultColor);
        ViewCompat.setBackgroundTintList(keywordButton, filterActive ? activeColor : defaultColor);
    }

    /**
     * Resets the inventory list view and clears the keyword text.
     */
    private void resetFilter() {
        inventoryListView.setAdapter(inventoryListAdapter);
        keywords.setText("");
    }

    /**
     * Filters the results in the inventory list by the entered keywords.
     *
     * @param keywordText The text to filter with.
     */
    private void filterResultsByKeywords(String keywordText) {
        // Implement filtering logic here
    }
}
