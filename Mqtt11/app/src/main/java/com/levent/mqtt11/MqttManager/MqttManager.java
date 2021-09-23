package com.levent.mqtt11.MqttManager;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.levent.mqtt11.model.PttRequest;
import com.levent.mqtt11.model.Request;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.ConnectActionListener;

import java.util.Base64;
import java.util.UUID;

public class MqttManager {

    MqttAndroidClient client;

    public String clientId = MqttClient.generateClientId();
    final String recordUuid = UUID.randomUUID().toString();
    boolean isConnectionSucceed = true;


    private RequestListener requestListener;
    private ConnectionListener connectionListener;

    private Context context;


    public interface RequestListener {


        void onRequest(byte[] data);


    }

    public interface ConnectionListener {

        void onConnectionStateChanged(boolean connectionState);

    }




    public MqttManager(Context _context, String serverURI) {

        requestListener = null;
        connectionListener = null;

        context = _context;

        client = new MqttAndroidClient(context, serverURI, clientId);



    }

    public RequestListener getRequestListener() {
        return requestListener;
    }

    public void setRequestListener(@Nullable RequestListener listener) {
        this.requestListener = listener;
    }

    public void setConnectionStateListener(@Nullable ConnectionListener listener) {
        this.connectionListener = listener;
    }


    public void connect() {

        try {
            IMqttToken token = client.connect();

            token.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Bağlandı");
                    setSubscription("pttRequest", 0);
                    Toast.makeText(context,"Connected",Toast.LENGTH_LONG).show();

                    connectionListener.onConnectionStateChanged(true);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    System.out.println("Hata");

                    connectionListener.onConnectionStateChanged(false);

                    Toast.makeText(context,"Connection Failed",Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("Hata" + e);
        }

        System.out.println("Button Parametrem " + isConnectionSucceed);

    }

    private void setSubscription(String topic, Integer qosLevel) {

        try {

            client.subscribe(topic, qosLevel);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishObjectMessage(Object message, String topic) {

        String json = null;
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        try {
            json = ow.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();

            System.out.println("Hata " + e);

        }

        byte[] convertedToBytesJsonRequest = json.getBytes();

        byte[] encodedMessage = Base64.getEncoder().encode(convertedToBytesJsonRequest);
        System.out.println("Publish Message Geldim " + message);

        try {
            client.publish(topic, encodedMessage, 0, false);

            System.out.println("Son Gönderilen" + encodedMessage);

        } catch (MqttException e) {
            e.printStackTrace();

            System.out.println("Hata " + e);

        }

    }

    public void enableListeners() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

                System.out.println("Connection Lost");

                Toast.makeText(context,"Connection Lost",Toast.LENGTH_LONG).show();

                connectionListener.onConnectionStateChanged(false);


            }

            @Override
            public void messageArrived(String topic, MqttMessage message)  {

                System.out.println("Message Arrived " + message.getPayload());

                if (topic.matches("pttRequest")) {

                    requestListener.onRequest(message.getPayload());
                }
                System.out.println("MESSAGE " + message.toString());
                System.out.println("MESSAGE " + message.getPayload().toString());
                System.out.println("GELEN ID " + message.getId());

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

                System.out.println("Mesaj İletildi");

            }
        });
    }


}
