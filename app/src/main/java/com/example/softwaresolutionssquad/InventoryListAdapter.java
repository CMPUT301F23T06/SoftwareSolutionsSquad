// Package declaration aligning with Java package naming conventions.
package com.example.softwaresolutionssquad;

// Import statements for necessary Android and Java classes.
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

        // Return the view with all data set, representing one list item.
        return listItemView;
    }
}
