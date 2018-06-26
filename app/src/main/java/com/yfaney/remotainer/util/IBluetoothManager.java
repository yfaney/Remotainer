package com.yfaney.remotainer.util;

import android.bluetooth.BluetoothDevice;

public interface IBluetoothManager {
    void onReceiveDiscoveryResults(BluetoothDevice device);

    void onDiscoveryFinished();
}
