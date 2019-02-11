package com.example.r30_a.recyclerviewpoc.view;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by R30-A on 2019/1/31.
 */

public class MyFloatButton extends FloatingActionButton implements View.OnTouchListener{

    private final static float CLICK_DRAG_TOLERANCE = 10; // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.
    private float downRawX,downRawY;
    private float dx,dy;

    public MyFloatButton(Context context) {
        super(context);
        init();
    }



    public MyFloatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyFloatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getAction();


        if(action == MotionEvent.ACTION_DOWN){

            downRawX = event.getRawX();
            downRawY = event.getRawY();
            dx = v.getX()-downRawX;
            dy = v.getY()-downRawY;

            return true;

        }else if(action == MotionEvent.ACTION_MOVE){

            int viewWidth = v.getWidth();
            int viewHeight = v.getHeight();

            View viewParent = (View)v.getParent();
            int parentWidth = viewParent.getWidth();
            int parentHeight = viewParent.getHeight();

            float newX = event.getRawX() + dx;
            newX = Math.max(0,newX);//為了不讓按鈕超出左邊畫面
            newX = Math.min(parentWidth - viewWidth, newX);//為了不讓按鈕超出右邊畫面

            float newY = event.getRawY() + dy;
            newY = Math.max(0,newY);//為了不讓按鈕超出頂部畫面
            newY = Math.min(parentHeight - viewHeight, newY);//為了不讓按鈕超出底部畫面

            v.animate()
                    .x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start();
            return true;

        }else if(action == MotionEvent.ACTION_UP){

            float upRawX = event.getRawX();
            float upRawY = event.getRawY();

            float upDX = upRawX - downRawX;
            float upDY = upRawY - downRawY;

            if(Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE){
                    return performClick();
            }else {
                return true;
            }

        }else{
            return super.onTouchEvent(event);
        }


    }
}
