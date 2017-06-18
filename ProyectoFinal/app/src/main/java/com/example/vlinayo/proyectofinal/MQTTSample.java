package com.example.vlinayo.proyectofinal;

/**
 * Created by vlinayo on 11/06/17.
 */

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTSample {

    public static void main(String[] args) {
        String topic        = "SEU";
        String content      = "Hello CloudMQTT";
        int qos             = 1;
//        String broker       = "tcp://cloudmqtt_server:cloudmqtt_port";
        String broker       = "tcp://m11.cloudmqtt.com:16726";

        //MQTT client id to use for the device. "" will generate a client id automatically
        String clientId     = "ClientId";

        MemoryPersistence persistence = new MemoryPersistence();
        try {
            MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
            mqttClient.setCallback(new MqttCallback() {
                public void messageArrived(String topic, MqttMessage msg)
                        throws Exception {
                    System.out.println("Recived:" + topic);
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
            connOpts.setUserName("vlinayo");
            connOpts.setPassword(new char[]{'2', '1', '2', '6', '1', '4', '0','8'});
            mqttClient.connect(connOpts);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            System.out.println("Publish message: " + message);
            mqttClient.subscribe(topic, qos);
            mqttClient.publish(topic, message);
            mqttClient.disconnect();
            System.exit(0);
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