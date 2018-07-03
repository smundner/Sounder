package de.shm.sounderapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements WifiList.OnFragmentInteractionListener{
    private final String TAG ="MAIN";
    public EditText et;
    public WifiManager wm;
    Fragment mFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        WifiList fragment = new WifiList();
        fragmentTransaction.replace(R.id.main_fragment,fragment);
        fragmentTransaction.commit();

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        wm=(WifiManager) this.getApplicationContext().getSystemService(this.WIFI_SERVICE);
        this.registerReceiver(new wifiBroadcast(),new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wm.startScan();
        mFragment = fragment;

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public class wifiBroadcast extends BroadcastReceiver {

        public wifiBroadcast(){
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = wm.getScanResults();
            for (ScanResult item:results) {

                if(mFragment instanceof WifiList){
                    ((WifiList) mFragment).addItem(item.SSID);Log.d(TAG,item.SSID);
                }
            }

        }
    }

}
