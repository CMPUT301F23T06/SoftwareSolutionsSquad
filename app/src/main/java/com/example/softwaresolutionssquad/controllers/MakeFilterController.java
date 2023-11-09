package com.example.softwaresolutionssquad.controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
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
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MakeFilterController {

    private final Context context;
    private final TextView makesTextView;
    private TextView keywordButton, dateButton, makeButton, tagButton;
    private LinearLayout makeFilter;
    private final InventoryListAdapter inventoryListAdapter;
    private final ListView inventoryListView;
    private final List<InventoryItem> inventoryItems;
    private final ArrayList<String> allMakesList;
    private final ArrayList<Integer> selectedMakesIndices;
    private Predicate<InventoryItem> filterCondition;


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
        setUpMakeButton();
    }

    private void setUpMakeButton() {
        makesTextView.setOnClickListener(v -> {
            // Logic to display makes selection dialog
            // Populate allMakesList if empty
            if (allMakesList.isEmpty()) {
                for (InventoryItem item : inventoryItems) {
                    String make = item.getMake().trim();
                    if (!allMakesList.contains(make)) {
                        allMakesList.add(make);
                    }
                }
            }

            // Convert ArrayList of makes to an Array for the dialog
            String[] allMakesArray = allMakesList.toArray(new String[0]);
            boolean[] selected = new boolean[allMakesArray.length];

            // Create and show the dialog
            showMakesSelectionDialog(allMakesArray, selected);
        });
    }

    private void showMakesSelectionDialog(String[] allMakesArray, boolean[] selected) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Make(s)");
        builder.setCancelable(false);
        // Reset dialog to be fully unselected
        selectedMakesIndices.clear();

        builder.setMultiChoiceItems(allMakesArray, selected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // If the make is checked, save its index
                if (isChecked) {
                    selectedMakesIndices.add(which);
                    // If the make is unselected, do not save its index
                } else {
                    selectedMakesIndices.remove(which);
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder stringBuilder = new StringBuilder();
                ArrayList<String> selectedMakesList = new ArrayList<>();
                for (int i = 0; i < selectedMakesIndices.size(); i++) {
                    // Get the names of the makes from the indices
                    selectedMakesList.add(allMakesArray[selectedMakesIndices.get(i)]);
                    // Attach the names of the selected makes to a string
                    stringBuilder.append(allMakesArray[selectedMakesIndices.get(i)]);
                    if (i != selectedMakesIndices.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }
                // Display the selected makes in the dropdown bar
                makesTextView.setText(stringBuilder.toString());

                // Create predicate to filter the full list of items by makes selected
                filterCondition = item -> selectedMakesList.contains(item.getMake());
                // Display the filtered list of items
                filteredResults(filterCondition);

            }
        });
        // Remove dialog when cancelled
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public void toggleMakeFilterVisibility() {
        if (makeFilter.getVisibility() == View.GONE) {
            makeFilter.setVisibility(View.VISIBLE);

            ViewCompat.setBackgroundTintList(keywordButton, null);
            ViewCompat.setBackgroundTintList(dateButton, null);
            ViewCompat.setBackgroundTintList(tagButton, null);
            ViewCompat.setBackgroundTintList(makeButton,
                    ColorStateList.valueOf(getColor(R.color.app_blue)));

            // Reset ListView to default, unfiltered list of items
            inventoryListView.setAdapter(inventoryListAdapter);
            // Clear the TextView
            makesTextView.setText("");
            // Save all the makes that are present in the list of items
            ArrayList<String> allMakesList = new ArrayList<>();
            // Save all the indices of the items selected to filter by
            ArrayList<Integer> selectedMakesIndices = new ArrayList<>();
            // Additional logic to reset views and set background colors
        } else {
            makeFilter.setVisibility(View.GONE);
            inventoryListView.setAdapter(inventoryListAdapter);
            // Reset background tint for makeButton if needed
        }
    }

    private void filteredResults(Predicate<InventoryItem> condition) {
        ArrayList<InventoryItem> filteredResults = inventoryItems.stream()
                .filter(condition)
                .collect(Collectors.toCollection(ArrayList::new));
        InventoryListAdapter filterListAdapter = new InventoryListAdapter(context, filteredResults);
        inventoryListView.setAdapter(filterListAdapter);
    }

    private int getColor(int colorId) {
        return context.getResources().getColor(colorId, null);
    }
}
