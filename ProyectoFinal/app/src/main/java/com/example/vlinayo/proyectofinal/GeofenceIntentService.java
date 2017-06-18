package com.example.vlinayo.proyectofinal;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vlinayo on 21/05/17.
 */

public class GeofenceIntentService extends IntentService {

    private static final String TAG = GeofenceIntentService.class.getSimpleName();
    public static final int GEOFENCE_NOTIFICATION_ID = 0;
    static String content;
    SharedPreferences preferences;
    Context context = this;


    public GeofenceIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve the Geofencing intent
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Handling errors
        if ( geofencingEvent.hasError() ) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;
        }

        // Retrieve GeofenceTrasition
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            // Get the geofence that were triggered
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // Create a detail message with Geofences received
            String geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences );
            // Send notification details as a String
            sendNotification( geofenceTransitionDetails );
        }
    }

    // Create a detail message with Geofences received
    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
        }

        String status = null;
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {
            status = "Entering ";
            content = "Inside";
        }
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            status = "Exiting ";
            content = "Outside";
        }
        return status + TextUtils.join( ", ", triggeringGeofencesList);
    }

    // Send a notification
    private void sendNotification( String msg ) {
        Log.i(TAG, "sendNotification: " + msg );

        // Intent to start the main Activity
        Intent notificationIntent = MapsActivity.makeNotificationIntent(
                getApplicationContext(), msg
        );

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MapsActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Creating and sending Notification
//        NotificationManager notificatioMng =
//                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
//        notificatioMng.notify(
//                GEOFENCE_NOTIFICATION_ID,
//                createNotification(msg, notificationPendingIntent));

        mqttMainConnection();

    }

//    // Create a notification
//    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
//        notificationBuilder
//                .setSmallIcon(R.mipmap.alert)
//                .setColor(Color.RED)
//                .setContentTitle(msg)
//                .setContentText("Geofence Notification!")
//                .setContentIntent(notificationPendingIntent)
//                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
//                .setAutoCancel(true);
//        return notificationBuilder.build();
//    }

    // Handle errors
    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }

    public void mqttMainConnection() {

        String server = MQTTServerActivity.serverName;
        String port = MQTTServerActivity.portName;
        String userName = MQTTServerActivity.userName;
        String userPass = MQTTServerActivity.userPass;
        String topic = MQTTServerActivity.topicName;

        char[] password;
        String broker;

        if((server == null) || ( port == null)){
            broker       = "tcp://m11.cloudmqtt.com:16726";
        }else {

            server = "tcp://".concat(server.concat(":"));
            broker = server.concat(port);
        }
        if((userName == null) || ( userPass == null)){
            userName = "vlinayo";
            password = new char[]{'1', '2', '3', '4'};
        }else{
            password = userPass.toCharArray();
        }
        if(topic == null){
            topic = "SEU";
        }

        int qos  = 1;

        //MQTT client id to use for the device. "" will generate a client id automatically
        String clientId     = "ClientId";

        MemoryPersistence persistence = new MemoryPersistence();
        try {
            MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
            mqttClient.setCallback(new MqttCallback() {
                public void messageArrived(String topic, MqttMessage msg)
                        throws Exception {
                    System.out.println("Recived:" + topic);
                    System.out.println("Message:" + msg);
                    System.out.println("Recived:" + new String(msg.getPayload()));
                }

                public void deliveryComplete(IMqttDeliveryToken arg0) {
                    System.out.println("Delivary complete");
                }

                public void connectionLost(Throwable arg0) {
                    // TODO Auto-generated method stub

                }
            });

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(userName);
            connOpts.setPassword(password);
            mqttClient.connect(connOpts);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            System.out.println("Publish message: " + message);
            mqttClient.subscribe(topic, qos);
            mqttClient.publish(topic, message);
            mqttClient.disconnect();
//            System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }

}
