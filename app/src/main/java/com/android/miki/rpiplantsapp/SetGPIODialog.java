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
 * Created by Miki on 8/15/2016.
 */
public class SetGPIODialog extends DialogFragment {

    private DBHandler mDBHandler;
    DialogListener mListener;
    private Plant mPlant;
    private int menuItemIndex;
    public static final String SET_GPIO_DIALOG_TAG = "SetGPIODialog";



    public interface SetGPIODialogListener{
        void onDialogPositiveClick();
        void onDialogPositiveClick(String pubOrSub, String newKey);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        this.setRetainInstance(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_set_gpio, null);

        final EditText lightGPIOEdit = (EditText) view.findViewById(R.id.gpio_light_edit);
        final EditText moistureGPIOEdit = (EditText) view.findViewById(R.id.gpio_moisture_edit);
        final EditText tempGPIOEdit = (EditText) view.findViewById(R.id.gpio_temp_edit);

        lightGPIOEdit.setText(String.valueOf(mPlant.getLightGPIO()));
        moistureGPIOEdit.setText(String.valueOf(mPlant.getMoistureGPIO()));
        tempGPIOEdit.setText(String.valueOf(mPlant.getTempGPIO()));

        lightGPIOEdit.selectAll();
        moistureGPIOEdit.selectAll();
        tempGPIOEdit.selectAll();



        // Set the title of dialog from data received from SettingsActivity


        builder.setView(view);
        //mEditText.setText();

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int lightGPIO = Integer.parseInt(lightGPIOEdit.getText().toString());
                int moistureGPIO = Integer.parseInt(moistureGPIOEdit.getText().toString());
                int tempGIO = Integer.parseInt(tempGPIOEdit.getText().toString());

                mDBHandler = new DBHandler(getActivity(), null, null, 1);
                // Deleting plant from database BEFORE changes.
                mDBHandler.deletePlant(mPlant.getPlantName());

                // Changing plant attributes
                if (mPlant != null){
                    mPlant.setLightGPIO(lightGPIO);
                    mPlant.setMoistureGPIO(moistureGPIO);
                    mPlant.setTempGPIO(tempGIO);
                }

                // Adding changed plant
                mDBHandler.addPlant(mPlant);
                mDBHandler.close();
                if (getArguments() != null){
                    menuItemIndex = getArguments().getInt(PlantStatsActivity.PLANTS_MENU_INDEX_KEY);
                }
                mListener.onDialogPositiveClick(getTag(), menuItemIndex, mPlant);


            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

    public static SetGPIODialog newInstance(int itemIndex){
        SetGPIODialog dialog = new SetGPIODialog();
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
