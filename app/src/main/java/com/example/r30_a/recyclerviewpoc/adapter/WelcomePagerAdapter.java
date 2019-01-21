package com.example.r30_a.recyclerviewpoc.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.r30_a.recyclerviewpoc.fragment.WelcomeFragment;

/**
 * Created by R30-A on 2018/12/22.
 */

public class WelcomePagerAdapter extends FragmentPagerAdapter {
    public WelcomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return WelcomeFragment.newInstance(position+1);
    }

    @Override
    public int getCount() {
        return WelcomeFragment.MAX_PAGE_NUMBER;
    }
}
