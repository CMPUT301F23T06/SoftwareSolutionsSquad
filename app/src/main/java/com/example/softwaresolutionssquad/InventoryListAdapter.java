// Package declaration aligning with Java package naming conventions.
package com.example.softwaresolutionssquad;

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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Custom ArrayAdapter for displaying InventoryItem objects in a ListView.
 * Extends ArrayAdapter to leverage its functionality for item management and view recycling.
 */
public class InventoryListAdapter extends ArrayAdapter<InventoryItem> {
    // ArrayList to store the InventoryItem objects.
    private ArrayList<InventoryItem> items;
    // Context for accessing application-specific resources and classes.
    private Context context;
    private FirebaseFirestore db;
    private CollectionReference itemsRef;

    /**
     * Constructor for InventoryListAdapter.
     *
     * @param context the current context (Activity, Application, etc)
     * @param items the data objects to represent in the ListView
     */
    public InventoryListAdapter(Context context, ArrayList<InventoryItem> items) {
        // Initialize the adapter using a 0 resource ID since the view is custom.
        super(context, 0, items);
        this.items = items; // Assign the passed item list to the instance variable.
        this.context = context; // Assign the passed context to the instance variable.
    }

    // Define an interface for the callback
    public interface OnDeleteButtonShowListener {
        void showDeleteButtonIfNeeded();
    }

    // Reference to the listener
    private OnDeleteButtonShowListener onDeleteButtonShowListener;

    // Setter for the listener
    public void setOnDeleteButtonShowListener(OnDeleteButtonShowListener listener) {
        this.onDeleteButtonShowListener = listener;
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
        // Attempt to reuse an existing view (convertView) if one is available.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.inventory_item_layout, parent, false);
        }

        // Get the InventoryItem object for the current position.
        InventoryItem currentItem = items.get(position);

        // Retrieve and assign TextViews from the layout file.
        TextView dateTextView = listItemView.findViewById(R.id.date);
        TextView descriptionTextView = listItemView.findViewById(R.id.Description);
        TextView makeTextView = listItemView.findViewById(R.id.Make);
        TextView estimatedValueTextView = listItemView.findViewById(R.id.EstimatedValue);

        // Format and set the date on its corresponding TextView.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateTextView.setText(dateFormat.format(currentItem.getPurchaseDate()));

        // Populate the text views with data from the current InventoryItem.
        descriptionTextView.setText(currentItem.getDescription());
        makeTextView.setText(currentItem.getMake());
        estimatedValueTextView.setText("$" + currentItem.getEstimatedValue());

        // Retrieve the checkbox from the layout and set its tag to the position
        CheckBox checkBox = listItemView.findViewById(R.id.checkItem);
        checkBox.setTag(position); // Tag with the position to identify the item when checkbox is toggled

        // Set the checkbox state based on the item's selection state
        checkBox.setChecked(currentItem.getSelected());

        // Set up a click listener for the checkbox
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // v is the checkbox itself
                int position = (int) v.getTag(); // get the tag associated with the checkbox: position of the item in the ListView
                InventoryItem item = getItem(position); // get the Inventory item at the given position
                if (item != null) {
                    item.setSelected(!item.getSelected()); // Toggle the current state
                    // Notify the listener
                    if (onDeleteButtonShowListener != null) {
                        onDeleteButtonShowListener.showDeleteButtonIfNeeded();
                    }
                }
            }
        });

        // Return the view with all data set, representing one list item.
        return listItemView;
    }
}
