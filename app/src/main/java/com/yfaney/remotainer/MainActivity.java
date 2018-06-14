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

public class MainActivity extends AppCompatActivity implements IBluetoothManager {
    public final static String KEY_CONTROLLER = "Controller";
    public final static String KEY_PLAYER = "Player";
    private static final int REQUEST_ENABLE_BLUETOOTH = 0x00000001;
    private String runningMode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkBluetoothAvailability();
    }

    private void checkBluetoothAvailability() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            finish(); //automatic close app if Bluetooth service is not available!
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    @Override
    public void onPairSuccess(String runningMode) {
        loadRemotainer(runningMode);
    }

    @Override
    public void onPairFailed(String failireReason) {

    }

    @Override
    public void onReceiveDiscoveryResults(final BluetoothDevice device) {

    }

    @Override
    public void onDiscoveryFinished() {

    }

    public void onClickController(View view) {
        loadRemotainer(KEY_CONTROLLER);
    }

    public void onClickPlayer(View view) {
        loadRemotainer(KEY_PLAYER);
    }

    private void loadRemotainer(String runningMode) {
        boolean isSuccessfullyPaird = false;
        if (PreferenceManager.getPrePairdDevice() != null) {
            final BluetoothManager bluetoothManager = BluetoothManager.getSingleton();
            isSuccessfullyPaird = bluetoothManager.isSuccessfullyPaired();
        }
        if (isSuccessfullyPaird) {
            if (KEY_CONTROLLER.equals(runningMode)) {
                //Load Controller Activity
                loadControllerService();
            } else if (KEY_PLAYER.equals(runningMode)) {
                //Load Player Activity
                loadPlayerService();
            }
        } else {
            final BluetoothManager bluetoothManager = BluetoothManager.getSingleton();
            bluetoothManager.pairDevice(runningMode);
        }
    }

    private void loadPlayerService() {
        //TODO Implement Calling Player Service

    }

    private void loadControllerService() {
        //TODO Implement Calling Controller Service

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
            else {
                Toast.makeText(this, "Bluetooth needs to be enabled to use this app.", Toast.LENGTH_SHORT).show();
                finish(); //automatic close app if Bluetooth service is not enabled!
            }
        }
    }

}
