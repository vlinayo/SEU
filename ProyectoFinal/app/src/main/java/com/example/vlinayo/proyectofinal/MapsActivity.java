package com.example.vlinayo.proyectofinal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.location.Location;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MapsActivity extends  AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    SharedPreferences sharedPref;

    GoogleMap mMap;
    static GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private SupportMapFragment mapFragment;
    Context context = this;
//    private GeofencingClient mGeofencingClient;

    //para crear una geofence
    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;

    private LocationRequest locationRequest;
    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL = 1000;
    private final int FASTEST_INTERVAL = 900;
    private Marker geoFenceMarker;
    static ArrayList<Marker> geoFenceMarkerList;
    static ArrayList<Circle> geoFenceCircleList;
    static ArrayList<Geofence> geoFenceList;
    UiSettings uiSets;
    EditText markerName;
    EditText markerRadio;
    Marker markerLocation;
    String mName;
    float mRadio;
    private Circle geoFenceLimits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Inicializamos el Mapa
        initGMaps();

        //creamos el Cliente Google.
        createGoogleClient();

        //inicializamos

        geoFenceCircleList = new ArrayList<>();
        geoFenceMarkerList = new ArrayList<>();


    }

    // Initialize GoogleMaps
    private void initGMaps() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Callback called when Map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("onMapReady()");
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                System.out.println("onMapClick(" + latLng + ")");
                addGeofence(latLng);
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                System.out.println("onMarkerClickListener: " + marker.getPosition());
                return false;
            }
        });

    }

    //interfaz de dialogo para agregar geofences
    public void addGeofence(final LatLng latLng){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.new_marker_dialog, null);
        markerName =(EditText)view.findViewById(R.id.markerUsername);
        markerRadio =(EditText)view.findViewById(R.id.markerRadio);

        builder.setView(view);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                if (id == DialogInterface.BUTTON_NEGATIVE) {
                    dialog.cancel();
                    return;
                }
                mName = markerName.getText().toString();
                mRadio = Float.parseFloat(markerRadio.getText().toString());
                markerForGeofence(latLng);

//                Toast.makeText(getApplicationContext(),marker.getTitle().toString(), Toast.LENGTH_SHORT).show();
            }

        };
        builder.setTitle("Add a new Location")
                .setMessage("Do you want to add the pressed location?")
                .setNegativeButton(android.R.string.cancel, listener) // dismisses by default
                .setPositiveButton(android.R.string.ok, listener)
                .create()
                .show();

    }

    // Create GoogleApiClient instance
    private void createGoogleClient() {
//        Log.d(TAG, "createGoogleApi()");
        if ( mGoogleApiClient == null ) {
            mGoogleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }

  //Funciones del API google

    @Override
    protected void onStart() {
        super.onStart();
        // Call GoogleApiClient connection when starting the Activity
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Disconnect GoogleApiClient when stopping Activity
        mGoogleApiClient.disconnect();
    }

    // GoogleApiClient.ConnectionCallbacks connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("onConnected()");
        getLastKnownLocation();
    }

    // GoogleApiClient.ConnectionCallbacks suspended
    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("onConnectionSuspended()");
    }

    // GoogleApiClient.OnConnectionFailedListener fail
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("onConnectionFailed()");
    }


    // Get last known location
    private void getLastKnownLocation() {
        System.out.println("getLastKnownLocation()");
        if ( checkPermission() ) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if ( mLastLocation != null ) {
                System.out.println("LasKnown location. " +
                        "Long: " + mLastLocation.getLongitude() +
                        " | Lat: " + mLastLocation.getLatitude());
                uiSets = mMap.getUiSettings();
                mMap.setMyLocationEnabled(true);
                uiSets.setRotateGesturesEnabled(true);
                uiSets.setAllGesturesEnabled(true);
                uiSets.setMapToolbarEnabled(true);
                uiSets.setZoomControlsEnabled(true);
                uiSets.setMyLocationButtonEnabled(true);
                uiSets.setCompassEnabled(true);
                writeLastLocation();
                startLocationUpdates();
            } else {
                System.out.println("No location retrieved yet");
                startLocationUpdates();
            }
        }
        else askPermission();
    }

    // Start location Updates
    private void startLocationUpdates(){
        System.out.println("startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("onLocationChanged ["+location+"]");
        mLastLocation = location;
        writeActualLocation(location);
    }

    private void writeActualLocation(Location location) {
        locationMarker(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void writeLastLocation() {
        writeActualLocation(mLastLocation);
    }


    // Check for permission to access Location
    private boolean checkPermission() {
        System.out.println("checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Asks for permission
    private void askPermission() {
        System.out.println( "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                MY_PERMISSIONS_REQUEST_LOCATION
        );
    }

    // Verify user's response of the permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        System.out.println( "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // Permission granted
                    getLastKnownLocation();

                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }

    // App cannot work without the permissions
    private void permissionsDenied() {
        System.out.println("permissionsDenied()");
    }

    private void locationMarker(LatLng latLng) {
        System.out.println("markerLocation("+latLng+")");
        String title = "My Current Location";
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        if ( mMap!=null ) {
            // Remove the anterior marker
            if ( markerLocation != null )
                markerLocation.remove();
            markerLocation = mMap.addMarker(markerOptions);
            float zoom = 14f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            mMap.animateCamera(cameraUpdate);
        }


    }

    // Create a marker for the geofence creation
    private void markerForGeofence(LatLng latLng) {
        System.out.println("markerForGeofence(" + latLng + ")");
        String title = mName;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title);
        if (mMap != null) {
            geoFenceMarker = mMap.addMarker(markerOptions);
            geoFenceMarkerList.add(geoFenceMarker);

            startGeofence();

        }
    }

    // Create a Geofence
    private Geofence createGeofence( LatLng latLng, float radius ) {
        System.out.println("createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration( GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }


    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest( Geofence geofence ) {
        System.out.println("createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }


    private PendingIntent createGeofencePendingIntent() {
        System.out.println("createGeofencePendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this, GeofenceIntentService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }



    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofences(GeofencingRequest request) {
        System.out.println("addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    System.out.println("onResult: " + status);
                    if ( status.isSuccess() ) {
                        drawGeofence();
                    }
                }

            });
    }

    // Draw Geofence circle on GoogleMap
    private void drawGeofence() {
        System.out.println("drawGeofence()");

        if ( geoFenceLimits != null ){
            geoFenceLimits.remove();

        }

        if(!geoFenceCircleList.isEmpty()){
            for (Circle circles : geoFenceCircleList){
                CircleOptions circleOptions = new CircleOptions()
                        .center( new LatLng(circles.getCenter().latitude, circles.getCenter().longitude))
                        .strokeColor(Color.argb(50, 70,70,70))
                        .fillColor( Color.argb(100, 150,150,150) )
                        .radius( circles.getRadius());
                geoFenceLimits = mMap.addCircle( circleOptions );

            }


        }

        CircleOptions circleOptions = new CircleOptions()
                    .center(geoFenceMarker.getPosition())
                    .strokeColor(Color.argb(50, 70,70,70))
                    .fillColor( Color.argb(100, 150,150,150) )
                    .radius(mRadio);
            geoFenceLimits = mMap.addCircle( circleOptions );
            geoFenceCircleList.add(geoFenceLimits);
    }

    // Start Geofence creation process
    private void startGeofence() {
        System.out.println("startGeofence()");
        if( geoFenceMarker != null ) {
            Geofence geofence = createGeofence( geoFenceMarker.getPosition(), mRadio );
            GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
//            geoFenceList.add(geofence);
//            removeGeofences();
            addGeofences( geofenceRequest);
        } else {
            System.out.println("Geofence marker is null");
        }
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, LocationsListActivity.class);
//                myIntent.putExtra("markers", mList);
                this.startActivity(i);
                return true;

            case R.id.MQTTConfig:
                Intent j = new Intent(this, MQTTServerActivity.class);
//                myIntent.putExtra("markers", mList);
                this.startActivity(j);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        RefreshMap();

    }


    //actualizamos vista del Mapa.
    protected void RefreshMap() {

        System.out.println("refreshMap()");

        mMap.clear();

        if(geoFenceMarkerList!= null){
            MarkerOptions marker = new MarkerOptions();
            CircleOptions circle = new CircleOptions();

            for (Marker mylist : geoFenceMarkerList) {
                mMap.addMarker(
                        marker.position(new LatLng(mylist.getPosition().latitude, mylist.getPosition().longitude)).
                                title(mylist.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                );

            }
//            startGeofence();


            for( Circle myCircle : geoFenceCircleList){
                mMap.addCircle(
                        circle.center(new LatLng(myCircle.getCenter().latitude, myCircle.getCenter().longitude))
                                .radius(myCircle.getRadius())
                                .strokeColor(Color.argb(50, 70,70,70))
                                .fillColor( Color.argb(100, 150,150,150) )
                                .strokeWidth(2)
                                .clickable(true)

                );

//                double d = myCircle.getRadius();
//                float radio = (float)d;
//                createGeofence(new LatLng(myCircle.getCenter().latitude, myCircle.getCenter().longitude),
//                       radio);
//
            }

        }

    }

    public void onPause() {
        super.onPause();
        //Inicializamos el Mapa
        initGMaps();
        //creamos el Cliente Google.
        createGoogleClient();

    }


    public void onResume() {
        super.onResume();
        //Inicializamos el Mapa
        initGMaps();
        //creamos el Cliente Google.
        createGoogleClient();

    }

    static Intent makeNotificationIntent(Context geofenceService, String msg)
    {
        Log.d("TAG",msg);
        return new Intent(geofenceService,MapsActivity.class);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
