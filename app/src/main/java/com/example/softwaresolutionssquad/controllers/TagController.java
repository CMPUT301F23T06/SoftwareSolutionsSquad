package com.example.softwaresolutionssquad.controllers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.R;
public class TagController {

    private Context context;
    private ArrayAdapter<InventoryItem> inventoryListAdapter;
    private ListView inventoryListView;
    private View keyFilter, makeFilter, dateFilter;
    private TextView dateButton, makeButton, keywordButton, tagButton;

    public TagController(Context context, ArrayAdapter<InventoryItem> inventoryListAdapter, ListView inventoryListView, View keyFilter, View makeFilter, View dateFilter, TextView dateButton, TextView makeButton, TextView keywordButton, TextView tagButton) {
        this.context = context;
        this.inventoryListAdapter = inventoryListAdapter;
        this.inventoryListView = inventoryListView;
        this.keyFilter = keyFilter;
        this.makeFilter = makeFilter;
        this.dateFilter = dateFilter;
        this.dateButton = dateButton;
        this.makeButton = makeButton;
        this.keywordButton = keywordButton;
        this.tagButton = tagButton;
    }

    public void toggleTagFilterVisibility(View tagFilter) {
        keyFilter.setVisibility(View.GONE);
        makeFilter.setVisibility(View.GONE);
        dateFilter.setVisibility(View.GONE);

        if (tagFilter.getVisibility() == View.GONE) {
            tagFilter.setVisibility(View.VISIBLE);
            setBackgroundTint(null);
            ViewCompat.setBackgroundTintList(tagButton, ColorStateList.valueOf(context.getColor(R.color.app_blue)));
        } else {
            tagFilter.setVisibility(View.GONE);
            inventoryListView.setAdapter(inventoryListAdapter);
        }
    }

    private void setBackgroundTint(ColorStateList colorStateList) {
        ViewCompat.setBackgroundTintList(dateButton, colorStateList);
        ViewCompat.setBackgroundTintList(makeButton, colorStateList);
        ViewCompat.setBackgroundTintList(keywordButton, colorStateList);
    }
}
