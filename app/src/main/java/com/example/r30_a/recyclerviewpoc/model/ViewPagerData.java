package com.example.r30_a.recyclerviewpoc.model;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by R30-A on 2019/1/24.
 */

public class ViewPagerData extends View{
    String title;
    String url;
    String content;
    String time;

    public ViewPagerData(Context context,String title,String url){
        super(context);
        this.title = title;
        this.url = url;
    }


    public ViewPagerData(Context context, String title, String url, String content, String time) {
        super(context);
        this.title = title;
        this.url = url;
        this.content = content;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
