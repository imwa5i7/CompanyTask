package com.imwasil.companytask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.DataHolder> {
    private ArrayList<Data> dataList;
    private Context mContext;

    public ListAdapter(ArrayList<Data> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.list_layout,parent,false);
        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, int position) {
        Data data=dataList.get(position);
        holder.edComment.setText(data.getComment());
        Glide.with(mContext).load(data.getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        if (dataList==null){
            return 1;
        }
        else {
            return dataList.size();
        }
    }

    public  class DataHolder extends RecyclerView.ViewHolder {
        TextView edComment;
        ImageView image;
        public DataHolder(@NonNull View itemView) {
            super(itemView);
            edComment=itemView.findViewById(R.id.layout_comment_id);
            image=itemView.findViewById(R.id.layout_image_id);
        }
    }
}
