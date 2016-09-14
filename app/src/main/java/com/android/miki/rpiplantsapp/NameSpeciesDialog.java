package com.android.miki.rpiplantsapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Miki on 8/16/2016.
 */
public class NameSpeciesDialog extends DialogFragment{
    private EditText plantNameEdit;
    private EditText plantSpeciesEdit;
    private DBHandler mDBHandler;
    DialogListener mListener;
    private Plant mPlant;
    private int menuItemIndex;
    public static final String NAME_SPECIES_DIALOG_TAG = "NameSpeciesDialog";




    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        this.setRetainInstance(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_name_species, null);
        plantNameEdit = (EditText) view.findViewById(R.id.plant_name_edit);
        plantSpeciesEdit = (EditText) view.findViewById(R.id.plant_species_edit);

        plantNameEdit.setText(mPlant.getPlantName());
        plantSpeciesEdit.setText(mPlant.getPlantSpecies());

        plantNameEdit.selectAll();
        plantSpeciesEdit.selectAll();


        builder.setView(view);
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedPlantName = plantNameEdit.getText().toString();
                String selectedPlantSpecies = plantSpeciesEdit.getText().toString();

                mDBHandler = new DBHandler(getActivity(), null, null, 1); //getActivity() may be buggy. Other 3 params are irrelevant
                // Deleting plant from database BEFORE changes.
                mDBHandler.deletePlant(mPlant.getPlantName());

                // Changing plant attributes
                if (mPlant != null){
                    mPlant.setPlantName(plantNameEdit.getText().toString());
                    mPlant.setPlantSpecies(plantSpeciesEdit.getText().toString());
                }

                // Adding changed plant
                mDBHandler.addPlant(mPlant);
                mDBHandler.close();
                if (getArguments() != null){
                    menuItemIndex = getArguments().getInt(PlantStatsActivity.PLANTS_MENU_INDEX_KEY);
                }
                mListener.onDialogPositiveClick(getTag(), menuItemIndex, mPlant);


            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

        return builder.create();
    }

    public void setPlant(Plant plant){
        mPlant = plant;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface){
        getActivity();
        super.onDismiss(dialogInterface);

    }

    public static NameSpeciesDialog newInstance(int itemIndex){
        NameSpeciesDialog dialog = new NameSpeciesDialog();
        Bundle args = new Bundle();
        args.putInt(PlantStatsActivity.PLANTS_MENU_INDEX_KEY, itemIndex);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        // Verify that activity implements the listener
        try {
            // Instantiate the AddPlantDialogListener so we can send events to host
            mListener = (DialogListener) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement " +
                    "DialogListener interface");
        }
    }
}
