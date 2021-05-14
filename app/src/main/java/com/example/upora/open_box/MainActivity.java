package com.example.upora.open_box;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int ACTIVITY_ID = 123;

    Button btnScan;
    String message="";
    Vibrator vibrator;
    public String[] splited;
    String id;
    private RequestQueue queue;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = findViewById(R.id.btnOpenBox);
        btnScan.setOnClickListener(this);
        queue = Volley.newRequestQueue(this);
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
                    Log.i("decoded", newData);
                    //base64 teks unzipamo v token.wav
                    //zapa treba mediaplayer za toti token.wav
                    try
                    {
                        File file2 = new File("token.wav");
                        file2.createNewFile(); //se naredi če še ne obstaja
                        FileOutputStream os = new FileOutputStream(file2, true);
                        os.write(decoded);
                        os.close();
                        Uri uri = Uri.fromFile(file2);
                        //MediaPlayer mediaPlayer = MediaPlayer.create(this, uri);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
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
    public static boolean isZipped(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
                && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }
}