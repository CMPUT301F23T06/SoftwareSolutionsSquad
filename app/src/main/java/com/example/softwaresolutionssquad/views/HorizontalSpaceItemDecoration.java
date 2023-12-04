package com.example.softwaresolutionssquad.views;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int horizontalSpaceHeight;

    /**
     * Constructor for HorizontalSpaceItemDecoration. Initializes the decoration with a specified horizontal space height.
     * @param horizontalSpaceHeight The height of the horizontal space to be applied between items in the RecyclerView.
     */
    public HorizontalSpaceItemDecoration(int horizontalSpaceHeight) {
        this.horizontalSpaceHeight = horizontalSpaceHeight;
    }

    /**
     * Calculates the offset for each item within the RecyclerView to create a horizontal space.
     * @param outRect The Rect of the view to be modified.
     * @param view The current view within the RecyclerView.
     * @param parent The RecyclerView to which the ItemDecoration is being applied.
     * @param state The current state of the RecyclerView.
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.right = horizontalSpaceHeight;
        }
    }
}
