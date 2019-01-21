package com.example.r30_a.recyclerviewpoc.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.controller.DetailPageActivity;
import com.example.r30_a.recyclerviewpoc.model.ContactData;
import com.github.promeg.pinyinhelper.Pinyin;
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

        //設定大頭貼
        if(list.get(position).getImg_avatar() != null){
            Bitmap bitmap_avatar = BitmapFactory.decodeByteArray(list.get(position).getImg_avatar(),0,list.get(position).getImg_avatar().length);
            holder.img_avatar.setImageBitmap(bitmap_avatar);
        }else{//沒有大頭貼的話給一個預設圖
            holder.img_avatar.setBackgroundResource(R.drawable.iconfinder_man_196742);
        }
        //設定常用清單tag
        if(list.get(position).getFavorTag() == 1){
            list.get(position).setImg_favor(holder.img_favor);
//            holder.img_favor.setVisibility(View.VISIBLE);
        }else {
            list.get(position).setImg_normal(holder.img_favor);
        }


        //點擊進入detail頁面
        holder.infoZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContactData data = list.get(position);
//                String email_home = data.getEmail_home();
//                String email_company = data.getEmail_company();
//                String email_other = data.getEmail_other();
//                String email_custom = data.getEmail_custom();
                Intent intent = new Intent(context, DetailPageActivity.class);
                intent.putExtra("id",data.getId());
                intent.putExtra("number",data.getNumber());
                intent.putExtra("name",data.getName());
                intent.putExtra("phoneNumber",data.getPhoneNum());
                intent.putExtra("note",data.getNote());
                intent.putExtra("city",data.getCity());
                intent.putExtra("street",data.getStreet());
                //bytes[] to base64
                intent.putExtra("avatar",data.getImg_avatar());
                intent.putExtra("email_home",data.getEmail_home());
                intent.putExtra("email_company",data.getEmail_company());
                intent.putExtra("email_other",data.getEmail_other());
                intent.putExtra("email_custom",data.getEmail_custom());
                context.startActivity(intent);
            }
        });

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
        //TextView txvrunrun;
        LinearLayout infoZone;
        public MainViewHolder(View v) {
            super(v);
            txv_Name = v.findViewById(R.id.txv_Name);
            txv_PhoneNum = v.findViewById(R.id.txv_PhoneNum);
            img_avatar = v.findViewById(R.id.img_avatar);
            contactData_layout = v.findViewById(R.id.contactData_layout);
            img_favor = v.findViewById(R.id.img_favor);
            number = v.findViewById(R.id.number);
            infoZone = v.findViewById(R.id.info_zone);

        }
    }

    public int getPosForSection(int section){
        for(int i=0; i<getItemCount(); i++){

            String str = Pinyin.toPinyin(list.get(i).getName().charAt(0));
            char firstChar = str.toUpperCase().charAt(0);//取拼音的第一個字元代號
            if(firstChar == section){
                return i;//有找到符合的話，跳頁至該資料的第一筆
            }
        }
        return -1;//找不到符合的項合就不動作
    }
}
