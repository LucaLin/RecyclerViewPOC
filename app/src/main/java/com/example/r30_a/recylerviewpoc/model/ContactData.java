package com.example.r30_a.recylerviewpoc.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.example.r30_a.recylerviewpoc.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by LUCA on 2018/12/12.
 */

public class ContactData {

    private String name;//名字
    private String phoneNum;//手機號碼
    private long id;//id
    private Bitmap img_avatar ;//大頭照
    private ImageView img_favor;//是否為常用清單的圖
    private String note;//記事
    private String address;//地址


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }




    private boolean isFavor = false;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }



    public boolean getIsFavor() {
        return isFavor;
    }

    public void isFavor(boolean favor) {
        isFavor = favor;
    }



    public ImageView getImg_favor() {

        return img_favor;

    }
    public void setFavorVisible(ImageView img_favor){
        img_favor.setVisibility(View.VISIBLE);
    };

    public void setImg_favor(ImageView img_favor) {
        this.img_favor = img_favor;
        img_favor.setBackgroundResource(android.R.drawable.star_on);

    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    private int number;

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public long getId() {
        return id;
    }

    public void setImg_avatar( Bitmap bitmap) {
        if(bitmap != null){
            img_avatar = bitmap;
        }else {
            img_avatar = null;
        }

    }
    public byte[] getImg_avatar() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(img_avatar != null){
            img_avatar.compress(Bitmap.CompressFormat.PNG,100,stream);
        }else {
            return null;
        }
        return stream.toByteArray();
    }
}
