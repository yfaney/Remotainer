package com.yfaney.remotainer.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Set;

public class BluetoothManager {
    private IBluetoothManager iBluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (iBluetoothManager != null) {
                        iBluetoothManager.onReceiveDiscoveryResults(device);
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (iBluetoothManager != null) {
                    iBluetoothManager.onDiscoveryFinished();
                }
            }
        }
    };

    private BluetoothManager() {
    }

    public static BluetoothManager getSingleton() {
        return new BluetoothManager();
    }

    public void setEventListener(final IBluetoothManager iBluetoothManager) {
        this.iBluetoothManager = iBluetoothManager;
    }

    public boolean isSuccessfullyPaired() {
        return false;
    }

    public void pairDevice(String runningMode) {
        iBluetoothManager.onPairSuccess(runningMode);
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public void setBluetoothAdapter(final BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public Set<BluetoothDevice> getAvailablePeers() {
        return bluetoothAdapter.getBondedDevices();
    }

    public void discoverDevices() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    public BroadcastReceiver getDiscoveryFinishReceiver() {
        return discoveryFinishReceiver;
    }
}
