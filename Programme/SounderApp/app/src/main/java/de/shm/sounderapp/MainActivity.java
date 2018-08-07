package de.shm.sounderapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;




public class MainActivity extends AppCompatActivity implements Start.OnFragmentInteractionListener{

    private final static String TAG ="SounderAppMain";
    private final String SSID ="\"SounderBox\"";


    Fragment mFragment;

    private WifiManager mWifiManager;
    private WifiManagerBroadcast mWifiManagerBroadcast;
    private WifiManager.WifiLock mWifiLock;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragment = new Start();
        fragmentTransaction.replace(R.id.main_fragment, mFragment);
        fragmentTransaction.commit();

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_SETTINGS}, 1);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);


        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //mWifiManagerBroadcast = new WifiManagerBroadcast();
        //registerReceiver(mWifiManagerBroadcast,intentFilter);

        if (mServiceIntent != null) getApplicationContext().stopService(mServiceIntent); mServiceIntent=null;
        Log.d(TAG, "Connect to network");
        if (mServiceIntent==null) {

            mServiceIntent = new Intent(getApplicationContext(), BuzzerService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startService(mServiceIntent);
            }
        }


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWifiManagerBroadcast!=null)unregisterReceiver(mWifiManagerBroadcast);
        if(mWifiLock!=null&& mWifiLock.isHeld())mWifiLock.release();

    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private void connectToSounder(){

        int nId=-1;
        boolean isIn=false;
        for(WifiConfiguration item : mWifiManager.getConfiguredNetworks()){
            if(item.SSID.equals(SSID)){
                nId=item.networkId;
                break;
            }
        }
        Log.d(TAG,"Try to Connect");

        if (nId<0) {
            WifiConfiguration wc = new WifiConfiguration();
            wc.SSID=SSID;
            wc.preSharedKey=SSID;
            nId = mWifiManager.addNetwork(wc);
        }
        mWifiManager.enableNetwork(nId,true);
        mWifiManager.reconnect();

        mWifiLock = mWifiManager.createWifiLock(TAG);
        if(mWifiLock.isHeld())  mWifiLock.acquire();

    }





    class WifiManagerBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                if (mWifiManager.getConnectionInfo().getSupplicantState().toString().equals("DISCONNECTED") || !(mWifiManager.getConnectionInfo().getSSID().equals(SSID))) {
                    Log.d(TAG, mWifiManager.getConnectionInfo().getSSID());
                    Log.d(TAG, mWifiManager.getConnectionInfo().getSupplicantState().toString());
                    connectToSounder();
                }

            } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) && mWifiManager.getConnectionInfo().getSupplicantState().toString().equals("COMPLETED")) {
                if (mServiceIntent != null) getApplicationContext().stopService(mServiceIntent); mServiceIntent=null;
                Log.d(TAG, "Connect to network");
                if (mServiceIntent==null) {
                    mServiceIntent = new Intent(getApplicationContext(), BuzzerService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getApplicationContext().startService(mServiceIntent);
                    }
                }
            }
        }
    }
}
