package com.example.vlinayo.sensorpractice;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private TextView ejeX;
    private TextView ejeY;
    private TextView ejeZ;
    private float actX, actY, actZ;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float vibrateThreshold = 0;

    private TextView ejeXMax;
    private TextView ejeYMax;
    private TextView ejeZMax;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    public Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ejeX = (TextView) findViewById(R.id.ejeX);
        ejeY = (TextView) findViewById(R.id.ejeY);
        ejeZ = (TextView) findViewById(R.id.ejeZ);

        ejeXMax = (TextView) findViewById(R.id.ejeXMax);
        ejeYMax = (TextView) findViewById(R.id.ejeYMax);
        ejeZMax = (TextView) findViewById(R.id.ejeZMax);

        ejeX.setText("0.0");
        ejeY.setText("0.0");
        ejeZ.setText("0.0");
        ejeXMax.setText("0.0");
        ejeYMax.setText("0.0");
        ejeZMax.setText("0.0");


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! no tenemos acelerometro..
        }
        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }


    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();
        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(actX - event.values[0]);
        deltaY = Math.abs(actY - event.values[1]);
        deltaZ = Math.abs(actZ - event.values[2]);
        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
        deltaX = 0;
        if (deltaY < 2)
        deltaY = 0;
        if (deltaZ > vibrateThreshold || deltaY > vibrateThreshold || deltaZ > vibrateThreshold) {
            v.vibrate(50);
        }
    }

    public void displayCleanValues() {
        ejeX.setText("0.0");
        ejeY.setText("0.0");
        ejeZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        ejeX.setText(Float.toString(deltaX));
        ejeY.setText(Float.toString(deltaY));
        ejeZ.setText(Float.toString(deltaZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            ejeXMax.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            ejeYMax.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            ejeZMax.setText(Float.toString(deltaZMax));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent myIntent = new Intent(this, BallActivity.class);
                this.startActivity(myIntent);
                return true;
            case R.id.action_game_settings:
                Intent my2Intent = new Intent(this, GolfActivity.class);
                this.startActivity(my2Intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
