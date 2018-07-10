package com.yfaney.remotainer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yfaney.remotainer.util.BluetoothManager;
import com.yfaney.remotainer.util.IBluetoothManager;
import com.yfaney.remotainer.util.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public final static String KEY_CONTROLLER = "Controller";
    public final static String KEY_PLAYER = "Player";
    public final static String REMOTAINER_PLAYER_PREFIX = "FP-";
    private static final int REQUEST_ENABLE_BLUETOOTH = 0x00000001;
    private static final int REQUEST_DOPAIR_BLUETOOTH = 0x00000010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkBluetoothAvailability();

        String runningMode = PreferenceManager.getRunningMode();
        if(KEY_PLAYER.equals(runningMode)){
            loadRemotainer(runningMode);
        }
    }

    /**
     * Check Bluetooth Availability and open Bluetooth Setting if necessary.
     */
    private void checkBluetoothAvailability() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            finish(); //automatic close app if Bluetooth service is not available!
        }
    }

    public void onClickController(View view) {
        loadRemotainer(KEY_CONTROLLER);
    }

    public void onClickPlayer(View view) {
        loadRemotainer(KEY_PLAYER);
    }

    private void loadRemotainer(String runningMode) {
        boolean isSuccessfullyPaird = false;
        final BluetoothManager bluetoothManager = BluetoothManager.getSingleton();
        if (PreferenceManager.getPrePairdDevice() != null) {
            isSuccessfullyPaird = bluetoothManager.isSuccessfullyPaired();
        }
        if (isSuccessfullyPaird) {
            if (KEY_CONTROLLER.equals(runningMode)) {
                //Load Controller Activity
                Set<BluetoothDevice> pairedDevices = new HashSet<>();
                for (BluetoothDevice device : bluetoothManager.getAvailablePeers()){
                    String device_name = device.getName();
                    if(device_name.startsWith(REMOTAINER_PLAYER_PREFIX)){
                        pairedDevices.add(device);
                    }
                    loadControllerService(pairedDevices);
                }
            } else if (KEY_PLAYER.equals(runningMode)) {
                //Load Player Activity
                loadPlayerService();
            }
        } else {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    private void loadPlayerService() {
        //1. Save the current mode so that we can automatically run this mode next time
        PreferenceManager.setRunningMode(KEY_PLAYER);
        //2. Run Player service. No need to pass the paired bluetooth devices.
        //TODO Implement Calling Player Service
    }

    private void loadControllerService(final Set<BluetoothDevice> pairedDevices) {
        //1. Save the current mode so that we can automatically run this mode next time
        PreferenceManager.setRunningMode(KEY_CONTROLLER);
        //2. Pass the paired BluetoothDevice instance
        PreferenceManager.setPrePairdDevices(pairedDevices);
        //3. Run Controller service
        //TODO Implement Calling Controller Service
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                //Now Bluetooth enabled. Do nothing.
            } else {
                Toast.makeText(this, "Bluetooth needs to be enabled to use this app.", Toast.LENGTH_SHORT).show();
                finish(); //automatic close app if Bluetooth service is not enabled!
            }
        } else if (requestCode == REQUEST_DOPAIR_BLUETOOTH) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

            } else {
                Toast.makeText(this, "The device needs to be paired to use this app.", Toast.LENGTH_SHORT).show();
                finish(); //automatic close app if Bluetooth service is not enabled!
            }

        }
    }

}
