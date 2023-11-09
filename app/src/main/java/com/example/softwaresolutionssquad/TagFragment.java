package com.example.softwaresolutionssquad;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class TagFragment extends Fragment implements AddTagFragment.OnFragmentInteractionListener {
    androidx.appcompat.widget.AppCompatButton createBtn;
    androidx.appcompat.widget.AppCompatButton deleteBtn;

    androidx.appcompat.widget.AppCompatButton searchBtn;

    EditText searchText;

    ArrayList<String> tagDataList;
    ArrayList<String> originalTagDataList;
    ArrayAdapter<String> tagAdapter;

    ListView tagsList;

    private Context context;
    public TagFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_layout, container, false);
        tagsList = view.findViewById(R.id.tag_list);
        tagDataList = new ArrayList<String>();
        deleteBtn = view.findViewById(R.id.deleteButton);
        createBtn = view.findViewById(R.id.createButton);
        searchBtn = view.findViewById(R.id.searchButton);
        searchText = view.findViewById(R.id.searchText);

        tagDataList = new ArrayList<>();
        originalTagDataList = new ArrayList<>();

        tagAdapter = new TagListAdapter(context, tagDataList);
        tagsList.setAdapter(tagAdapter);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTagFragment addTagFragment = new AddTagFragment();
                addTagFragment.setListener(TagFragment.this); // Set the listener to the current TagFragment
                addTagFragment.show(getActivity().getSupportFragmentManager(), "ADD_TAG");
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> tagsToRemove = new ArrayList<>();

                // Iterate through the tagDataList to find tags with a non-white background
                for (int i = 0; i < tagsList.getCount(); i++) {
                    View tag = tagsList.getChildAt(i);
                    TextView tagName = tag.findViewById(R.id.tagName);
                    int bgColor = ((ColorDrawable) tagName.getBackground()).getColor();
                    if (bgColor != Color.WHITE) {
                        tagsToRemove.add(tagDataList.get(i));
                    }
                }

                // Remove the tags with a non-white background from the tagDataList
                tagDataList.removeAll(tagsToRemove);

                // Notify the adapter that the data has changed
                tagAdapter.notifyDataSetChanged();

                // Disable the delete button after removing the tags
                deleteBtn.setEnabled(false);
                deleteBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dark_gray)));
                deleteBtn.setTextColor(Color.BLACK);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTextStr = searchText.getText().toString().toLowerCase();

                if (searchTextStr.isEmpty()) {
                    // If the search text is empty, restore the original tag list
                    tagDataList.clear();
                    tagDataList.addAll(originalTagDataList);
                } else {
                    // Filter the tags based on the search text
                    tagDataList.clear();
                    for (String tag : originalTagDataList) {
                        if (tag.toLowerCase().contains(searchTextStr)) {
                            tagDataList.add(tag);
                        }
                    }
                }

                // Notify the adapter that the data has changed
                tagAdapter.notifyDataSetChanged();
            }
        });

        tagsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View tag = parent.getChildAt(position);
                TextView tagName = tag.findViewById(R.id.tagName);
                ImageView tagImage = tag.findViewById(R.id.tagImage);
                CardView tagLayout = view.findViewById(R.id.tag);
                int bgColor = ((ColorDrawable) tagName.getBackground()).getColor();

                if (bgColor == Color.WHITE) {
                    tagLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.button_blue_color));
                    tagName.setBackgroundColor(ContextCompat.getColor(context, R.color.button_blue_color));
                    tagName.setTextColor(Color.WHITE);
                    tagImage.setColorFilter(Color.WHITE);
                } else {
                    tagLayout.setCardBackgroundColor(Color.WHITE);
                    tagName.setBackgroundColor(Color.WHITE);
                    tagName.setTextColor(ContextCompat.getColor(context, R.color.button_blue_color));
                    tagImage.setColorFilter(ContextCompat.getColor(context, R.color.button_blue_color));

                }

                boolean enableDeleteBtn = isDeleteButtonEnabled();

                deleteBtn.setEnabled(enableDeleteBtn);
                if (enableDeleteBtn) {
                    deleteBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                    deleteBtn.setTextColor(Color.WHITE);
                } else {
                    deleteBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dark_gray)));
                    deleteBtn.setTextColor(Color.BLACK);

                }
            }
        });


        return view;
    }

    private boolean isDeleteButtonEnabled() {
        for (int i = 0; i < tagsList.getCount(); i++) {
            View tag = tagsList.getChildAt(i);
            TextView tagName = tag.findViewById(R.id.tagName);
            int bgColor = ((ColorDrawable) tagName.getBackground()).getColor();
            if (bgColor != Color.WHITE) {
                return true; // Enable the delete button
            }
        }
        return false; // Disable the delete button
    }

    @Override
    public void onOkPressed(String tag) {
        if (originalTagDataList.contains(tag)) {
            // Tag already exists, show an error toast
            Toast.makeText(context, "Tag already exists", Toast.LENGTH_SHORT).show();
        } else {
            // Tag is unique, add it to the list
            originalTagDataList.add(tag);
            tagDataList.add(tag);
            // Notify the adapter that the data has changed
            tagAdapter.notifyDataSetChanged();
        }
    }
}
