package com.example.r30_a.recylerviewpoc.adapter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
            holder.img_avatar.setImageBitmap(list.get(position).getImg_avatar());
        }else{
            holder.img_avatar.setBackgroundResource(R.drawable.icon_avatar);
        }
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("請選擇功能");
//                builder.setPositiveButton("通話", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + list.get(position).getPhoneNum()));
//                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                            return;
//                        }
//                        context.startActivity(intent);
//                    }
//                }).setNeutralButton("傳簡訊", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+ list.get(position).getPhoneNum()));
//                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//                            return;
//                        }
//                        context.startActivity(intent);
//                    }
//                }).show();
//                return true;
//            }
//        });

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
