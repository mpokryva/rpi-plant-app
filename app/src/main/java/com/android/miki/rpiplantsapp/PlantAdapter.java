package com.android.miki.rpiplantsapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Miki on 7/21/2016.
 */
public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.ViewHolder>{
    private String[] mDataset;
    private OnItemClickListener mListener;

    /**
     * Interface for receiving click events from cells
     */
    public interface OnItemClickListener {
        public void onClick (View view, int position);
    }

    /**
     * Custom viewholder for our planet views
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    public PlantAdapter(String[] myDataset, OnItemClickListener listener){
        mDataset = myDataset;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        View v = vi.inflate(R.layout.drawer_list_item, parent, false);
        TextView tv = (TextView) v.findViewById(R.id.drawer_item_text);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        holder.mTextView.setText(mDataset[position]);
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount(){
        return mDataset.length;
    }
}
