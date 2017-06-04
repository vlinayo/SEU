package com.example.vlinayo.proyectofinal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlinayo on 2/06/17.
 */

public class LocationsListActivity  extends Activity implements Serializable {

        ListView listView;
        static ArrayList<Marker> list;
    @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_list);

            //obtenemos la lista de la vista
            listView = (ListView) findViewById(R.id.list);

            //Deifinimos los valores de la lista
            list = MapsActivity.mList;

            final ArrayList<String> newList = new ArrayList<String>(list.size());
            for (Marker mylist : list) {
                newList.add(mylist.getTitle());
            }
            System.out.println(newList);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, newList);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    // ListView Clicked item index
                    int itemPosition     = position;
                    // ListView Clicked item value
                    final String  itemValue    = (String) listView.getItemAtPosition(position);

                    new AlertDialog.Builder(LocationsListActivity.this)
                            .setTitle("¡Alert!")
                            .setMessage("Do you want to delete this place?" + itemValue)
                            .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    list.remove(position);
                                    newList.remove(position);
                                    MapsActivity.mList = list;
                                    adapter.remove(itemValue);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .create()
                            .show();
                            listView.invalidateViews();
                    // Show Alert
//                    Toast.makeText(getApplicationContext(),
//                            "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
//                            .show();

                }

            });

        }

    @Override
    public void onBackPressed() {

        setResult(RESULT_OK);
        finish();
        super.onBackPressed();
    }
}
