package com.yfaney.remotainer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yfaney.remotainer.util.BluetoothManager;
import com.yfaney.remotainer.util.IBluetoothManager;
import com.yfaney.remotainer.util.PreferenceManager;

public class MainActivity extends AppCompatActivity implements IBluetoothManager {
    public final static String KEY_CONTROLLER = "Controller";
    public final static String KEY_PLAYER = "Player";
    private String runningMode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            } else if (KEY_PLAYER.equals(runningMode)) {
                //Load Player Activity
            }
        } else {
            final BluetoothManager bluetoothManager = BluetoothManager.getSingleton();
            bluetoothManager.pairDevice(runningMode);
        }
    }

    @Override
    public void onPairSuccess(String runningMode) {
        loadRemotainer(runningMode);
    }

    @Override
    public void onPairFailed(String failireReason) {

    }
}
