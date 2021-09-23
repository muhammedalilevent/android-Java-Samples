package com.levent.mqtt11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.levent.mqtt11.MqttManager.MqttManager;


public class MainActivity extends AppCompatActivity {


    private boolean permissionToRecordAccepted = false;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};


    AudioHandler mAudioHandler;

    String serverURI = "tcp://192.168.1.42:1884";

    MqttManager mqttManager;

    boolean micState = false;

    boolean connectionState = false;
boolean btnRecorderState = false;

    //Widgets
    private Button btnPTT;
    private Button btnEndRecord;
    private TextView txtConnState;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        mqttManager = new MqttManager(MainActivity.this, serverURI);

        mqttManager.setConnectionStateListener(_connectionState -> {
            System.out.println("connectionState STATE "+_connectionState );
            if (_connectionState) {

                txtConnState.setText("Connected");

            } else {

                txtConnState.setText("Not Connected");

            }


            btnPTT.setVisibility(_connectionState ? View.VISIBLE : View.INVISIBLE);
           // btnEndRecord.setEnabled(_connectionState);

            connectionState = _connectionState;

        });

        btnPTT = findViewById(R.id.btnPTT);

        btnPTT.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        //PRESSED
                        if (micState == false) {

                            mAudioHandler.threadVoiceRecorder();
                            micState = true;
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (micState == true) {

                            mAudioHandler.stopRecord();

                            micState = false;
                        }
                        //RELEASED
                        return true;
                }

                return false;
            }
        });


        btnEndRecord = findViewById(R.id.btnEndRecord);
        txtConnState = findViewById(R.id.txtConnState);

        btnPTT.setVisibility(View.INVISIBLE);

        btnEndRecord.setVisibility(View.INVISIBLE);

        mAudioHandler = new AudioHandler(mqttManager);

        /*mAudioHandler.setWorkingModeChangedListener(isBusy -> {
            btnRecorderState = isBusy;
            runOnUiThread(()->
                    {
                        if(btnRecorderState && btnPTT.getVisibility() == View.VISIBLE) {
                            //btnPTT.setEnabled(false);
                            btnPTT.setVisibility(View.INVISIBLE);
                            System.out.println("Butonum Kapanmalı Değer False Olmalı "+btnPTT.getVisibility() );
                        }
                        if(!btnRecorderState && btnPTT.getVisibility() == View.INVISIBLE)
                        {
                            //btnPTT.setEnabled(true);
                            btnPTT.setVisibility(View.VISIBLE);
                            System.out.println("Butonum Kapanmalı Değer True Olmalı "+btnPTT.getVisibility() );
                        }
                    });
//            if(isBusy && btnPTT.isEnabled()) {
//                btnPTT.setEnabled(false);
//                System.out.println("Butonum Kapanmalı Değer False Olmalı "+btnPTT.isEnabled() );
//            }
//            if(!isBusy && !btnPTT.isEnabled())
//            {
//                btnPTT.setEnabled(true);
//                System.out.println("Butonum Kapanmalı Değer True Olmalı "+btnPTT.isEnabled() );
//            }

        }  );*/


//        mAudioHandler.setWorkingModeChangedListener(isBusy -> {
//            if(isBusy && btnPTT.isEnabled()) {
//                btnRecorderState =false;
//                btnPTT.setEnabled(false);
//                System.out.println("Butonum Kapanmalı Değer False Olmalı "+btnPTT.isEnabled() );
//            }
//            if(!isBusy && !btnPTT.isEnabled())
//            {
//                btnRecorderState =true;
//                btnPTT.setEnabled(true);
//                System.out.println("Butonum Kapanmalı Değer True Olmalı "+btnPTT.isEnabled() );
//            }
//
//        }  );

        /*mAudioHandler.setWorkingModeChangedListener(isBusy -> {

            btnPTT.setEnabled(!isBusy);

        }  );*/

    }





    public void connect(View view) {

        //init
        if (!connectionState) {

            mqttManager.connect();
            mqttManager.enableListeners();
            connectionState = true;

        }


    }

    /*public void startRecord(View view) {

        if (micState == false) {

            mAudioHandler.threadVoiceRecorder();
            micState = true;
        }


    }*/

    /*public void stopRecord(View view) {

        if (micState == true) {

            mAudioHandler.stopRecord();

            micState = false;
        }


    }*/

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

}