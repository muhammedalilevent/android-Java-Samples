package com.levent.mqtt11;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levent.mqtt11.MqttManager.MqttManager;
import com.levent.mqtt11.model.PttRequest;
import com.levent.mqtt11.model.Request;

import org.json.JSONException;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class AudioHandler {

    private AudioRecord audioRecord;
    private AudioTrack audioTrack;

    private int intBufferSize;
    private int intWaitSize = 5;

    private Thread thread;

    private boolean isRecordActive = false;
    private boolean isPlaybackActive = false;

    private MqttManager mqttManager;


    private Queue<byte[]> Streams;
    private String recordUuid = "";

    WorkingModeChanged workingModeChangedListener;


    public interface WorkingModeChanged {

        void onWorkingModeChanged(boolean isBusy);

    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    public AudioHandler(MqttManager _mqttManager) {

        mqttManager = _mqttManager;

        workingModeChangedListener = null;

        mqttManager.setRequestListener(data -> {

            try {
                Request<PttRequest> request = null;
                    request = GetObject(data);


                if(!request.data.clientId.matches(mqttManager.clientId)){
                    //return;
                    AddStream(request.data.stream);
                }
                if (request.data.isFinished){
                    workingModeChangedListener.onWorkingModeChanged(false);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });


        Streams = new LinkedList<>();

        int intRecordSampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);

        intBufferSize = AudioRecord.getMinBufferSize(intRecordSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                intRecordSampleRate,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                intBufferSize,
                AudioTrack.MODE_STREAM);
        audioTrack.setPlaybackRate(intRecordSampleRate);
    }

    public void setWorkingModeChangedListener(@Nullable WorkingModeChanged listener) {
        this.workingModeChangedListener = listener;
    }


@RequiresApi(api = Build.VERSION_CODES.O)
private Request<PttRequest> GetObject(byte[] data) throws JSONException {
    byte[] decodedResponse = Base64.getDecoder().decode(data);

    ObjectMapper mapper = new ObjectMapper();
    Request<PttRequest> map = null;
    try {
        map = mapper.readValue(decodedResponse, new TypeReference<Request<PttRequest>>() {
        });

        System.out.println("BAŞARILI "+map);
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("DENEME CATCH "+map.data);
    }
    return map;
}
    public void AddStream(byte[] data) {

        Streams.add(data);
        if (Streams.size() >= intWaitSize && !isPlaybackActive) {
            isPlaybackActive = true;

            workingModeChangedListener.onWorkingModeChanged(isPlaybackActive);


            audioTrack.play();
            thread = new Thread(() -> threadSoundPlayer());
            thread.run();
        }

    }

    private void threadSoundPlayer() {
        while (isPlaybackActive && Streams.size() > 0) {
            byte[] data = Streams.remove();
            audioTrack.write(data, 0, data.length);
        }
        isPlaybackActive = false;
    }

    @SuppressLint("MissingPermission")
    public void threadVoiceRecorder() {

        Runnable runnable = () -> {
            isRecordActive = true;
            recordUuid = UUID.randomUUID().toString();
            System.out.println("threadVoice funct girdi");

            int intRecordSampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);

            intBufferSize = AudioRecord.getMinBufferSize(intRecordSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC
                    , intRecordSampleRate,
                    AudioFormat.CHANNEL_IN_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    intBufferSize);

            audioRecord.startRecording();

            byte[] data = new byte[intBufferSize];

            while (isRecordActive) {

                audioRecord.read(data, 0, data.length);

                Request<PttRequest> request = new Request();

                request.requestType = 101;
                request.data = new PttRequest();
                request.data.uniqueId = mqttManager.clientId + "_" + recordUuid;
                request.data.stream = data;
                request.data.isFinished = false;
                request.data.chunkSize = intBufferSize;
                request.data.extenstion = ".3gpp";
                request.data.clientId = mqttManager.clientId;


                mqttManager.publishObjectMessage(request, "pttRequest");

            }
        };
        new Thread(runnable).start();
    }

    public void stopRecord() {

        isRecordActive = false;
        audioRecord.stop();

        Request<PttRequest> request = new Request();

        request.requestType = 101;
        request.data = new PttRequest();
        request.data.uniqueId = mqttManager.clientId + "_" + recordUuid;
        request.data.stream = new byte[0];
        request.data.isFinished = true;
        request.data.chunkSize = intBufferSize;
        request.data.extenstion = ".3gpp";
        request.data.clientId = mqttManager.clientId;

        mqttManager.publishObjectMessage(request, "pttRequest");

    }


}
