package com.example.upora.open_box;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.upora.data.Box;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ListOpened extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter adapter;

    public ArrayList<Box> listOfBoxes = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_opened);

        Box tmp = new Box(5423,stringToLocalDateTime("2020-05-05","00:00"),true,25,25);
        listOfBoxes.add(tmp);

        recyclerView = findViewById(R.id.recyclerView);
        initAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDateTime stringToLocalDateTime(String date, String time){
        String dateTime = date+" "+time;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(dateTime, formatter);
    }

    private void initAdapter() {
        adapter = new Adapter(listOfBoxes, new Adapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View itemView, int position) {
                /*adapter.notifyDataSetChanged();
                Intent i = new Intent(getBaseContext(),ActivityInsert.class);
                i.putExtra(ActivityInsert.FORM_MODE_ID,ActivityInsert.FORM_MODE_UPDATE);
                i.putExtra("position",position);
                adapter.notifyDataSetChanged();
                startActivity(i);*/
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