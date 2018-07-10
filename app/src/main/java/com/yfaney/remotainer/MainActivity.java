package com.yfaney.remotainer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yfaney.remotainer.util.BluetoothManager;
import com.yfaney.remotainer.util.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public final static String KEY_CONTROLLER = "Controller";
    public final static String KEY_PLAYER = "Player";
    public final static String REMOTAINER_PLAYER_PREFIX = "FP-";
    private static final int REQUEST_ENABLE_WIFI = 0x00000001;
    private static final int REQUEST_DOPAIR_BLUETOOTH = 0x00000010;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private WiFiDirectBroadcastReceiver receiver;
    private boolean isWifiP2pEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver();
        receiver.setActivity(this);
        receiver.setWiFiDirectProperties(mManager, mChannel);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkWiFiAvailability();

        String runningMode = PreferenceManager.getRunningMode();
        if(KEY_PLAYER.equals(runningMode)){
            loadRemotainer(runningMode);
        }
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /**
     * Check WiFi Availability and open Bluetooth Setting if necessary.
     */
    private void checkWiFiAvailability() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && !wifiManager.isWifiEnabled()) {
            Intent enableIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            startActivityForResult(enableIntent, REQUEST_ENABLE_WIFI);
        } else {
            Toast.makeText(this, "WiFi is not available!", Toast.LENGTH_SHORT).show();
            finish(); //automatic close app if WiFi service is not available!
        }
    }

    public void onClickController(View view) {
        loadRemotainer(KEY_CONTROLLER);
    }

    public void onClickPlayer(View view) {
        loadRemotainer(KEY_PLAYER);
    }

    private void loadRemotainer(String runningMode) {
        if (KEY_CONTROLLER.equals(runningMode)) {
            mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if(!group.getNetworkName().startsWith(REMOTAINER_PLAYER_PREFIX)){
                        return;
                    }
                    final Set<WifiP2pDevice> pairedDevices = new HashSet<>();
                    String groupPassword = group.getPassphrase();
                    for(WifiP2pDevice device : group.getClientList()){
                        String device_name = device.deviceName;
                        if(device_name.startsWith(REMOTAINER_PLAYER_PREFIX)){
                            pairedDevices.add(device);
                        }
                        loadControllerService(pairedDevices);
                    }
                }
            });
        } else if (KEY_PLAYER.equals(runningMode)) {
            //Load Player Activity
            loadPlayerService();
        }
    }

    private void loadPlayerService() {
        //1. Save the current mode so that we can automatically run this mode next time
        PreferenceManager.setRunningMode(KEY_PLAYER);
        //2. Run Player service. No need to pass the paired bluetooth devices.
        //TODO Implement Calling Player Service
    }

    private void loadControllerService(final Set<WifiP2pDevice> pairedDevices) {
        //1. Save the current mode so that we can automatically run this mode next time
        PreferenceManager.setRunningMode(KEY_CONTROLLER);
        //2. Pass the paired BluetoothDevice instance
        //PreferenceManager.setPrePairdDevices(pairedDevices);
        //3. Run Controller service
        //TODO Implement Calling Controller Service
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_WIFI) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                //Now Bluetooth enabled. Do nothing.
            } else {
                Toast.makeText(this, "WiFi needs to be enabled to use this app.", Toast.LENGTH_SHORT).show();
                finish(); //automatic close app if WiFi service is not enabled!
            }
        } else if (requestCode == REQUEST_DOPAIR_BLUETOOTH) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

            } else {
                Toast.makeText(this, "The device needs to be paired to use this app.", Toast.LENGTH_SHORT).show();
                finish(); //automatic close app if WiFi service is not enabled!
            }

        }
    }

    public void setIsWifiP2pEnabled(final boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    public boolean getIsWifiP2pEnabled() {
        return isWifiP2pEnabled;
    }
}
