package com.example.r30_a.recylerviewpoc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.model.ContactData;

import java.util.ArrayList;

/**
 * Created by LUCA on 2018/12/12.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MainViewHolder>{


    ArrayList<ContactData> list = new ArrayList();
    LayoutInflater inflater;
    Context context;

    public MyAdapter(Context context, ArrayList<ContactData> list) {
        this.list = list;
        this.context = context;

    }


    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.contactslist_layout,parent,false);


        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {

        holder.txv_Name.setText(list.get(position).getName());
        holder.txv_PhoneNum.setText(list.get(position).getPhoneNum());
        if(list.get(position).getImg_avatar() != null){
            holder.img_avatar.setImageBitmap(list.get(position).getImg_avatar());
        }else{
            holder.img_avatar.setBackgroundResource(R.drawable.icon_avatar);
        }

    }




    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MainViewHolder extends RecyclerView.ViewHolder {

        TextView txv_Name, txv_PhoneNum;
        ImageView img_avatar;

        public MainViewHolder(View v) {
            super(v);
            txv_Name = v.findViewById(R.id.txv_Name);
            txv_PhoneNum = v.findViewById(R.id.txv_PhoneNum);
            img_avatar = v.findViewById(R.id.img_avatar);
        }

        public TextView getTxv_Name() {
            return txv_Name;
        }

        public TextView getTxv_PhoneNum() {
            return txv_PhoneNum;
        }

        public ImageView getImg_avatar() {
            return img_avatar;
        }
    }
}
