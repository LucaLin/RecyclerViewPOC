package com.example.r30_a.recyclerviewpoc.model;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.example.r30_a.recyclerviewpoc.R;

import java.io.ByteArrayOutputStream;

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
    private String city;
    private String street;
    private String address;//地址
    private String email_home;
    private String email_company;
    private String email_other;
    private String email_custom;
    private int favorTag=0;
    private String letter;//字首

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public int getFavorTag() {
        return favorTag;
    }

    public void setFavorTag(int favorTag) {
        this.favorTag = favorTag;
    }

    public String getEmail_home() {
        return email_home;
    }

    public void setEmail_home(String email_home) {
        this.email_home = email_home;
    }

    public String getEmail_company() {
        return email_company;
    }

    public void setEmail_company(String email_company) {
        this.email_company = email_company;
    }

    public String getEmail_other() {
        return email_other;
    }

    public void setEmail_other(String email_other) {
        this.email_other = email_other;
    }

    public String getEmail_custom() {
        return email_custom;
    }

    public void setEmail_custom(String email_custom) {
        this.email_custom = email_custom;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

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
        this.img_favor.setBackgroundResource(R.drawable.icons8_starfavor_24);

    }
    public void setImg_normal(ImageView img_normal){
        this.img_favor = img_normal;
        this.img_favor.setBackgroundResource(R.drawable.icons8_star_white_24);
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
