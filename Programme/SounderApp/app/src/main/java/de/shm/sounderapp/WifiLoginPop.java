package de.shm.sounderapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class WifiLoginPop extends DialogFragment{

    private String mData;
    private EditText ssidText;
    private TextView title;
    private Button ok;
    private Button cancel;

    private OkClickListener mListener;

    public WifiLoginPop(){}

    public static WifiLoginPop newInstance(String title){
        WifiLoginPop frag = new WifiLoginPop();
        Bundle args = new Bundle();
        args.putString("title",title);
        frag.setArguments(args);
        return frag;
    }

    public interface OkClickListener{
        void okClickListener(String ssid, String password);
    }

    public void setOkClickListener(OkClickListener listener){
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi_login_pop,container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        ssidText = (EditText) view.findViewById(R.id.wifiPasswordText);
        title = (TextView) view.findViewById(R.id.wifiPasswordTitle);
        ok = (Button) view.findViewById(R.id.wifiPasswordOk);
        cancel = (Button) view.findViewById(R.id.wifiPasswordCancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null)mListener.okClickListener(getArguments().getString("title"),ssidText.getText().toString());
                WifiLoginPop.this.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiLoginPop.this.dismiss();
            }
        });

        title.setText(getArguments().getString("title"));
        getDialog().setTitle(getArguments().getString("title","enter_password"));
        ssidText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

}
