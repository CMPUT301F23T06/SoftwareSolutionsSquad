package com.example.softwaresolutionssquad;

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

public class InventoryListAdapter extends ArrayAdapter<InventoryItem> {
    private ArrayList<InventoryItem> items;
    private Context context;

    public InventoryListAdapter(Context context, ArrayList<InventoryItem> items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.inventory_item_layout, parent, false);
        }

        InventoryItem item = items.get(position);

        TextView dateView = view.findViewById(R.id.date);
        TextView descriptionView = view.findViewById(R.id.Description);
        TextView makeView = view.findViewById(R.id.Make);
        TextView estimatedValueView = view.findViewById(R.id.EstimatedValue);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        dateView.setText(sdf.format(item.getPurchaseDate()));
        descriptionView.setText(item.getDescription());
        makeView.setText(item.getMake());
        estimatedValueView.setText("$" + String.valueOf(item.getEstimatedValue()));

        return view;
    }
}
