package com.example.smart_fan;


import android.bluetooth.BluetoothDevice;

public class Device_Type {
    private String name;
    private String address;
    private BluetoothDevice device;

    public Device_Type() {}

    public String getName() {return name;}
    public String getAddress() {
        return address;
    }
    public BluetoothDevice getDevice() {
        return device;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAddress(String address) {this.address = address;}
    public void setDevice(BluetoothDevice device){this.device = device;}
}