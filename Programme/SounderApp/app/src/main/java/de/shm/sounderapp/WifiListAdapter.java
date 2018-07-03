package de.shm.sounderapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_list_item, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG,"position: "+ position);
        holder.getTextView().setText(mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    private static final String TAG = "WiFiListAdapter";
    private ArrayList<String> mDataSet = new ArrayList<String>();

    public WifiListAdapter(){

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG,Integer.toString(getAdapterPosition()));
                }
            });
            textView = (TextView) itemView.findViewById(R.id.wifiListItemText);
        }

        public TextView getTextView() {
            return textView;
        }
    }




    public void addItem(String newItem) {
        for(String item:mDataSet){
            if(item.equals(newItem))return;
        }
        mDataSet.add(newItem);
        notifyItemInserted(mDataSet.size()-1);
    }
    public void removeItem(int position){
        mDataSet.remove(position);
        notifyItemRemoved(position);
    }
}
