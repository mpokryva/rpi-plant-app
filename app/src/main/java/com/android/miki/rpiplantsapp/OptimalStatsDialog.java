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
public class OptimalStatsDialog extends DialogFragment {

    private EditText optimalTempEdit;
    private EditText optimalMoistureEdit;
    private EditText optimalLightEdit;
    private DBHandler mDBHandler;
    DialogListener mListener;
    private Plant mPlant;
    private int menuItemIndex;
    public static final String OPTIMAL_STATS_DIALOG_TAG = "OptimalStatsDialog";




    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        this.setRetainInstance(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_optimal_stats, null);
        optimalTempEdit = (EditText) view.findViewById(R.id.optimal_temp_edit);
        optimalMoistureEdit = (EditText) view.findViewById(R.id.optimal_moisture_edit);
        optimalLightEdit = (EditText) view.findViewById(R.id.optimal_light_edit);

        optimalLightEdit.setText(String.valueOf(mPlant.getLightFrag().getStat().getOptimalLevel()));
        optimalMoistureEdit.setText(String.valueOf(mPlant.getMoistureFrag().getStat().getOptimalLevel()));
        optimalTempEdit.setText(String.valueOf(mPlant.getTempFrag().getStat().getOptimalLevel()));

        optimalLightEdit.selectAll();
        optimalMoistureEdit.selectAll();
        optimalTempEdit.selectAll();


        builder.setView(view);
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedOptimalTemp = Integer.parseInt(optimalTempEdit.getText().toString());
                int selectedOptimalMoisture = Integer.parseInt(optimalMoistureEdit.getText().toString());
                int selectedOptimalLight = Integer.parseInt(optimalLightEdit.getText().toString());

                mDBHandler = new DBHandler(getActivity(), null, null, 1);
                // Deleting plant from database BEFORE changes.
                mDBHandler.deletePlant(mPlant.getPlantName());

                // Changing plant attributes
                if (mPlant !=null) {
                    mPlant.getLightFrag().getStat().setOptimalLevel(selectedOptimalLight);
                    mPlant.getMoistureFrag().getStat().setOptimalLevel(selectedOptimalMoisture);
                    mPlant.getTempFrag().getStat().setOptimalLevel(selectedOptimalTemp);
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

    public static OptimalStatsDialog newInstance(int itemIndex){
        OptimalStatsDialog dialog = new OptimalStatsDialog();
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
