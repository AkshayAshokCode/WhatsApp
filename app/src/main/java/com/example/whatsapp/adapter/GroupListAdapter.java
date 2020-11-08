package com.example.whatsapp.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.model.GroupList;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.Holder> {
    private List<GroupList> list;
    private Context context;

    public GroupListAdapter(List<GroupList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_group_list,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        GroupList groupList=list.get(position);
        holder.tvName.setText(groupList.getUserName());
        holder.tvDesc.setText(groupList.getDescription());
        holder.tvDate.setText(groupList.getDate());

        Glide.with(context).load(groupList.getUrlProfile()).into(holder.profile);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDesc, tvDate;
        private CircularImageView profile;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tv_name);
            tvDate=itemView.findViewById(R.id.tv_date);
            tvDesc=itemView.findViewById(R.id.tv_desc);
            profile=itemView.findViewById(R.id.image_profile);
        }
    }
}

