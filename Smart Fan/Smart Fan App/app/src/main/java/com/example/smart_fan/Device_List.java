package com.example.smart_fan;

import static java.lang.System.out;
import static java.lang.Thread.sleep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;

public class Device_List extends Dialog {
    Handler Handler = new Handler(Looper.getMainLooper());
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private ListView Device_Search;
    private Device_Adapter adapter;
    private Device_Type item = new Device_Type();
    private BluetoothGatt ble_gatt_;
    private boolean mScanning = true;
    Listener listener;
    public Device_List(Context context,Listener listener) {
        super(context);
        this.listener = listener;
    }
    private static final long SCAN_PERIOD = 10000;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);
        bluetoothManager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        //dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Device_Search = findViewById(R.id.device_recycler);
        Device_Search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(),adapter.getItem(position).getName(),Toast.LENGTH_SHORT).show();
                connectDevice(adapter.getItem(position).getDevice());
            }
        });
        adapter = new Device_Adapter();
        scanLeDevice(mScanning);
    }
    @SuppressLint("MissingPermission")
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            Handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                }
            }, SCAN_PERIOD);
            mScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            Handler.post(new Runnable() {
                @Override
                public void run() {
                    if(device.getName() != null){
                        adapter.addItem(device.getName(), device.getAddress(),device);
                    }
                    Device_Search.setAdapter(adapter);
                }
            });
        }
    };
    private class GattClientCallback extends BluetoothGattCallback {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt _gatt, int _status, int _new_state) {
            super.onConnectionStateChange(_gatt, _status, _new_state);
            if (_status == BluetoothGatt.GATT_FAILURE) {
                Log.d( "실패", "Closing Gatt connection" );
                disconnectGattServer();
                return;
            } else if (_status != BluetoothGatt.GATT_SUCCESS) {
                Log.d( "실패", "Closing Gatt connection" );
                disconnectGattServer();
                return;
            }
            if (_new_state == BluetoothProfile.STATE_CONNECTED) {
                // update the connection status message
                // set the connection flag
                Log.d("성공", "Connected to the GATT server");
                listener.changeText(ble_gatt_.getDevice().getName());
                dismiss();
            } else if (_new_state == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d( "실패", "Closing Gatt connection" );
                disconnectGattServer();
            }
        }
    }
    public void disconnectGattServer() {
        Log.d( "종료", "Closing Gatt connection" );
        // reset the connection flag
        // disconnect and close the gatt
        if( ble_gatt_ != null ) {
            ble_gatt_.disconnect();
            ble_gatt_.close();
        }
    }
    @SuppressLint("MissingPermission")
    private void connectDevice(BluetoothDevice _device ) {
        // update the status
        GattClientCallback gatt_client_cb= new GattClientCallback();
        ble_gatt_= _device.connectGatt( getContext(), false, gatt_client_cb );
    }
}


