package tech.nextgen.unimacampusmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter <CustomAdapter.MyViewHolder> {

    //creating arraylists
    ArrayList<String> buildingNames;
    ArrayList<String> buildingId;
    Context ctx;

    //constructor

    public CustomAdapter(ArrayList<String> buildingNames, ArrayList<String> buildingId, Context ctx) {
        this.buildingNames = buildingNames;
        this.buildingId = buildingId;
        this.ctx = ctx;


    }
    //view holders
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        //inflate the item layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //set the data in items
        holder.buildingNames.setText(buildingNames.get(position));
        holder.buildingId.setText(buildingId.get(position));

        //adding onclick listener event on item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //displying a msg with building name
                Toast.makeText(ctx, buildingNames.get(position), Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    public int getItemCount(){
        return buildingNames.size();
    }

    //myholder class
    public class MyViewHolder extends RecyclerView.ViewHolder{
        //widgets
        TextView buildingNames, buildingId;


        //constructor
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            buildingNames = itemView.findViewById(R.id.building_name);
            buildingId = itemView.findViewById(R.id.building_id);
        }
    }
}
