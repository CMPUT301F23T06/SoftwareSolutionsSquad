package com.example.softwaresolutionssquad.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.softwaresolutionssquad.R;

import java.util.ArrayList;

public class ItemTagAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> tags;
    private final Context context;

    /**
     * Constructor for ItemTagAdapter. Initializes the adapter with the context and list of tags.
     * @param context The current context used to inflate the layout file.
     * @param tags The ArrayList of tags to be displayed.
     */
    public ItemTagAdapter(Context context, ArrayList<String> tags) {
        super(context, 0, tags);
        this.context = context;
        this.tags = tags;
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     * @param position The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate type before using.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_tag, parent, false);
        }

        String tag = tags.get(position);

        TextView itemName = view.findViewById(R.id.textViewItem);
        ImageButton deleteBtn = view.findViewById(R.id.buttonDelete);

        itemName.setText(tag);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tags.remove(position);
                notifyDataSetChanged();
            }
        });
        return view;
    }
}
