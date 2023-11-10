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

    private ArrayList<String> tags;
    private Context context;

    public TagListAdapter(Context context, ArrayList<String> tags) {
        super(context, 0, tags);
        this.context = context;
        this.tags = tags;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
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
