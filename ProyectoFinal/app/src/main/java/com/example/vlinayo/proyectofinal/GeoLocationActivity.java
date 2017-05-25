package com.example.vlinayo.proyectofinal;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vlinayo on 21/05/17.
 */

public class GeoLocationActivity extends Activity {

//    private LocationClient mLocationClient;

    static class StoreLocation {
        public LatLng mLatLng;
        public String mId;
        StoreLocation(LatLng latlng, String id) {
            mLatLng = latlng;
            mId = id;
        }
    }


}
