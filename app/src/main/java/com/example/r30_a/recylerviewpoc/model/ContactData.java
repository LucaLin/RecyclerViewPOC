package com.example.r30_a.recylerviewpoc.model;

import android.widget.ImageView;

/**
 * Created by LUCA on 2018/12/12.
 */

public class ContactData {

    private String name;
    private String phoneNum;
    private String id;
    private ImageView img_avatar;

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getId() {
        return id;
    }

    public void setImg_avatar(ImageView img_avatar) {
        this.img_avatar = img_avatar;
    }

    public ImageView getImg_avatar() {
        return img_avatar;
    }
}
