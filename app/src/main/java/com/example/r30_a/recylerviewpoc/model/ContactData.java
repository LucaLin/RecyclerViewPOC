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

    private String name;
    private String phoneNum;
    private long id;
    private Bitmap img_avatar ;
    private ImageView img_favor;

    public ImageView getImg_favor() {

        return img_favor;

    }
    public void setFavorVisible(ImageView img_favor){
        img_favor.setVisibility(View.VISIBLE);
    };


    public void setImg_favor(ImageView img_favor) {
        this.img_favor = img_favor;

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
