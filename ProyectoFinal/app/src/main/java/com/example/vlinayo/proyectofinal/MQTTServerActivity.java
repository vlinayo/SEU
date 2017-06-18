package com.example.vlinayo.proyectofinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by vlinayo on 15/06/17.
 */

public class MQTTServerActivity  extends AppCompatActivity {

    EditText server, topic, port, username, password;
    static String serverName,topicName,portName, userName, userPass;
    Button init;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mqtt_layout);

        server = (EditText)findViewById(R.id.server);
        topic = (EditText)findViewById(R.id.topic);
        port = (EditText)findViewById(R.id.port);
        init = (Button)findViewById(R.id.save);
        username = (EditText) findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        //si ya existe una configuración la pintamos...
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        serverName = sharedPref.getString("Server",null);
        portName = sharedPref.getString("Port",null);
        topicName = sharedPref.getString("Topic",null);
        userName = sharedPref.getString("User",null);
        userPass = sharedPref.getString("Pass",null);

        if(serverName != null){
            server.setText(serverName);
        }
        if(portName != null){
            port.setText(portName);
        }
        if(topicName != null){
            topic.setText(topicName);
        }
        if(userName != null){
            username.setText(userName);
        }
        if(userPass != null){
            password.setText(userPass);
        }


        //al hacer click en Guardar:
        //guardamos las variables de configuración del MQTT.

//        init.isClickable();
        init.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //salvamos los datos ingresados.
                //los valores que ingresa el usuario
                serverName = server.getText().toString();
                topicName = topic.getText().toString();
                portName = port.getText().toString();
                userName = username.getText().toString();
                userPass = password.getText().toString();

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Server", serverName);
                editor.putString("Port", portName);
                editor.putString("Topic", topicName);
                editor.putString("User", userName);
                editor.putString("Pass", userPass);
                editor.commit();
                finish();

            }
        });


    }
}
