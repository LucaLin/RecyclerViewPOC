package com.example.r30_a.recylerviewpoc.adapter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.model.ContactData;
import com.example.r30_a.recylerviewpoc.slideHelper.Extension;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LUCA on 2018/12/12.
 */

public class MyAdapter extends SwipeMenuAdapter<MyAdapter.MainViewHolder> {

    ArrayList<ContactData> list = new ArrayList();
    Context context;

    public MyAdapter(Context context, ArrayList<ContactData> list) {
        this.list = list;
        this.context = context;

    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.contactslist_layout,parent,false);

        return v;
    }

    @Override
    public MainViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {

        return new MainViewHolder(realContentView);
    }

    @Override
    public void onCompatBindViewHolder(MainViewHolder holder, int position, List<Object> payloads) {
        super.onCompatBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, final int position) {

        holder.txv_Name.setText(list.get(position).getName());
        holder.txv_PhoneNum.setText(list.get(position).getPhoneNum());
        if(list.get(position).getImg_avatar() != null){
            Bitmap bitmap_avatar = BitmapFactory.decodeByteArray(list.get(position).getImg_avatar(),0,list.get(position).getImg_avatar().length);
            holder.img_avatar.setImageBitmap(bitmap_avatar);
        }else{
            holder.img_avatar.setBackgroundResource(R.drawable.iconfinder_man_196742);
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
        RelativeLayout contactData_layout;

        public MainViewHolder(View v) {
            super(v);
            txv_Name = v.findViewById(R.id.txv_Name);
            txv_PhoneNum = v.findViewById(R.id.txv_PhoneNum);
            img_avatar = v.findViewById(R.id.img_avatar);
            contactData_layout = v.findViewById(R.id.contactData_layout);

        }

    }
}
