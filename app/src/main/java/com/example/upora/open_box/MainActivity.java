package com.example.upora.open_box;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int ACTIVITY_ID = 123;

    Button btnScan;
    String message="";
    Vibrator vibrator;
    public String[] splited;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = findViewById(R.id.btnOpenBox);
        btnScan.setOnClickListener(this);
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

                        // tuki pride predvajanje žetona

                        finish();
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
}