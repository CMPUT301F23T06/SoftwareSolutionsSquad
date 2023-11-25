package com.example.softwaresolutionssquad.views;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class ItemTagAdapter extends ArrayAdapter<String> {
    private ArrayList<String> tags;
    private Context context;
    public ItemTagAdapter(Context context, ArrayList<String> tags) {
        super(context, 0, tags);
        this.context = context;
        this.tags = tags;
    }

}
