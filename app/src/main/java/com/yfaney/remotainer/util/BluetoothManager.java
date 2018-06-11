package com.yfaney.remotainer.util;

public class BluetoothManager {
    private IBluetoothManager iBluetoothManager;
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
}
