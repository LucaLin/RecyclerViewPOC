package com.example.r30_a.recyclerviewpoc.view;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.adapter.MyAdapter;
import com.example.r30_a.recyclerviewpoc.model.ContactData;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;

/**
 * Created by R30-A on 2019/3/6.
 */

public class MyCustomSearchView extends FrameLayout implements View.OnClickListener{

    public View btnOpenSearch, btnCloseSearch;
    public RelativeLayout search_open_view, search_close_view;
    public EditText edtInput;

    public MyCustomSearchView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MyCustomSearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context) {

        LayoutInflater.from(context).inflate(R.layout.view_search,this,true);

        btnOpenSearch = this.findViewById(R.id.open_search_button);
        btnCloseSearch = this.findViewById(R.id.close_search_button);

        btnOpenSearch.setOnClickListener(this);
        btnCloseSearch.setOnClickListener(this);

        search_close_view = this.findViewById(R.id.search_closed_view);
        search_open_view = this.findViewById(R.id.search_open_view);

        edtInput = this.findViewById(R.id.search_input_text);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.open_search_button:

                search_open_view.setVisibility(VISIBLE);
                Animator animator_open = ViewAnimationUtils.createCircularReveal(search_open_view,
                        (btnOpenSearch.getRight() + btnOpenSearch.getLeft()) / 2,
                        (btnOpenSearch.getTop() + btnOpenSearch.getBottom()) /2 ,
                        0f,Float.valueOf(v.getWidth()));
                animator_open.setDuration(300);
                animator_open.start();

                break;
            case R.id.close_search_button:

                final Animator animator_close = ViewAnimationUtils.createCircularReveal(search_open_view,
                        (btnOpenSearch.getRight() + btnOpenSearch.getLeft()) / 2,
                        (btnOpenSearch.getTop() + btnOpenSearch.getBottom()) /2 ,
                        Float.valueOf(v.getWidth()),0f);
                animator_close.setDuration(300);
                animator_close.start();

                animator_close.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        search_open_view.setVisibility(INVISIBLE);
                        edtInput.setText("");
                        animator_close.removeAllListeners();
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {}
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });

                break;

        }
    }
}
