// Package declaration aligning with Java package naming conventions.
package com.example.softwaresolutionssquad.views;

// Import statements for necessary Android and Java classes.

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.softwaresolutionssquad.R;
import com.example.softwaresolutionssquad.models.InventoryItem;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Custom ArrayAdapter for displaying InventoryItem objects in a ListView.
 * Extends ArrayAdapter to leverage its functionality for item management and view recycling.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> imageUris;

    /**
     * Constructor for InventoryListAdapter.
     *
     * @param context the current context (Activity, Application, etc)
     * @param imageUris the data objects to represent in the ListView
     */
    public ImageAdapter(Context context, ArrayList<String> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageViewItem);
        }
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        System.err.println("TESTING");
        System.err.println(imageUris);
        Uri imageUri = Uri.parse(imageUris.get(position));
//        holder.imageView.setImageURI(imageUri);
        Glide.with(context).load(imageUris.get(position)).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullScreenImage(imageUri);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    private void showFullScreenImage(Uri imageUri) {
        Dialog fullScreenDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        fullScreenDialog.setContentView(R.layout.fragment_view_item_photo);

        ImageView imageView = fullScreenDialog.findViewById(R.id.fullscreenImageView);
        ImageButton closeButton = fullScreenDialog.findViewById(R.id.closeButton);

        Glide.with(context).load(imageUri).into(imageView);

        closeButton.setOnClickListener(v -> fullScreenDialog.dismiss());
        imageView.setOnClickListener(v -> fullScreenDialog.dismiss()); // Optional: allows closing by clicking the image

        fullScreenDialog.show();
    }
}
