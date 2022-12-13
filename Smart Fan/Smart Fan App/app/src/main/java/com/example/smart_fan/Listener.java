package com.example.smart_fan;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public interface Listener {
    void Device_info(BluetoothDevice ble_device);
}
