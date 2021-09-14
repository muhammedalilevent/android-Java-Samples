package com.levent.mqtt11;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.levent.mqtt11.model.PttRequest;
import com.levent.mqtt11.model.Request;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


public class MainActivity extends AppCompatActivity {


    MqttAndroidClient client;


    private boolean permissionToRecordAccepted = false;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};

    //final String clientUuid = UUID.randomUUID().toString();

    String clientId = MqttClient.generateClientId();

    Recorder mRecorder;


    //Widgets
    private Button btnPTT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.1.34:1884", clientId);

        btnPTT = findViewById(R.id.btnPTT);

        mRecorder = new Recorder(getExternalCacheDir().getAbsolutePath());


    }

    public void connect(View view) {

        System.out.println("butona basıldı");

        try {
            System.out.println("try içi");
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Bağlandı");
                    Toast.makeText(MainActivity.this, "connected!!", Toast.LENGTH_LONG).show();
                    setSubscription();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    System.out.println("Hata");

                    Toast.makeText(MainActivity.this, "connection failed!!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("Hata"+ e);
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("Message Arrived "+message.getPayload());

                convertRequestToRecord(message.getPayload());


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    private void setSubscription() {

        try {

            client.subscribe("event", 0);


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void startRecord(View view) {

        mRecorder.startRecord();


    }

    public void stopRecord(View view) throws JsonProcessingException {

        publishMessage(mRecorder.stopRecord());

    }

    public void publishMessage(String dataPath) throws JsonProcessingException {

        byte[] message =   convertRecordToRequest(dataPath);

        String topic = "event";

        try {
            client.publish(topic, message,0,false);
        } catch ( MqttException e) {
            e.printStackTrace();
        }


    }

    public byte[] convertRecordToRequest(String dataPath) throws JsonProcessingException {

        byte[] encoded = new byte[0];

        final String recordUuid = UUID.randomUUID().toString();

        try {
            encoded = Files.readAllBytes(Paths.get(dataPath));
            System.out.println(Arrays.toString(encoded));
        } catch (IOException e) {

            System.out.println("Hata "+e);

        }

        Request<PttRequest> request  = new Request<PttRequest>();

        request.requestType = 101;
        request.data = new PttRequest();
        request.data.uniqueId = clientId+"_"+recordUuid;
        request.data.stream = encoded;
        request.data.length = encoded.length;
        request.data.chunkSize = encoded.length;
        request.data.extenstion = ".3gp";

        System.out.println("Uzunluk "+encoded.length);


        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(request);
        byte[] convertedToBytesJsonRequest = null;

        System.out.println(json);

            convertedToBytesJsonRequest = json.getBytes();

        byte[] encoded2 = Base64.getEncoder().encode(convertedToBytesJsonRequest);


        return encoded2;

    }

    public void convertRequestToRecord(byte[] response) throws JSONException, JsonProcessingException {

        byte[] decodedResponse = Base64.getDecoder().decode(response);

        JSONObject sample = new JSONObject(new String(decodedResponse));

        System.out.println(sample);

        //Gson gson = new Gson();





        ObjectMapper mapper = new ObjectMapper();
        Request<PttRequest> map = null;
        try {
            map = mapper.readValue(decodedResponse, new TypeReference<Request<PttRequest>>() {

            });
            System.out.println("BAŞARILI "+map.data.length);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("DENEME CATCH "+map.data);
        }


    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

}