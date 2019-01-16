package com.example.r30_a.recylerviewpoc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.example.r30_a.recylerviewpoc.R;

import java.util.ArrayList;

/**
 * Created by R30-A on 2019/1/16.
 */

public class SideBar extends View{

    ArrayList<String> alphaList = new ArrayList<>();
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    private String[] alphabet = {
            "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X",
            "Y", "Z" , "#"
    };

    private int choose = -1;//選中的tag
    private Paint paint = new Paint();
    private float singleHeight;
    private String textColor = "#636363";
    private String selectTextColor = "#3399ff";
    private int textSize = 40;
    private int bigTextSize = 80;

    public SideBar(Context context,ArrayList<String> list){
        super(context);
        alphaList = list;
    }

    public SideBar(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        textSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,context.getResources().getDisplayMetrics());
        bigTextSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,32,context.getResources().getDisplayMetrics());
    }

    public SideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(Color.parseColor("#00000000"));

        int height = getHeight();//獲取高度
        int width = getWidth();//獲取寬度

        singleHeight = (height * 1f) /alphabet.length;//每個字母的高度

        //singleHeight = (height * 1f -singleHeight /2) / alphabet.length;
        for(int i =0;i<alphabet.length;i++){
            paint.setColor(Color.parseColor(textColor));

            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            int t = textSize;
            paint.setTextSize(t);


            if(i == choose){
                paint.setColor(Color.parseColor("#c60000"));
                paint.setFakeBoldText(true);
            }
            //X坐標等於中間-字母串寬度的一半
            float xpos = width/1.2f - paint.measureText(alphabet[i])/2;
            float ypos = singleHeight * i + singleHeight;
            //選中時的狀態
            if(recordY >=0){
                float xp = xpos -textSize * 9 +Math.abs(recordY - ypos);
                if(xp <0 ){
                    xpos = xpos+xp;//字出去的距離
                    t = (t-(int)(xp /2 +0.5));//字的大小
                    paint.setColor(Color.argb((int)(textSize *6 +xp -0.5),63,63,63));//字的顏色
                }
                if(i == choose){
                    paint.setColor(Color.parseColor(selectTextColor));
                    paint.setFakeBoldText(true);
                }
            }
            paint.setTextSize(t);
            canvas.drawText(alphabet[i],xpos,ypos,paint);
            paint.reset();

        }

    }

    float recordY = -1;
    private float oldX;
    private float oldY;
    private int state;//選取狀態，0=未處理，1=獲取，2=未獲取

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        float y = event.getY();//獲取y坐標
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int)(y / getHeight() * alphabet.length);//點擊y坐標所占總高度的比例 * 字母串的長度 = 點擊中字母的個數

        switch (action){
            case MotionEvent.ACTION_UP:
                recordY = -1;
                choose = -1;
                invalidate();
                break;
            case MotionEvent.ACTION_DOWN:
                state = 0;
                oldX = event.getX();
                oldY = event.getY();
                updateView(event,oldChoose,listener,c);
                break;
            default:
                if(state ==0){
                    float newX = event.getX();
                    float newY = event.getY();
                    //如果是橫著滑動，就重置狀態
                    if((oldX-newX) * (oldX-newX) +
                       (oldY-newY) * (oldY-newY) >bigTextSize){
                        if((oldX-newX) * (oldX-newX) > (oldY-newY) * (oldY-newY)){
                            recordY = -1;
                            choose = -1;
                            invalidate();
                            state = 2;
                            return true;
                        }
                    }else {
                        state =1;
                    }
                }else if(state==2){
                    return true;
                }
                updateView(event,oldChoose,listener,c);
                break;


        }
        invalidate();//刷新屏幕
        return true;


    }
    private void updateView(MotionEvent e,final int oldChoose,final OnTouchingLetterChangedListener listener,
                            final int c){
        recordY = e.getY();
        if(oldChoose != c){
            if(c >= 0 && c< alphabet.length){
                if(listener != null){
                    listener.onTouchingChanged(alphabet[c]);

                }
                choose = c;
            }
        }

    }

    //    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        final int action = event.getAction();
//        final float y = event.getY();
//        final int oldChoose = choose;
//        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
//        final int c = (int)(y / getHeight() * alphabet.length);
//
//        switch (action){
//            case MotionEvent.ACTION_UP:
//                setBackgroundDrawable(new ColorDrawable(0x00000000));
//                choose = -1;
//                invalidate();
//
//                break;
//            default:
//                setBackgroundResource(R.color.colorAccent);
//                if(oldChoose != c){
//                    if(c >=0 && c< alphabet.length){
//                        if(listener != null){
//                            listener.onTouchingChanged(alphabet[c]);
//                        }
//                        choose = c;
//                        invalidate();
//                    }
//                }
//        }
//
//        return true;
//    }
    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener){
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener{
        public void onTouchingChanged(String s);
    }
}
