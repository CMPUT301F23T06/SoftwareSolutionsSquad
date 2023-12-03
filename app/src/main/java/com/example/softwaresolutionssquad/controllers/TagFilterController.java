package com.example.softwaresolutionssquad.controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.softwaresolutionssquad.R;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;
import android.app.AlertDialog;

import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Controller class for managing filter operations based on the tags of inventory items.
 */
public class TagFilterController {

    private final Context context;
    private final TextView tagsTextView;
    private final LinearLayout tagFilter;
    private final InventoryListAdapter inventoryListAdapter;
    private final ListView inventoryListView;
    private final List<InventoryItem> inventoryItems;
    private final ArrayList<String> allTagsList;
    private final ArrayList<Integer> selectedTagsIndices;
    private Predicate<InventoryItem> filterCondition;
    private final TextView keywordButton;
    private final TextView dateButton;
    private final TextView makeButton;
    private final TextView tagButton;

    /**
     * Constructs a TagFilterController.
     *
     * @param context                The current context.
     * @param tagsTextView           The text view to display selected tags.
     * @param tagFilter              The layout containing the tag filter controls.
     * @param keywordButton          The button for keyword filtering.
     * @param dateButton             The button for date filtering.
     * @param makeButton             The button for make filtering.
     * @param tagButton              The button for tag filtering.
     * @param inventoryListAdapter   The adapter for the inventory list view.
     * @param inventoryListView      The list view for displaying inventory items.
     * @param inventoryItems         The list of inventory items.
     */
    public TagFilterController(Context context,
                               TextView tagsTextView,
                               LinearLayout tagFilter,
                               TextView keywordButton,
                               TextView dateButton,
                               TextView makeButton,
                               TextView tagButton,
                               InventoryListAdapter inventoryListAdapter,
                               ListView inventoryListView,
                               List<InventoryItem> inventoryItems) {
        this.context = context;
        this.tagsTextView = tagsTextView;
        this.inventoryListAdapter = inventoryListAdapter;
        this.inventoryListView = inventoryListView;
        this.inventoryItems = inventoryItems;
        this.allTagsList = new ArrayList<>();
        this.selectedTagsIndices = new ArrayList<>();
        this.keywordButton = keywordButton;
        this.dateButton = dateButton;
        this.makeButton = makeButton;
        this.tagButton = tagButton;
        this.tagFilter = tagFilter;
        initializeTagButton();
    }

    /**
     * Initializes the tag button and sets its on click listener.
     */
    private void initializeTagButton() {
        tagsTextView.setOnClickListener(v -> showTagsSelectionDialog());
    }

    /**
     * Shows a multi-choice dialog for selecting tags to filter inventory items.
     */
    private void showTagsSelectionDialog() {
        populateTagsListIfNeeded();
        String[] allTagsArray = allTagsList.toArray(new String[0]);
        boolean[] selected = new boolean[allTagsList.size()];
        for (int i : selectedTagsIndices) {
            selected[i] = true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme);
        builder.setTitle("Select Tag(s)")
                .setCancelable(false)
                .setMultiChoiceItems(allTagsArray, selected, this::onTagSelected)
                .setPositiveButton("OK", this::onTagsDialogPositive)
                .setNegativeButton("Cancel", this::onTagsDialogNegative)
                .show();
    }

    /**
     * Populates the allTagsList if it is currently empty.
     */
    private void populateTagsListIfNeeded() {
        if (allTagsList.isEmpty()) {
            HashSet<String> tags = new HashSet<String>();
            for (InventoryItem item : inventoryItems) {
                tags.addAll(item.getTags());
            }
            allTagsList.addAll(tags);
        }
    }

    /**
     * Handles tag selection within the dialog.
     *
     * @param dialog    The dialog where the selection was made.
     * @param which     The index of the selected item.
     * @param isChecked The new checked state of the item.
     */
    private void onTagSelected(DialogInterface dialog, int which, boolean isChecked) {
        if (isChecked) {
            if (!selectedTagsIndices.contains(which)) {
                selectedTagsIndices.add(which);
            }
        } else {
            selectedTagsIndices.remove(Integer.valueOf(which));
        }
    }

    /**
     * Handles the positive action of the tags selection dialog.
     *
     * @param dialog The dialog interface.
     * @param which  The button that was clicked.
     */
    private void onTagsDialogPositive(DialogInterface dialog, int which) {
        HashSet<String> selectedTagsList = new HashSet<>();
        for (int index : selectedTagsIndices) {
            selectedTagsList.add(allTagsList.get(index));
        }
        tagsTextView.setText(String.join(", ", selectedTagsList));
        if (selectedTagsList.isEmpty()) {
            inventoryListView.setAdapter(inventoryListAdapter);
        } else {
            filterCondition = item -> item.getTags().stream().anyMatch(tag -> selectedTagsList.contains(tag));
            filteredResults(filterCondition);
        }
    }

    /**
     * Handles the negative action of the tags selection dialog.
     *
     * @param dialog The dialog interface.
     * @param which  The button that was clicked.
     */
    private void onTagsDialogNegative(DialogInterface dialog, int which) {
        dialog.dismiss();
    }

    /**
     * Toggles the visibility of the tag filter layout.
     */
    public void toggleTagFilterVisibility() {
        if (tagFilter.getVisibility() == View.GONE) {
            tagFilter.setVisibility(View.VISIBLE);
            tagButton.setTextColor(Color.WHITE);
            keywordButton.setTextColor(Color.BLACK);
            dateButton.setTextColor(Color.BLACK);
            makeButton.setTextColor(Color.BLACK);
            ViewCompat.setBackgroundTintList(keywordButton, null);
            ViewCompat.setBackgroundTintList(dateButton, null);
            ViewCompat.setBackgroundTintList(makeButton, null);
            ViewCompat.setBackgroundTintList(tagButton, ColorStateList.valueOf(context.getResources().getColor(R.color.app_blue, null)));
            inventoryListView.setAdapter(inventoryListAdapter);
            tagsTextView.setText("");
            selectedTagsIndices.clear();
        } else {
            tagButton.setTextColor(Color.BLACK);
            tagFilter.setVisibility(View.GONE);
            ViewCompat.setBackgroundTintList(tagButton, null);
        }
    }

    /**
     * Filters the results based on the specified condition and updates the list view.
     *
     * @param condition The predicate to apply as the filter condition.
     */
    public void filteredResults(Predicate<InventoryItem> condition) {
        List<InventoryItem> filteredResults = inventoryItems.stream()
                .filter(condition)
                .collect(Collectors.toList());
        InventoryListAdapter filterListAdapter = new InventoryListAdapter(context, new ArrayList<>(filteredResults));
        inventoryListView.setAdapter(filterListAdapter);
    }

    /**
     * Retrieves a color from the resource.
     *
     * @param colorId The resource ID of the color to retrieve.
     * @return The resolved color value.
     */
    private int getColor(int colorId) {
        return context.getResources().getColor(colorId, null);
    }
}
