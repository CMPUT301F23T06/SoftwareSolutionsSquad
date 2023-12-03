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
 * Controller class for managing filter operations based on the make of inventory items.
 */
public class MakeFilterController {

    private final Context context;
    private final TextView makesTextView;
    private final LinearLayout makeFilter;
    private final InventoryListAdapter inventoryListAdapter;
    private final ListView inventoryListView;
    private final List<InventoryItem> inventoryItems;
    private final ArrayList<String> allMakesList;
    private final ArrayList<Integer> selectedMakesIndices;
    private Predicate<InventoryItem> filterCondition;
    private final TextView keywordButton;
    private final TextView dateButton;
    private final TextView makeButton;
    private final TextView tagButton;

    /**
     * Constructs a MakeFilterController.
     *
     * @param context                The current context.
     * @param makesTextView          The text view to display selected makes.
     * @param makeFilter             The layout containing the make filter controls.
     * @param keywordButton          The button for keyword filtering.
     * @param dateButton             The button for date filtering.
     * @param makeButton             The button for make filtering.
     * @param tagButton              The button for tag filtering.
     * @param inventoryListAdapter   The adapter for the inventory list view.
     * @param inventoryListView      The list view for displaying inventory items.
     * @param inventoryItems         The list of inventory items.
     */
    public MakeFilterController(Context context,
                                TextView makesTextView,
                                LinearLayout makeFilter,
                                TextView keywordButton,
                                TextView dateButton,
                                TextView makeButton,
                                TextView tagButton,
                                InventoryListAdapter inventoryListAdapter,
                                ListView inventoryListView,
                                List<InventoryItem> inventoryItems) {
        this.context = context;
        this.makesTextView = makesTextView;
        this.inventoryListAdapter = inventoryListAdapter;
        this.inventoryListView = inventoryListView;
        this.inventoryItems = inventoryItems;
        this.allMakesList = new ArrayList<>();
        this.selectedMakesIndices = new ArrayList<>();
        this.keywordButton = keywordButton;
        this.dateButton = dateButton;
        this.makeButton = makeButton;
        this.tagButton = tagButton;
        this.makeFilter = makeFilter;
        initializeMakeButton();
    }

    /**
     * Initializes the make button and sets its on click listener.
     */
    private void initializeMakeButton() {
        makesTextView.setOnClickListener(v -> showMakesSelectionDialog());
    }

    /**
     * Shows a multi-choice dialog for selecting makes to filter inventory items.
     */
    private void showMakesSelectionDialog() {
        populateMakesListIfNeeded();
        String[] allMakesArray = allMakesList.toArray(new String[0]);
        boolean[] selected = new boolean[allMakesList.size()];
        for (int i : selectedMakesIndices) {
            selected[i] = true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme);
        builder.setTitle("Select Make(s)")
                .setCancelable(false)
                .setMultiChoiceItems(allMakesArray, selected, this::onMakeSelected)
                .setPositiveButton("OK", this::onMakesDialogPositive)
                .setNegativeButton("Cancel", this::onMakesDialogNegative)
                .show();
    }

    /**
     * Populates the allMakesList if it is currently empty.
     */
    private void populateMakesListIfNeeded() {
        if (allMakesList.isEmpty()) {
            for (InventoryItem item : inventoryItems) {
                String make = item.getMake().trim();
                if (!allMakesList.contains(make)) {
                    allMakesList.add(make);
                }
            }
        }
    }

    /**
     * Handles make selection within the dialog.
     *
     * @param dialog    The dialog where the selection was made.
     * @param which     The index of the selected item.
     * @param isChecked The new checked state of the item.
     */
    private void onMakeSelected(DialogInterface dialog, int which, boolean isChecked) {
        if (isChecked) {
            if (!selectedMakesIndices.contains(which)) {
                selectedMakesIndices.add(which);
            }
        } else {
            selectedMakesIndices.remove(Integer.valueOf(which));
        }
    }

    /**
     * Handles the positive action of the makes selection dialog.
     *
     * @param dialog The dialog interface.
     * @param which  The button that was clicked.
     */
    private void onMakesDialogPositive(DialogInterface dialog, int which) {
        HashSet<String> selectedMakesList = new HashSet<>();
        for (int index : selectedMakesIndices) {
            selectedMakesList.add(allMakesList.get(index));
        }
        makesTextView.setText(String.join(", ", selectedMakesList));
        if (selectedMakesList.isEmpty()) {
            inventoryListView.setAdapter(inventoryListAdapter);
        } else {
            filterCondition = item -> selectedMakesList.contains(item.getMake());
            filteredResults(filterCondition);
        }
    }

    /**
     * Handles the negative action of the makes selection dialog.
     *
     * @param dialog The dialog interface.
     * @param which  The button that was clicked.
     */
    private void onMakesDialogNegative(DialogInterface dialog, int which) {
        dialog.dismiss();
    }

    /**
     * Toggles the visibility of the make filter layout.
     */
    public void toggleMakeFilterVisibility() {
        if (makeFilter.getVisibility() == View.GONE) {
            makeFilter.setVisibility(View.VISIBLE);
            makeButton.setTextColor(Color.WHITE);
            keywordButton.setTextColor(Color.BLACK);
            dateButton.setTextColor(Color.BLACK);
            tagButton.setTextColor(Color.BLACK);
            ViewCompat.setBackgroundTintList(keywordButton, null);
            ViewCompat.setBackgroundTintList(dateButton, null);
            ViewCompat.setBackgroundTintList(tagButton, null);
            ViewCompat.setBackgroundTintList(makeButton, ColorStateList.valueOf(context.getResources().getColor(R.color.app_blue, null)));
            inventoryListView.setAdapter(inventoryListAdapter);
            makesTextView.setText("");
            selectedMakesIndices.clear();
        } else {
            makeFilter.setVisibility(View.GONE);
            makeButton.setTextColor(Color.BLACK);
            ViewCompat.setBackgroundTintList(makeButton, null);
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
