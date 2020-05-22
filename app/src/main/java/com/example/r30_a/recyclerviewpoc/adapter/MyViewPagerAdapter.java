package com.example.r30_a.recyclerviewpoc.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.model.ViewPagerData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by R30-A on 2019/1/24.
 */

public class MyViewPagerAdapter extends PagerAdapter{

    private ArrayList<ViewPagerData> list= new ArrayList<>();;
    private Context context;
    private ViewPager viewPager;
    private TextView txvTitle;
    private boolean isStop = false;//線程是否停止

    public MyViewPagerAdapter(Context context, ViewPager viewPager, ArrayList<ViewPagerData> list){
        this.context = context;
        this.list = list;
        this.viewPager = viewPager;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.news_layout,null);
        txvTitle = view.findViewById(R.id.txvTitle);
        if(list != null &&list.size()>0)
        txvTitle.setText(list.get(position % list.size()).getTitle());
        txvTitle.setGravity(Gravity.CENTER);
        txvTitle.setOnClickListener(v -> {
            Uri uri = Uri.parse(list.get(position % list.size()).getUrl());
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(uri);
            context.startActivity(intent);

        });
        viewPager.addView(view);
        return view;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view ==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if(list != null &&list.size()>0)
        viewPager.removeView(list.get(position % list.size()));
    }

}
