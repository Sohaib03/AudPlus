package com.threedots.audplus;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class RenameDialog extends DialogFragment implements View.OnClickListener {


    public TextInputEditText filenameText;
    public Button okButton;
    public String filename;
    public Fragment parentFragment;

    public RenameDialog(Fragment parent, String filename) {
        super();
        this.parentFragment = parent;
        this.filename = filename;
    }

    public interface OnInputListener {
        void sendInput(String input);
    }

    public OnInputListener onInputListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rename_dialog, container, false);

        filenameText = view.findViewById(R.id.filename_text);
        okButton = view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(this);

        filenameText.setText(filename);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ok_button) {
            filename = filenameText.getText().toString();
            if (!filename.equals("")){
                onInputListener.sendInput(filename);
                dismiss();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            onInputListener = (OnInputListener) parentFragment;
            if (onInputListener == null) {
                Log.i("Dialog", "onAttach: " + "null");
            }
        } catch (ClassCastException e) {
            Log.e("RenameDialog", "Oops. " + e.getMessage());
        }
    }
}
