package com.example.vlinayo.myfirstapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button mButton;
    EditText mEdit;
    TextView tempMaxima;
    TextView tempMinima;
    TextView tempMedia;
    TextView NumMedidas;

    int tempMin = 5;
    int tempMax = 35;
    int numMed = 0;
    int tempMed;
    int grados;
    List<Integer> celsiusG = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button)findViewById(R.id.button);
        mEdit   = (EditText)findViewById(R.id.tempAdd);
        tempMaxima = (TextView)findViewById(R.id.tempMax);
        tempMinima = (TextView)findViewById(R.id.tempMin);
        tempMedia = (TextView)findViewById(R.id.tempMed);
        NumMedidas = (TextView)findViewById(R.id.contador);

        // valores max y min a la lista.
        celsiusG.add(tempMax);
        celsiusG.add(tempMin);
        tempMed = average();

        //inicializamos los valores
        tempMaxima.setText(String.valueOf(tempMax));
        tempMinima.setText(String.valueOf(tempMin));
        NumMedidas.setText(String.valueOf(numMed));
        tempMedia.setText(String.valueOf(tempMed));


        mButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        String value = mEdit.getText().toString();
                        grados = Integer.parseInt(value);
                        // si se registra un nuevo valor fuera del rango
                        if(grados < tempMin){
                            //show alert..
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("¡Aviso!")
                                    .setMessage("valor fuera de rango detectado:" + value + "C")
                                    .setMessage("¿Desea añadirlo o desea ignorarlo?")
                                    .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            tempMin = grados;
                                            celsiusG.add(tempMin);
                                            update(); //update de los datos.

                                        }
                                    })
                                    .create()
                                    .show();


                        }
                        else if(grados > tempMax){
                            // show alert..
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("¡Aviso!")
                                    .setMessage("valor fuera de rango detectado:" + value + "C")
                                    .setMessage("¿Desea añadirlo o desea ignorarlo?")
                                    .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            tempMax = grados;
                                            celsiusG.add(tempMax);
                                            update(); //update de los datos.

                                        }
                                    })
                                    .create()
                                    .show();
                        }
                        //si el valor esta dentro del rango
                        else{
                            celsiusG.add(grados);
                            update(); //update de los datos.

                        }

                    }
                });
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
                //metodoListarTemperaturas()
                Intent myIntent = new Intent(this, MyListActivity.class);
                myIntent.putExtra("data", (Serializable) celsiusG);
                this.startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //actualizamos los campos
    public void update(){
        //Aumenta el contador.
        numMed++;

        //Promedio
        tempMed = average();

        tempMaxima.setText(String.valueOf(tempMax));
        NumMedidas.setText(String.valueOf(numMed));
        tempMedia.setText(String.valueOf(tempMed));
        tempMinima.setText(String.valueOf(tempMin));


    }

    public int average()
    {
        int average = 0;
        for (int i = 0; i < celsiusG.size(); i++)  {
            average += celsiusG.get(i) ;
        }
        return average/celsiusG.size();
    }

}
