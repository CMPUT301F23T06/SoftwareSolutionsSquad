package com.example.softwaresolutionssquad.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.softwaresolutionssquad.R;

import java.util.ArrayList;

public class TagListAdapter extends ArrayAdapter<String> {

    private final ArrayList<String> tags;
    private final Context context;

    /**
     * Constructor for TagListAdapter. Initializes the adapter with the context and list of tags.
     * @param context The current context used to inflate the layout file.
     * @param tags The ArrayList of tags to be displayed.
     */
    public TagListAdapter(Context context, ArrayList<String> tags) {
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
            view = LayoutInflater.from(context).inflate(R.layout.tag_list, parent, false);
        }

        String tag = tags.get(position);

        TextView tagName = view.findViewById(R.id.tagName);
        ImageView tagImage = view.findViewById(R.id.tagImage);

        tagName.setText(tag);
        tagImage.setImageResource(R.drawable.tag);

        return view;
    }
}
