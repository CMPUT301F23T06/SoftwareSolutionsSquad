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
    private final Context context;
    private final ArrayList<String> imageUris;
    private final ArrayList<String> newImages;

    /**
     * Constructor for InventoryListAdapter.
     *
     * @param context the current context (Activity, Application, etc)
     * @param imageUris the data objects to represent in the ListView
     */
    public ImageAdapter(Context context, ArrayList<String> imageUris, ArrayList<String> newImages) {
        this.context = context;
        this.imageUris = imageUris;
        this.newImages = newImages;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageButton deleteButton; // Add a delete button

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageViewItem);
            deleteButton = view.findViewById(R.id.deleteButton); // Initialize the delete button
        }
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri imageUri = Uri.parse(imageUris.get(position));
//        holder.imageView.setImageURI(imageUri);
        Glide.with(context)
                .load(imageUris.get(position))
                .override(holder.imageView.getWidth(), holder.imageView.getHeight())
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullScreenImage(imageUri);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            System.err.println(imageUris);
            System.err.println(newImages);
            newImages.remove(imageUris.get(position));
            imageUris.remove(position); // Remove the image URI from the list
            notifyItemRemoved(position); // Notify the adapter of the item removed
            notifyItemRangeChanged(position, imageUris.size());
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

        Glide.with(context)
                .load(imageUri)
                .into(imageView);

        closeButton.setOnClickListener(v -> fullScreenDialog.dismiss());
        imageView.setOnClickListener(v -> fullScreenDialog.dismiss()); // Optional: allows closing by clicking the image

        fullScreenDialog.show();
    }
}
