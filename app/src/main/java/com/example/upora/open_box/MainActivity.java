package com.example.upora.open_box;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.upora.data.Box;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int ACTIVITY_ID = 123;

    Button btnScan;
    String message="";
    Vibrator vibrator;
    public String[] splited;
    String id;
    private RequestQueue queue;
    private MediaPlayer mediaPlayer;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    FusedLocationProviderClient fusedLocationProviderClient;

    float longiitude;
    float latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = findViewById(R.id.btnOpenBox);
        btnScan.setOnClickListener(this);
        queue = Volley.newRequestQueue(this);

        rootNode = FirebaseDatabase.getInstance("https://open-box-2021-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = rootNode.getReference("box");
    }

    @Override
    public void onClick(View v) {
        scanCode();
    }

    private void scanCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scanning code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()!= null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);


                splited = result.getContents().split("/");
                id = splited[1];
                builder.setMessage(id);

                builder.setTitle("Open box with id:");
                builder.setPositiveButton("No, scan again!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanCode();
                    }
                }).setNegativeButton("Yes open!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jsonParse();
                        //finish();

                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
            else{
                Toast.makeText(this,"No results",Toast.LENGTH_LONG).show();
            }
        }else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    private  void returnValue(String dataString){
        Intent data = getIntent();
        data.putExtra(String.valueOf(ACTIVITY_ID),message);
        setResult(RESULT_OK,data);
        finish();
    }

    private void jsonParse(){
        String url = "https://api-test.direct4.me/Sandbox/PublicAccess/V1/api/access/OpenBox?boxID="+ id +"&tokenFormat=2";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                String data = null;
                try {
                    data = response.getString("Data");
                    Log.i("response",data);
                    byte[] decoded = Base64.getDecoder().decode(data);

                    String newData = new String(decoded);
                    try (FileOutputStream fos = new FileOutputStream("data/data/com.example.upora.open_box/mndaZip.zip")) {
                        fos.write(decoded);
                        //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                    }

                    Log.i("decoded", newData);
                    //base64 teks unzipamo v token.wav
                    //zapa treba mediaplayer za toti token.wav
                    try
                    {
                        File file2 = new File("data/data/com.example.upora.open_box/token.wav");
                        file2.createNewFile(); //se naredi če še ne obstaja
                        file2.setExecutable(true);
                        FileOutputStream os = new FileOutputStream(file2, false);

                        unzip("data/data/com.example.upora.open_box/mndaZip.zip","data/data/com.example.upora.open_box/unZiped");

                        MediaPlayer mediaPlayer = MediaPlayer.create(getBaseContext(), Uri.parse("data/data/com.example.upora.open_box/unZiped/token.wav"));
                        mediaPlayer.start();

                        getGPS();

                        //vpraša ali se je nabiralik odprl ali ne in shrani v bazo
                        AlertDialog.Builder builderBoxOpened = new AlertDialog.Builder(MainActivity.this);
                        builderBoxOpened.setMessage(id);

                        /*AlertDialog.Builder builderLocation = new AlertDialog.Builder(MainActivity.this);
                        builderLocation.setMessage(id);

                        builderLocation.setTitle("Check location for box with id:");
                        builderLocation.setPositiveButton("No!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scanCode();
                            }
                        }).setNegativeButton("Yes!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(getBaseContext(),LocationActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                //startamo LocationActivity katera vsebuje mapo. Ta pridobi gps lokacijo telefona in označi z puščico na mapo trenutno lokacijo
                                // startActivity(new Intent(MainActivity.this, LocationActivity.class));
                            }
                        });
                        AlertDialog dialog3=builderLocation.create();
                        dialog3.show();*/

                        builderBoxOpened.setTitle("Ali se je nabiralnik odprl?");
                        builderBoxOpened.setPositiveButton("DA!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Box tmp = new Box( parseInt(id), LocalDateTime.now().toString(),true,longiitude,latitude);
                                String id = String.valueOf(getRandomNumber(1,50));
                                reference.child(id).setValue(tmp);

                            }
                        }).setNegativeButton("NE!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Box tmp = new Box( parseInt(id), LocalDateTime.now().toString(),false,longiitude,latitude);

                                String id = String.valueOf(getRandomNumber(1,50));
                                reference.child(id).setValue(tmp);
                            }
                        });
                        AlertDialog dialog2=builderBoxOpened.create();
                        dialog2.show();


                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i("Catch",error.toString());
            }
        });

        queue.add(request);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static boolean isZipped(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
                && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }

    public static Boolean unzip(String sourceFile, String destinationFolder)  {
        ZipInputStream zis = null;

        try {
            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourceFile)));
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                String fileName = ze.getName();
                fileName = fileName.substring(fileName.indexOf("/") + 1);
                File file = new File(destinationFolder, fileName);
                File dir = ze.isDirectory() ? file : file.getParentFile();

                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Invalid path: " + dir.getAbsolutePath());
                if (ze.isDirectory()) continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }

            }
        } catch (IOException  ioe){
            Log.d("TAG",ioe.getMessage());
            return false;
        }  finally {
            if(zis!=null)
                try {
                    zis.close();
                } catch(IOException e) {

                }
        }
        return true;
    }

    public void showList(View view) {
        Intent i = new Intent(getBaseContext(),ListOpened.class);
        //i.putExtra(ActivityAddPlayer.FORM_MODE_ID,ActivityAddPlayer.FORM_MODE_INSERT);
        startActivity(i);
    }

    private void getGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    longiitude = (float) location.getLongitude();
                    latitude = (float) location.getLatitude();
                }
            });
        }
        else {
            //permissions not granted
            Toast.makeText(this, "Permissions not granted!", Toast.LENGTH_LONG).show();
        }
    }

    public void openMaps(View view) {
        Intent i = new Intent(getBaseContext(),LocationActivity.class);
        //i.putExtra(ActivityAddPlayer.FORM_MODE_ID,ActivityAddPlayer.FORM_MODE_INSERT);
        startActivity(i);
    }
}