package com.example.r30_a.recyclerviewpoc.controller;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.adapter.WelcomePagerAdapter;
import com.example.r30_a.recyclerviewpoc.fragment.WelcomeFragment;
import com.example.r30_a.recyclerviewpoc.util.CommonUtil;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class WelcomeActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ArrayList<Integer> imgList = new ArrayList<>();
    private ArrayList<Integer> msgList = new ArrayList<>();
    private WelcomePagerAdapter welcomePagerApdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();
        welcomePagerApdater = new WelcomePagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager)findViewById(R.id.welcomeViewpager);
        viewPager.setAdapter(welcomePagerApdater);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        //標計頁數的小球
        CircleIndicator indicator = (CircleIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        //頁數沒有兩頁以上不顯示
        if(WelcomeFragment.MAX_PAGE_NUMBER <= 1){
            indicator.setVisibility(View.INVISIBLE);
        }
        if(CommonUtil.isFirstTimeUse(this)){
            CommonUtil.setFirstTimeUse(this,false);
        }
    }

    private void initView() {
        imgList.add(R.drawable.iconfinder_man_196742);
        imgList.add(R.drawable.sorry2);
        imgList.add(R.drawable.sorry);
        imgList.add(R.drawable.sorry3);

        msgList.add(R.string.welcome_page1);
        msgList.add(R.string.welcome_page2);
        msgList.add(R.string.welcome_page3);
        msgList.add(R.string.welcome_page4);
    }

    public int getImage(int pageNumber){
        return imgList.get(pageNumber);
    }
    public int getWelcomeTitle(int pageNumber){
        return msgList.get(pageNumber);
    }
}
