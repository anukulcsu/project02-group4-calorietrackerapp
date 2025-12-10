package com.example.calorietracker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class TargetFragment extends DialogFragment {
    public interface OnTargetSavedListener {
        void onTargetSaved(int newTarget);
    }

    private OnTargetSavedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnTargetSavedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTargetSavedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_edit_target, null);
        EditText etNewTarget = view.findViewById(R.id.etNewTarget);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        btnSave.setOnClickListener(v -> {
            String input = etNewTarget.getText().toString();
            if (!input.isEmpty()) {
                int target = Integer.parseInt(input);
                listener.onTargetSaved(target);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Please enter a value", Toast.LENGTH_SHORT).show();
            }
        });
        btnCancel.setOnClickListener(v -> dismiss());
        builder.setView(view);
        return builder.create();
    }
}