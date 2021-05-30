package com.example.upora.open_box;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.Random;

public class LocationActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_ALL = 123;

    TextView tv_lon, tv_lat;
    MapView map = null;
    GeoPoint startPoint;
    IMapController mapController;
    Marker marker;
    Random rnd;
    Polyline path;

    LocationRequest locationRequest;

    FusedLocationProviderClient fusedLocationProviderClient;

    String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.CAMERA
    };
    Location lok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_location);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * 30);
        locationRequest.setFastestInterval(100 * 5);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public void initMapStartGPS() {
        /*
        mapController = map.getController();
        mapController.setZoom(18.5);
        startPoint = new GeoPoint(tmpUser.getLocationLatitude(),tmpUser.getLocationLongitude());
        //Log.i("Latitude", String.valueOf(tmpUser.getLocationLatitude()))
        mapController.setCenter(startPoint);
        map.invalidate();

         */
    }

    protected void onStart() {
        super.onStart();
        if (!hasPermissions(this,PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            getGPS();
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ALL: {
                if (grantResults.length >= PERMISSIONS.length) {
                    for (int i=0; i<PERMISSIONS.length; i++) {
                        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this,"Nimaš dovoljenj",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                    initMapStartGPS();
                }
                else
                    finish();
            }

        }
    }

    private Polyline getPath() { //Singelton
        if (path==null) {
            path = new Polyline();
            path.setColor(Color.RED);
            path.setWidth(10);
            path.addPoint(startPoint.clone());
            map.getOverlayManager().add(path);
        }
        return path;
    }
    private Marker getPositionMarker() { //Singelton
        if (marker==null) {
            marker = new Marker(map);
            marker.setTitle("Here I am");
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(getResources().getDrawable(R.drawable.ic_position));
            map.getOverlays().add(marker);
        }
        return marker;
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
        Log.i(TAG,"onPause "+lok);
    }

    public void onResume(){
        super.onResume();
        map.onResume();
    }

    private void getGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocationActivity.this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    startPoint.setLatitude(latitude);
                    startPoint.setLongitude(longitude);
                    mapController.setCenter(startPoint);
                    getPositionMarker().setPosition(startPoint);
                    map.invalidate();
                }
            });
        }
        else {
            //permissions not granted
            Toast.makeText(this, "Permissions not granted!", Toast.LENGTH_LONG).show();
        }
    }
}