package com.android.miki.rpiplantsapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Miki on 8/12/2016.
 */
public class SingleEditDialog extends DialogFragment {
    SingleEditDialogListener mListener;
    private static final String PUB_OR_SUB_OR_CHANNEL = "pubOrSub";
    private static final String EDIT_TEXT_CONTENT = "editTextContent";

    public interface SingleEditDialogListener{
        void onDialogPositiveClick(String pubOrSub, String newKey);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.single_edit_dialog, null);
        final EditText mEditText = (EditText) view.findViewById(R.id.single_edit_text);
        final TextView dialogTitle = (TextView)view.findViewById(R.id.single_edit_dialog_title);

        // Set the title of dialog, and editText content, from data received from SettingsActivity.
        Bundle data = getArguments();
        String title = data.getString(PUB_OR_SUB_OR_CHANNEL);
        String editTextContent = data.getString(EDIT_TEXT_CONTENT);
        dialogTitle.setText(title);
        mEditText.setText(editTextContent);
        mEditText.selectAll();

        builder.setView(view);
        //mEditText.setText();

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    String newKey = mEditText.getText().toString();
                    mListener.onDialogPositiveClick(getTag(), newKey);

            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        return builder.create();
    }

    public static SingleEditDialog newInstance(String dialogTitle, String editTextContent){
        SingleEditDialog dialog = new SingleEditDialog();
        Bundle args = new Bundle();
        args.putString(PUB_OR_SUB_OR_CHANNEL, dialogTitle);
        args.putString(EDIT_TEXT_CONTENT, editTextContent);
        dialog.setArguments(args);

        return dialog;
    }
}
