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
    public ItemTagAdapter(Context context, ArrayList<String> tags) {
        super(context, 0, tags);
        this.context = context;
        this.tags = tags;
    }

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
