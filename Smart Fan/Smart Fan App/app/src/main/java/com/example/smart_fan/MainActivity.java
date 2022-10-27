package com.example.smart_fan;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Listener {
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private Switch bluetooth_switch;
    private Switch auto_switch;
    private SeekBar angle_seekbar;
    private TextView angle_text;
    private TextView connect_device;
    private View auto_view;
    private View angle_view;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//Title bar 없애기
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        angle_seekbar = findViewById(R.id.angle_bar);
        connect_device = findViewById(R.id.connect_device);
        angle_text = findViewById(R.id.angle);
        bluetooth_switch = findViewById(R.id.bluetooth_btn);
        auto_switch = findViewById(R.id.auto_btn);
        auto_view = findViewById(R.id.Auto_view);
        angle_view = findViewById(R.id.Angle_view);
        auto_view.setVisibility(View.INVISIBLE);//안보이게
        angle_view.setVisibility(View.INVISIBLE);//안보이게
        bluetooth_switch.setChecked(false);//초기값은 off
        auto_switch.setChecked(false);//초기값은 off
        Device_List Dialog = new Device_List(this,this);
        ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        auto_view.setVisibility(View.VISIBLE);
                        Dialog.show();
                    }
                    else{
                        bluetooth_switch.setChecked(false);
                    }
                });
        bluetooth_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                if(bluetoothAdapter == null){//블루투스 지원을 안하는 기기
                    bluetooth_switch.setChecked(false);
                    Toast.makeText(this,"블루투스를 지원하지 않는 기기입니다.",Toast.LENGTH_SHORT).show();
                }
                else{
                    if (!bluetoothAdapter.isEnabled()) {//만약 권한을 얻기전이라면
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityResult.launch(enableBtIntent);
                    }
                    else {//권한을 얻었다면
                        auto_view.setVisibility(View.VISIBLE);
                        Dialog.show();
                    }
                }//사용 여부 확인 및 권한 얻기
            }//블루투스를 켰을때
            else{
                auto_switch.setChecked(false);
                auto_view.setVisibility(View.INVISIBLE);
                angle_view.setVisibility(View.INVISIBLE);
            }//블루투스 연결을 끊었을때 자동을 수동으로 바꿔주고 바꾼다.
        });
        auto_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    angle_seekbar.setProgress(90);
                    angle_view.setVisibility(View.VISIBLE);
                }//자동일때
                else{
                    angle_view.setVisibility(View.INVISIBLE);
                }//수동일때
            }
        });
        angle_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                angle_text.setText(progress+"°");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });//사용자가 원하는 각도에 따라서 값을 출력해준다.
    }
    public void changeText(String str) {
        connect_device.setText(str);
    }
}