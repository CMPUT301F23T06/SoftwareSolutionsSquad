package com.example.softwaresolutionssquad.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.softwaresolutionssquad.R;

public class AddTagFragment extends DialogFragment {
    private EditText tagName;
    private OnFragmentInteractionListener listener;

    /**
     * Interface for handling interaction with the fragment. Provides a method to communicate the selected tag back to the calling component.
     */
    public interface OnFragmentInteractionListener {
        void onOkPressed(String tag);
    }

    /**
     * Sets the listener for fragment interaction events.
     * @param listener The listener to handle fragment interaction callbacks.
     */
    public void setListener(OnFragmentInteractionListener listener) {
        this.listener = listener;
    }


    /**
     * Creates the dialog for the AddTagFragment with custom layout and handlers for positive and negative actions.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return A new Dialog instance to be displayed by the fragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_add_tag, null);
        tagName = view.findViewById(R.id.tag_name_input);

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.CustomAlertDialogTheme));
        return builder.setView(view).setNegativeButton("Cancel", null).setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tag = tagName.getText().toString();
                listener.onOkPressed(tag);
            }
        }).create();
    }
}
