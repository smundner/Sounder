package de.shm.sounderapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WifiList.OnWifiInteractionListener,Start.OnFragmentInteractionListener{
    private final String TAG ="SounderAppMain";
    public EditText et;
    public WifiManager wm;
    private WifiManager.WifiLock wl;
    Fragment mFragment;
    private Socket sounderSocket;
    private BufferedReader socket_reader;
    private DataOutputStream socket_writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Start fragment = new Start();

        fragmentTransaction.replace(R.id.main_fragment,fragment);
        fragmentTransaction.commit();

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        wm=(WifiManager) this.getApplicationContext().getSystemService(this.WIFI_SERVICE);
        //this.registerReceiver(new wifiBroadcast(),new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        this.registerReceiver(new WifiOnChange(),new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        wm.startScan();
        mFragment = fragment;
        connectToSounder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wl.release();
        if (sounderSocket!=null) {
            try {
                sounderSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onWifiInteraction(String ssid,String pass) {
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID="\""+ssid+"\"";
        wc.preSharedKey="\""+pass+"\"";
        wm.addNetwork(wc);

        List<WifiConfiguration> list = wm.getConfiguredNetworks();
        for(WifiConfiguration i: list){
            if(i.SSID!=null && i.SSID.equals("\""+ssid+"\"")){
                wm.disconnect();
                wm.enableNetwork(i.networkId,true);
                wm.reconnect();

                break;
            }
        }
        wl = wm.createWifiLock(TAG);

        wl.acquire();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private void connectToSounder(){
        WifiInfo wi = wm.getConnectionInfo();
        if(!wi.getSSID().equals("SounderBox")) {
            for(WifiConfiguration item:wm.getConfiguredNetworks()){
                if(item!=null&&item.SSID.equals("SounderBox")){
                    wm.enableNetwork(item.networkId,true);
                    break;
                }
            }
            wl = wm.createWifiLock(TAG);
            wl.acquire();

        }else if(sounderSocket==null) {
            //byte[] ip = {(byte)192,(byte)168,4,1};
            InetAddress ip = null;
            try {
                ip = InetAddress.getByName("192.168.4.1");
                sounderSocket = new Socket(ip,6200);
                socket_reader= new BufferedReader(new InputStreamReader(sounderSocket.getInputStream()));
                socket_writer= new DataOutputStream(sounderSocket.getOutputStream());
                Log.d(TAG,"Connected");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public class WifiOnChange extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            connectToSounder();
        }
    }

    public class wifiBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = wm.getScanResults();
            for (ScanResult item:results) {
                if(mFragment instanceof WifiList) ((WifiList) mFragment).addItem(item.SSID);Log.d(TAG,item.SSID);
            }


        }
    }


}
