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
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Miki on 7/26/2016.
 */
public class AddPlantDialog extends DialogFragment {
    private EditText plantNameEdit;
    private EditText plantSpeciesEdit;
    private EditText optimalTempEdit;
    private EditText optimalMoistureEdit;
    private EditText optimalLightEdit;
    private DBHandler mDBHandler;
    AddPlantDialogListener mListener;

    public interface AddPlantDialogListener{
        public void onDialogPositiveClick(Plant newPlant);
        //public void onDialogNegativeClick(); //Not sure if needed
    }
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_plant_dialog, null);
        plantNameEdit = (EditText) view.findViewById(R.id.plant_name_edit);
        plantSpeciesEdit = (EditText) view.findViewById(R.id.plant_species_edit);
        optimalTempEdit = (EditText) view.findViewById(R.id.optimal_temp_edit);
        optimalMoistureEdit = (EditText) view.findViewById(R.id.optimal_moisture_edit);
        optimalLightEdit = (EditText) view.findViewById(R.id.optimal_light_edit);
        builder.setView(view);
                //.setTitle(R.string.add_plant) // Remove either message or title
                builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedPlantName = plantNameEdit.getText().toString();
                        String selectedPlantSpecies = plantSpeciesEdit.getText().toString();
                        int selectedOptimalTemp = Integer.parseInt(optimalTempEdit.getText().toString());
                        int selectedOptimalMoisture = Integer.parseInt(optimalMoistureEdit.getText().toString());
                        int selectedOptimalLight = Integer.parseInt(optimalLightEdit.getText().toString());

                        /**
                         * May be buggy.
                         */
                        Plant newPlant = new Plant(selectedPlantName, selectedPlantSpecies);
                        newPlant.getLightFrag().getStat().setOptimalLevel(selectedOptimalLight);
                        newPlant.getMoistureFrag().getStat().setOptimalLevel(selectedOptimalMoisture);
                        newPlant.getTempFrag().getStat().setOptimalLevel(selectedOptimalTemp);

                        mDBHandler = new DBHandler(getActivity(), null, null, 1); //getActivity() may be buggy. Other 3 params are irrelevant
                        mDBHandler.addPlant(newPlant);
                        mDBHandler.close();
                        mListener.onDialogPositiveClick(newPlant);


                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });


        //return createdDialog;
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        // Verify that activity implements the listener
        try {
            // Instantiate the AddPlantDialogListener so we can send events to host
            mListener = (AddPlantDialogListener) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement " +
                    "AddPlantDialogListener interface");
        }
    }



}
