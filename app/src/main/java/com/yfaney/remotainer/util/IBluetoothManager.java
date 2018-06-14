package com.yfaney.remotainer.util;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public interface IBluetoothManager {
    void onPairSuccess(String runningMode);

    void onPairFailed(String failureReason);

    void onReceiveDiscoveryResults(BluetoothDevice device);
    void onDiscoveryFinished();
}
