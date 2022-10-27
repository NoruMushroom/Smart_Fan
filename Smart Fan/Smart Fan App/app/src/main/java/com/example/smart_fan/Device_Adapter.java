package com.example.smart_fan;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class Device_Adapter extends BaseAdapter {
    private ArrayList<Device_Type> Devices = new ArrayList<>();
    public Device_Adapter(){

    }
    @Override
    public int getCount() {
        return Devices.size();
    }

    @Override
    public Device_Type getItem(int position) {
        return Devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        Context context = parent.getContext();//

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.device_item, parent, false);
        }
        Device_Type device = getItem(position);
        TextView Name = convertView.findViewById(R.id.device_name);
        TextView Address = convertView.findViewById(R.id.device_address);
        Name.setText(device.getName());
        Address.setText(device.getAddress());
        return convertView;
    }

    public void addItem(String Name, String Address, BluetoothDevice device) {//매개변수 바꿔야됨
        Device_Type Device = new Device_Type();
        Device.setName(Name);
        Device.setAddress(Address);
        Device.setDevice(device);
        Devices.add(Device);
    }
}
