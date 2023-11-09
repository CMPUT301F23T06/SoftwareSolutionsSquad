package com.example.softwaresolutionssquad.controllers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.R;

/**
 * This class is responsible for controlling the tag filters of an inventory list view.
 */
public class TagController {

    private Context context;
    private ArrayAdapter<InventoryItem> inventoryListAdapter;
    private ListView inventoryListView;
    private View keyFilter;
    private View makeFilter;
    private View dateFilter;
    private TextView dateButton;
    private TextView makeButton;
    private TextView keywordButton;
    private TextView tagButton;

    /**
     * Constructs a new TagController.
     *
     * @param context                the application context
     * @param inventoryListAdapter   the adapter for the inventory list view
     * @param inventoryListView      the inventory list view itself
     * @param keyFilter              the view for the keyword filter
     * @param makeFilter             the view for the make filter
     * @param dateFilter             the view for the date filter
     * @param dateButton             the button for the date filter
     * @param makeButton             the button for the make filter
     * @param keywordButton          the button for the keyword filter
     * @param tagButton              the button for the tag filter
     */
    public TagController(Context context, ArrayAdapter<InventoryItem> inventoryListAdapter,
                         ListView inventoryListView, View keyFilter, View makeFilter,
                         View dateFilter, TextView dateButton, TextView makeButton,
                         TextView keywordButton, TextView tagButton) {
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

    /**
     * Toggles the visibility of the tag filter.
     *
     * @param tagFilter the tag filter view to toggle visibility for
     */
    public void toggleTagFilterVisibility(View tagFilter) {
        // Hide all filter views
        keyFilter.setVisibility(View.GONE);
        makeFilter.setVisibility(View.GONE);
        dateFilter.setVisibility(View.GONE);

        // Toggle the visibility of the specified tag filter
        if (tagFilter.getVisibility() == View.GONE) {
            tagFilter.setVisibility(View.VISIBLE);
            setBackgroundTint(null);
            // Highlight the tag button when the tag filter is visible
            ViewCompat.setBackgroundTintList(tagButton, ColorStateList.valueOf(context.getColor(R.color.app_blue)));
        } else {
            tagFilter.setVisibility(View.GONE);
            // Update the inventory list when hiding the filter
            inventoryListView.setAdapter(inventoryListAdapter);
        }
    }

    /**
     * Sets the background tint of the filter buttons.
     *
     * @param colorStateList the color state list to set as the background tint
     */
    private void setBackgroundTint(ColorStateList colorStateList) {
        ViewCompat.setBackgroundTintList(dateButton, colorStateList);
        ViewCompat.setBackgroundTintList(makeButton, colorStateList);
        ViewCompat.setBackgroundTintList(keywordButton, colorStateList);
    }
}
