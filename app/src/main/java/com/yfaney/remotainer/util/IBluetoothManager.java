package com.yfaney.remotainer.util;

public interface IBluetoothManager {
    void onPairSuccess(String runningMode);

    void onPairFailed(String failureReason);
}
