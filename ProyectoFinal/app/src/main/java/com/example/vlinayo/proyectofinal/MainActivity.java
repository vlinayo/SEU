package com.example.vlinayo.proyectofinal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by vlinayo on 17/06/17.
 */

public class MainActivity extends AppCompatActivity {

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.init);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Inicia la vista del Mapa.
                Intent maps = new Intent(MainActivity.this, MapsActivity.class);
                MainActivity.this.startActivity(maps);
            }
        });
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.MQTTConfig:
                Intent j = new Intent(this, MQTTServerActivity.class);
//                myIntent.putExtra("markers", mList);
                this.startActivity(j);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
