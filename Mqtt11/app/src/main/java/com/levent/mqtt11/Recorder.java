package com.levent.mqtt11;

import android.Manifest;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.IOException;

public class Recorder {

    private static String fileName = null;

    private MediaRecorder recorder = null;

    private MediaPlayer player = null;

    public Recorder(String cachePath){

        fileName = cachePath;
        fileName += "/audiorecordtest"+System.currentTimeMillis()+".3gp";



    }

    public void startRecord(){

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            System.out.println(e);
        }

        recorder.start();

    }

    public String stopRecord(){

        recorder.stop();
        recorder.release();
        recorder = null;
        System.out.println(fileName);

        return fileName;

    }



}
