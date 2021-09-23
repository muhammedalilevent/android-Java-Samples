package com.levent.audiorecordersample10;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;


import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


public class MainActivity extends Activity {

    private boolean permissionToRecordAccepted = false;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private TextView txtStatus;


    private AudioRecord audioRecord;
    private AudioTrack audioTrack;

    private int intBufferSize;
    private short[] shortAudioData;

    private int intGain;
    private boolean isActive = false;

    private Thread thread;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        txtStatus = findViewById(R.id.txtStatus);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                threadLoop();
            }
        });


    }

    public void btnStart(View view) {

        isActive = true;
        txtStatus.setText("Active");
        intGain = 1;


        thread.start();

    }

    public void btnStop(View view) {

        isActive = false;
        txtStatus.setText("Stopped");
        audioRecord.stop();
        audioTrack.stop();


    }

    @SuppressLint("MissingPermission")
    private void threadLoop() {

        int intRecordSampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);

        intBufferSize = AudioRecord.getMinBufferSize(intRecordSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        shortAudioData = new short[intBufferSize];


        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC
                , intRecordSampleRate,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                intBufferSize);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                intRecordSampleRate,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                intBufferSize,
                AudioTrack.MODE_STREAM);

        audioTrack.setPlaybackRate(intRecordSampleRate);

        audioRecord.startRecording();
        audioTrack.play();

        while (isActive) {
            audioRecord.read(shortAudioData, 0, shortAudioData.length);

            /*for (int i = 0; i < shortAudioData.length; i++) {

                shortAudioData[i] = (short) Math.min(shortAudioData[i] * intGain, Short.MAX_VALUE);

            }
*/
            audioTrack.write(shortAudioData, 0, shortAudioData.length);
        }


    }

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