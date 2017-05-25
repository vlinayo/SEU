package com.example.vlinayo.sensorpractice;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by vlinayo on 12/04/17.
 */


public class BallActivity extends Activity implements SensorEventListener {

    private ImageView ball;
    private SensorManager sensore;
    private Sensor accelerometer;
    public Vibrator v;
    private float vibrateThreshold = 0;
    private float actX, actY, actZ;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    final String tag = "AccLogger";
    private RelativeLayout layout;


    int xa=0;
    int ya=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ball_main);

        sensore = (SensorManager) getSystemService(SENSOR_SERVICE);
        layout = (RelativeLayout) findViewById(R.id.ball);
        layout.scrollTo(xa,ya);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        Sensor Accel = sensore.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // register this class as a listener for the orientation and accelerometer sensors
        sensore.registerListener((SensorEventListener) this, Accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensore.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;
        float [] values = event.values;
        synchronized (this) {
            Log.d(tag, "onSensorChanged: " + sensor + ", x: " +
                    values[0] + ", y: " + values[1] + ", z: " + values[2]);
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
                xa=(int)values[0];// this part of code is only test to see int x and y on Activity
                ya=(int)values[1];
                displayBall();
            }
        }

        // display the current x,y,z accelerometer values
        // get the change of the x,y,z values of the accelerometer
//        deltaX = Math.abs(actX - event.values[0]);
//        deltaY = Math.abs(actY - event.values[1]);
//        deltaZ = Math.abs(actZ - event.values[2]);
//        // if the change is below 2, it is just plain noise
//        if (deltaX < 2)
//            deltaX = 0;
//        if (deltaY < 2)
//            deltaY = 0;
//        if (deltaZ > vibrateThreshold || deltaY > vibrateThreshold || deltaZ > vibrateThreshold) {
//            v.vibrate(50);

    }

    public void displayBall() {
        layout.scrollBy(xa, ya);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}