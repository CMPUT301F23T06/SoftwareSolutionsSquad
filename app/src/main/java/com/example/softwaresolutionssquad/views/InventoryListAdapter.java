// Package declaration aligning with Java package naming conventions.
package com.example.softwaresolutionssquad.views;

// Import statements for necessary Android and Java classes.
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.softwaresolutionssquad.R;
import com.example.softwaresolutionssquad.models.InventoryItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Custom ArrayAdapter for displaying InventoryItem objects in a ListView.
 * Extends ArrayAdapter to leverage its functionality for item management and view recycling.
 */
public class InventoryListAdapter extends ArrayAdapter<InventoryItem> {
    private final ArrayList<InventoryItem> items;

    private final ArrayList<InventoryItem> originalItems;
    private final Context context;

    /**
     * Constructor for InventoryListAdapter.
     *
     * @param context the current context (Activity, Application, etc)
     * @param items the data objects to represent in the ListView
     */
    public InventoryListAdapter(Context context, ArrayList<InventoryItem> items) {
        super(context, 0, items);
        this.items = items;
        this.originalItems = new ArrayList<>(items);
        this.context = context;
    }

    public void addToOriginal(ArrayList<InventoryItem> items) {
        originalItems.clear();
        originalItems.addAll(items);
    }

    public ArrayList<InventoryItem> getOriginalItems() {
        return originalItems;
    }

    public void updateItems(ArrayList<InventoryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        this.notifyDataSetChanged();
    }

    /**
     * Resets the list of items to its original state.
     */
    public void resetItems() {
        items.clear();
        items.addAll(this.originalItems);
        this.notifyDataSetChanged();
    }

    public ArrayList<InventoryItem> getItems(){
        return items;
    }
    /**
     * Interface for delete button visibility callback.
     */
    public interface OnCheckedItemShowButtonsListener {
        void showButtonsIfNeeded();
    }

    private OnCheckedItemShowButtonsListener OnCheckedItemShowButtonsListener;

    /**
     * Sets the delete button show listener.
     *
     * @param listener the listener to set
     */
    public void setOnButtonsShowListener(OnCheckedItemShowButtonsListener listener) {
        this.OnCheckedItemShowButtonsListener = listener;
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param position the position of the item within the adapter's data set of the item whose view we want.
     * @param convertView the old view to reuse, if possible.
     * @param parent the parent that this view will eventually be attached to.
     * @return a View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView == null
                ? LayoutInflater.from(context).inflate(R.layout.inventory_item_layout, parent, false)
                : convertView;

        InventoryItem currentItem = items.get(position);

        TextView dateTextView = listItemView.findViewById(R.id.date);
        TextView descriptionTextView = listItemView.findViewById(R.id.Description);
        TextView makeTextView = listItemView.findViewById(R.id.Make);
        TextView estimatedValueTextView = listItemView.findViewById(R.id.EstimatedValue);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateTextView.setText(dateFormat.format(currentItem.getPurchaseDate()));

        descriptionTextView.setText(currentItem.getDescription());
        makeTextView.setText(currentItem.getMake());
        estimatedValueTextView.setText(String.format("$%.2f", currentItem.getEstimatedValue()));

        CheckBox checkBox = listItemView.findViewById(R.id.checkItem);
        checkBox.setTag(position);
        checkBox.setChecked(currentItem.getSelected());

        checkBox.setOnClickListener(v -> {
            int position1 = (int) v.getTag();
            InventoryItem item = getItem(position1);
            if (item != null) {
                item.setSelected(!item.getSelected());
                if (OnCheckedItemShowButtonsListener != null) {
                    OnCheckedItemShowButtonsListener.showButtonsIfNeeded();
                }
            }
        });

        return listItemView;
    }
}
