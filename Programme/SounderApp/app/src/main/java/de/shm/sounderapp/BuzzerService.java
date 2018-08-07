package de.shm.sounderapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Scanner;


public class BuzzerService extends Service{
    public final String TAG = "BUZZER_SERVICE";

    private volatile boolean keepRunning=true;
    private Thread ListenerThread;
    private volatile ServerSocket serverSocket=null;
    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder nr = new NetworkRequest.Builder();
        nr.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);

        cm.requestNetwork(nr.build(),new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                cm.bindProcessToNetwork(network);
                ListenerThread = new Thread(new SocketListenet(network));
                if(ListenerThread!=null){
                    if(Thread.State.RUNNABLE != ListenerThread.getState()){
                        ListenerThread.start();}
                }
            }
        });


        return super.onStartCommand(intent, flags, startId);
    }

    class SocketListenet implements Runnable{

        Network mNetwork;

        public SocketListenet(Network network){
            mNetwork=network;
        }

        @Override
        public void run() {
            Log.d(TAG,"Thread number: "+Thread.currentThread().getName());
            try {

                InetAddress ip = InetAddress.getByName("192.168.4.1");
                Socket sounderSocket = mNetwork.getSocketFactory().createSocket(ip,6200);//(ip,6200);
                DataOutputStream socket_writer = new DataOutputStream(sounderSocket.getOutputStream());
                socket_writer.writeUTF("Hallo ich habs geschaft\n");
                socket_writer.close();
                sounderSocket.close();
                Log.d(TAG,"Message sended");
                serverSocket = new ServerSocket(6201);
            } catch (IOException e) {
                e.printStackTrace();
            }


            while(keepRunning){
                Log.d(TAG,"Running");
                try {
                    Socket client = serverSocket.accept();
                    Scanner in = new Scanner(client.getInputStream());
                    if (in!=null) {
                        String read = in.nextLine();
                        if (!(read==null)) {
                            Log.d(TAG,read);
                            switch (read)
                            {
                                case"Alarm1":
                                    Log.d("Alarm","Alarm1");
                                    break;
                                case "Alarm2":
                                    Log.d("Alarm","Alarm2");
                                    break;
                                case "Alarm3":
                                    Log.d("Alarm","Alarm3");
                                    break;
                                case "Alarm4":
                                    Log.d("Alarm","Alarm4");
                                    break;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(serverSocket!=null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        keepRunning=false;
        try {
            if (serverSocket!=null) {
                serverSocket.close();
                serverSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"Thread closed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
