package com.example.upora.open_box;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.upora.data.Box;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ListOpened extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter adapter;

    public ArrayList<Box> listOfBoxes = new ArrayList<>();

    Box tmp;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_opened);

        recyclerView = findViewById(R.id.recyclerView);

        rootNode = FirebaseDatabase.getInstance("https://open-box-2021-default-rtdb.europe-west1.firebasedatabase.app/");
       // reference = rootNode.getReference("box");
        for(int i =0;i<=50;i++){
            reference = rootNode.getReference("box/"+i);
            tmp = new Box();
            getAllInfo();
        }

        initAdapter();
        Log.i("stevilo", String.valueOf(listOfBoxes.size()));
    }

    private void getAllInfo(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    tmp = snapshot.getValue(Box.class);
                    listOfBoxes.add(tmp);
                    Log.i("Box iz baze",tmp.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("Firebase error",error.getMessage());
            }
        });
    }


    private void initAdapter() {
        adapter = new Adapter(listOfBoxes, new Adapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View itemView, int position) {
                /*adapter.notifyDataSetChanged();
                Intent i = new Intent(getBaseContext(),ActivityInsert.class);
                i.putExtra(ActivityInsert.FORM_MODE_ID,ActivityInsert.FORM_MODE_UPDATE);
                i.putExtra("position",position);*/
                adapter.notifyDataSetChanged();
               // startActivity(i);*/
            }

            @Override
            public void OnLongItemClick(View itemView, int position) {
                /*app.removeAt(position);
                adapter.notifyDataSetChanged();*/
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}