package com.example.r30_a.recylerviewpoc.adapter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.model.ContactData;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LUCA on 2018/12/12.
 */

public class MyAdapter extends SwipeMenuAdapter<MyAdapter.MainViewHolder> implements View.OnClickListener{

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
        holder.number.setText(String.valueOf(list.get(position).getNumber()));
        holder.img_favor.setVisibility(View.INVISIBLE);
        //設定大頭貼
        if(list.get(position).getImg_avatar() != null){
            Bitmap bitmap_avatar = BitmapFactory.decodeByteArray(list.get(position).getImg_avatar(),0,list.get(position).getImg_avatar().length);
            holder.img_avatar.setImageBitmap(bitmap_avatar);
        }else{//沒有大頭貼的話給一個預設圖
            holder.img_avatar.setBackgroundResource(R.drawable.iconfinder_man_196742);
        }
        //設定常用清單tag
        if(list.get(position).getImg_favor() != null && list.get(position).getIsFavor()){
            list.get(position).setImg_favor(holder.img_favor);
            holder.img_favor.setVisibility(View.VISIBLE);
        }

        holder.imgbtn_dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_dial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + list.get(position).getPhoneNum()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(intent_dial);

            }
        });

        holder.imgbtn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_sms = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+ list.get(position).getPhoneNum()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(intent_sms);
            }
        });

//        holder.contactData_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String number = list.get(position).getPhoneNum();
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                //通話
//                builder.setPositiveButton(R.string.dial, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent_dial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
//                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                                return;
//                            }
//                            context.startActivity(intent_dial);
//                    }
//                //簡訊
//                }).setNegativeButton(R.string.smsto, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent_sms = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+ list.get(position).getPhoneNum()));
//                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//                            return;
//                        }
//                        context.startActivity(intent_sms);
//                    }
//                }).setTitle(R.string.hint)
//                  .create();
//                builder.show();
//            }
//        });

    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getNumber();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }


    public class MainViewHolder extends RecyclerView.ViewHolder {

        TextView txv_Name, txv_PhoneNum, number;
        ImageView img_avatar;//大頭貼
        RelativeLayout contactData_layout;
        ImageView img_favor;//常用清單tag
        ImageView imgbtn_dial,imgbtn_sms;
        public MainViewHolder(View v) {
            super(v);
            txv_Name = v.findViewById(R.id.txv_Name);
            txv_PhoneNum = v.findViewById(R.id.txv_PhoneNum);
            img_avatar = v.findViewById(R.id.img_avatar);
            contactData_layout = v.findViewById(R.id.contactData_layout);
            img_favor = v.findViewById(R.id.img_favor);
            number = v.findViewById(R.id.number);
            imgbtn_dial = v.findViewById(R.id.imgbtn_dial);
            imgbtn_sms = v.findViewById(R.id.imgbtn_sms);

        }

    }
}
