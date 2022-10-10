package com.example.smart_fan;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Switch bluetooth_switch;
    private Switch auto_switch;
    private SeekBar angle_seekbar;
    private TextView angle_text;
    private View auto_view;
    private View angle_view;
    private int REQUEST_BLUETOOTH_ENABLE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//Title bar 없애기
        angle_seekbar = findViewById(R.id.angle_bar);
        angle_text = findViewById(R.id.angle);
        bluetooth_switch = findViewById(R.id.bluetooth_btn);
        auto_switch = findViewById(R.id.auto_btn);
        auto_view = findViewById(R.id.Auto_view);
        angle_view = findViewById(R.id.Angle_view);
        auto_view.setVisibility(View.INVISIBLE);//안보이게
        angle_view.setVisibility(View.INVISIBLE);//안보이게
        bluetooth_switch.setChecked(false);//초기값은 off
        auto_switch.setChecked(false);//초기값은 off
        ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            auto_view.setVisibility(View.VISIBLE);
                        }
                        else{
                            bluetooth_switch.setChecked(false);
                        }
                    }
                });
        bluetooth_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if(bluetoothAdapter == null){
                        bluetooth_switch.setChecked(false);
                    }
                    else{
                        if (!bluetoothAdapter.isEnabled()) {//만약 권한을 얻기전이라면
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityResult.launch(enableBtIntent);
                        }
                        else{//권한을 얻은후
                            auto_view.setVisibility(View.VISIBLE);
                        }
                    }//사용 여부 확인 및 권한 얻기
                }//블루투스를 켰을때
                else{
                    auto_switch.setChecked(false);
                    auto_view.setVisibility(View.INVISIBLE);
                    angle_view.setVisibility(View.INVISIBLE);
                }//블루투스 연결을 끊었을때 자동을 수동으로 바꿔주고 바꾼다.
            }
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
}