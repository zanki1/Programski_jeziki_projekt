package com.example.upora.open_box;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upora.data.Box;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private OnItemClickListener listener;

    ArrayList<Box> listOfBoxes;

    public Adapter(ArrayList<Box> listOfBoxes, OnItemClickListener listener ) {
        this.listOfBoxes = listOfBoxes;
        this.listener=listener;
    }

    public Box getPracticePosition(int position){
        return listOfBoxes.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtID;
        public TextView txtDateOpened;
        public TextView txtOpened;
        public ImageView iv;
        public View background;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtID = (TextView) itemView.findViewById(R.id.txtID);
            txtDateOpened = (TextView) itemView.findViewById(R.id.txtDateOpened);
            txtOpened = (TextView) itemView.findViewById(R.id.txtOpened);
            iv = (ImageView) itemView.findViewById(R.id.rowicon);
            background = itemView.findViewById(R.id.myLayouthrow);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION)
                            listener.OnLongItemClick(itemView,position);
                    }
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION)
                            listener.OnItemClick(itemView,position);
                    }

                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.rv_rowlayout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Box tmp = getPracticePosition(position);
        holder.txtID.setText("ID paketnika: " + tmp.getBoxID());
        holder.txtDateOpened.setText("Datum: " + String.valueOf(tmp.getDateStamp()));

        if(tmp.isOpened()){
            holder.txtOpened.setText("Uspešno odprt: DA");
            holder.iv.setImageResource(R.drawable.ic_green);
            //holder.iv.setImage(R.drawable.ic_green);

        }
        else{
            holder.txtOpened.setText("Uspešno odprt: NE");
            holder.iv.setImageResource(R.drawable.ic_red);
        }
    }

    @Override
    public int getItemCount() {
        try{
            return listOfBoxes.size();
        }
        catch (Exception e){
            Log.i("Error",e.getMessage());
            return 0;
        }
    }

    public interface OnItemClickListener{
        void OnItemClick(View itemView, int position);
        void OnLongItemClick(View itemView, int position);
    }
}
