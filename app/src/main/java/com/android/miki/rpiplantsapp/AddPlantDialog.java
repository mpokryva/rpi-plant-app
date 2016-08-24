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
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
    private EditText lightGPIOEdit;
    private EditText moistureGPIOEdit;
    private EditText tempGPIOEdit;
    private DBHandler mDBHandler;
    AddPlantDialogListener mListener;
    public static final String ADD_PLANT_DIALOG_TAG = "AddPlantDialog";

    public interface AddPlantDialogListener{
        void onDialogPositiveClick(Plant newPlant);
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
        lightGPIOEdit = (EditText) view.findViewById(R.id.gpio_light_edit);
        moistureGPIOEdit = (EditText) view.findViewById(R.id.gpio_moisture_edit);
        tempGPIOEdit = (EditText) view.findViewById(R.id.gpio_temp_edit);

        builder.setView(view);
                builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedPlantName = plantNameEdit.getText().toString();
                        String selectedPlantSpecies = plantSpeciesEdit.getText().toString();
                        double selectedOptimalTemp = Double.parseDouble(optimalTempEdit.getText().toString());
                        double selectedOptimalMoisture = Double.parseDouble(optimalMoistureEdit.getText().toString());
                        double selectedOptimalLight = Double.parseDouble(optimalLightEdit.getText().toString());
                        double lightGPIO = Double.parseDouble(lightGPIOEdit.getText().toString());
                        double moistureGPIO = Double.parseDouble(moistureGPIOEdit.getText().toString());
                        double tempGPIO = Double.parseDouble(tempGPIOEdit.getText().toString());

                        /**
                         * May be buggy.
                         */
                        Plant newPlant = new Plant(selectedPlantName, selectedPlantSpecies);
                        newPlant.getLightFrag().getStat().setOptimalLevel(selectedOptimalLight);
                        newPlant.getMoistureFrag().getStat().setOptimalLevel(selectedOptimalMoisture);
                        newPlant.getTempFrag().getStat().setOptimalLevel(selectedOptimalTemp);
                        newPlant.setLightGPIO(lightGPIO);
                        newPlant.setMoistureGPIO(moistureGPIO);
                        newPlant.setTempGPIO(tempGPIO);

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
