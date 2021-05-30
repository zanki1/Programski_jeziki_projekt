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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.upora.data.Box;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
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

    public ArrayList<Box> listOfBoxes = new ArrayList<>();
    public ArrayList<GeoPoint> listOfGeoPoints = new ArrayList<>();
    public ArrayList<Marker> listOfMarkers = new ArrayList<>();

    Box tmp;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    Box tmpbox = new Box(15, "", true,16.197500f, 46.520556f );

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

        rootNode = FirebaseDatabase.getInstance("https://open-box-2021-default-rtdb.europe-west1.firebasedatabase.app/");
        // reference = rootNode.getReference("box");
        for(int i =0;i<=50;i++){
            reference = rootNode.getReference("box/"+i);
            tmp = new Box();
            getAllInfo();
        }

    }

    private void getAllInfo(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    tmp = snapshot.getValue(Box.class);
                    listOfGeoPoints.add(new GeoPoint(tmp.getLatitude(),tmp.getLongitude()));
                    Log.i("Box iz baze",tmp.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("Firebase error",error.getMessage());
            }
        });
    }

    public void initMapStartGPS() {

        mapController = map.getController();
        mapController.setZoom(18.5);
        startPoint = new GeoPoint(tmpbox.getLatitude(),tmpbox.getLongitude());
        //Log.i("Latitude", String.valueOf(tmpUser.getLocationLatitude()))
        mapController.setCenter(startPoint);
        map.invalidate();


    }

    protected void onStart() {
        super.onStart();
        if (!hasPermissions(this,PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            initMapStartGPS();
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
                    setLocation(location);
                }
            });
        }
        else {
            //permissions not granted
            Toast.makeText(this, "Permissions not granted!", Toast.LENGTH_LONG).show();
        }
    }

    public void showLocation(View view) {
        getGPS();
    }

    private void setLocation(Location location) {
        startPoint.setLongitude(location.getLongitude());
        startPoint.setLatitude(location.getLatitude());
        mapController.setCenter(startPoint);
        getPositionMarker().setPosition(startPoint);
        map.invalidate();
    }

    public void showAll(View view) {
        for(int i = 0;i<listOfGeoPoints.size();i++){
            //getPositionMarker().setPosition(listOfGeoPoints.get(i));
            listOfMarkers.add(new Marker(map));
            listOfMarkers.get(i).setTitle("Here I am");
            listOfMarkers.get(i).setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            listOfMarkers.get(i).setIcon(getResources().getDrawable(R.drawable.ic_position));
            map.getOverlays().add(listOfMarkers.get(i));
            listOfMarkers.get(i).setPosition(listOfGeoPoints.get(i));
        }
        map.invalidate();
    }
}