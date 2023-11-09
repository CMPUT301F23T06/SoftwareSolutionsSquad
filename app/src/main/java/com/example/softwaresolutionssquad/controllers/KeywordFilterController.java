package com.example.softwaresolutionssquad.controllers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.example.softwaresolutionssquad.R;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KeywordFilterController {
    private EditText keywords;
    private TextView keywordButton, dateButton, makeButton, tagButton;

    private LinearLayout keyFilter, dateFilter, makeFilter, tagFilter;

    private Context context;
    private InventoryListAdapter inventoryListAdapter;
    private ListView inventoryListView;

    private Predicate<InventoryItem> filterCondition;
    private ArrayList<InventoryItem> InventoryItems;

    public KeywordFilterController(Context context,
                                   LinearLayout keyFilter,
                                   EditText keywords,
                                   TextView keywordButton,
                                   TextView dateButton,
                                   TextView makeButton,
                                   TextView tagButton,
                                   LinearLayout dateFilter,
                                   LinearLayout makeFilter,
                                   LinearLayout tagFilter,
                                   InventoryListAdapter inventoryListAdapter,
                                   ListView inventoryListView,
                                   ArrayList<InventoryItem> InventoryItems
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
        this.InventoryItems = InventoryItems;
        setupKeywordFilter();
    }

    private void setupKeywordFilter() {
        keywordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFilters();
            }
        });

        keywords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                applyKeywordFilter(s.toString());
            }
        });
    }

    private void toggleFilters() {
        // Hide other filters
        dateFilter.setVisibility(View.GONE);
        makeFilter.setVisibility(View.GONE);
        tagFilter.setVisibility(View.GONE);

        // Show or hide the keyword filter
        if (keyFilter.getVisibility() == View.GONE) {
            keyFilter.setVisibility(View.VISIBLE);
            ViewCompat.setBackgroundTintList(keywordButton, ColorStateList.valueOf(getColor(R.color.app_blue)));
            inventoryListView.setAdapter(inventoryListAdapter);
            keywords.setText("");
        } else {
            keyFilter.setVisibility(View.GONE);
            inventoryListView.setAdapter(inventoryListAdapter);
            ViewCompat.setBackgroundTintList(keywordButton, null);
        }

        // Reset the backgrounds for other filter buttons
        ViewCompat.setBackgroundTintList(dateButton, null);
        ViewCompat.setBackgroundTintList(makeButton, null);
        ViewCompat.setBackgroundTintList(tagButton, null);
    }

    private void applyKeywordFilter(String keywordText) {
        String[] keywordArray = keywordText.toLowerCase().split(" ");
        filterCondition = item -> Arrays.stream(keywordArray).anyMatch(keyword -> item.getDescription().toLowerCase().contains(keyword));
        filteredResults(filterCondition);
    }

    private void filteredResults(Predicate<InventoryItem> condition) {
        ArrayList<InventoryItem> filteredResults = InventoryItems.stream()
                .filter(condition)
                .collect(Collectors.toCollection(ArrayList::new));
        InventoryListAdapter filterListAdapter = new InventoryListAdapter(context, filteredResults);
        inventoryListView.setAdapter(filterListAdapter);
    }

    private int getColor(int colorId) {
        return context.getResources().getColor(colorId, null);
    }
}
