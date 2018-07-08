package de.shm.sounderapp;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.ViewHolder> {
    OnItemClickListener mListener;
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


    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Position: " + getAdapterPosition());
                    if(mListener!=null) mListener.onItemClickListener(mDataSet.get(getAdapterPosition()));
                }
            });
            textView = (TextView) itemView.findViewById(R.id.wifiListItemText);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public interface OnItemClickListener{
        void onItemClickListener(String data);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;

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
