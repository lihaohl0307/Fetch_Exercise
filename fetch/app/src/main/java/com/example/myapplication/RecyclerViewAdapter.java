package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    List<Item> itemList;
    Context context;

    public RecyclerViewAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View itemView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item currentItem = itemList.get(position);
        // connect current item to the views
        holder.idTextView.setText(String.valueOf(currentItem.getId()));
        holder.listIdTextView.setText(String.valueOf(currentItem.getListId()));
        holder.nameTextView.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView idTextView;
        TextView listIdTextView;
        TextView nameTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.idTextView);
            listIdTextView = itemView.findViewById(R.id.listIdTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }
}
