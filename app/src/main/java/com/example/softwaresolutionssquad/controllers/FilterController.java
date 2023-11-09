package com.example.softwaresolutionssquad.controllers;
import com.example.softwaresolutionssquad.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

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

    // Constructor
    public FilterController(View dateFilter,
                            View makeFilter,
                            View tagFilter,
                            View keyFilter,
                            TextView dateButton,
                            TextView makeButton,
                            TextView tagButton,
                            TextView keywordButton,
                            ListView inventoryListView, ListAdapter inventoryListAdapter, EditText keywords,
                            Context context) {
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

    private void setupKeywordButton() {
        keywordButton.setOnClickListener(v -> toggleKeywordFilter());
    }

    private void setupTextWatcher() {
        keywords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterResultsByKeywords(s.toString());
            }
        });
    }

    private void toggleKeywordFilter() {
        boolean isFilterVisible = keyFilter.getVisibility() == View.VISIBLE;
        // Toggle visibility
        keyFilter.setVisibility(isFilterVisible ? View.GONE : View.VISIBLE);
        // Update button backgrounds
        updateButtonBackgrounds(isFilterVisible);
        // Reset the list view and keyword text if needed
        if (isFilterVisible) {
            inventoryListView.setAdapter(inventoryListAdapter);
            keywords.setText("");
        }
    }

    private void updateButtonBackgrounds(boolean isFilterVisible) {
        int appBlueColor = ContextCompat.getColor(context, R.color.app_blue);
        ViewCompat.setBackgroundTintList(dateButton, null);
        ViewCompat.setBackgroundTintList(makeButton, null);
        ViewCompat.setBackgroundTintList(tagButton, null);
        ViewCompat.setBackgroundTintList(keywordButton, isFilterVisible ? null : ColorStateList.valueOf(appBlueColor));
    }

    private void filterResultsByKeywords(String keywordText) {
        // Your filtering logic here
    }
}
