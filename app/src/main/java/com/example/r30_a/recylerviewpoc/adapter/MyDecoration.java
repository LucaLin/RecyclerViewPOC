package com.example.r30_a.recylerviewpoc.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.example.r30_a.recylerviewpoc.R;

/**
 * sampled by luca on 2018/12/22.
 */

//自定義清單抬頭
public class MyDecoration extends RecyclerView.ItemDecoration {

    private DecorationCallBack callBack;
    private Paint paint;//抬頭畫筆
    private TextPaint textPaint;//抬頭文字畫筆
    private int topGap;
    private Paint.FontMetrics fontMetrics;
    String lastText="";

    public MyDecoration(Context context, DecorationCallBack callBack) {
        Resources resources = context.getResources();
        this.callBack = callBack;

        paint = new Paint();
//        paint.setColor(context.getResources().getColor(R.color.colorPrimary));
        paint.setColor(Color.parseColor("#77a6e4"));


        textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        textPaint.setAntiAlias(true);
        textPaint.setTextSize(65);
        textPaint.getFontMetrics(fontMetrics);
        textPaint.setTextAlign(Paint.Align.LEFT);
        fontMetrics = new Paint.FontMetrics();

        topGap = resources.getDimensionPixelSize(R.dimen.section_top);

    }

//繪製抬頭區域
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);


        int left = parent.getLeft();
        int right = parent.getRight()-parent.getPaddingRight();
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++){

            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            long groupId = callBack.getGroupId(position);

            if(groupId<0){return;}
            //取得清單的第一個字
            String textLine = callBack.getGroupFirstLine(position).toUpperCase();


            //群組的第一個才加
            if((position == 0  || isFirstInGroup(position)) && !lastText.equals(textLine)){

                float top = view.getTop()-topGap;
                float bottom = view.getTop();

                //繪製一個方形區塊，範圍需要上下左右的長寬

                    c.drawRect(left,top,right,bottom,paint);
                    c.drawText(textLine,left,bottom-4,textPaint);
                    lastText = textLine;

            }
        }
    }

    //滑動時的群組欄要保留的操作
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int left = parent.getLeft();
        int right = parent.getRight()-parent.getPaddingRight();
        int itemCount = state.getItemCount();
        int childCount = parent.getChildCount();

        float lineHeight = textPaint.getTextSize() + fontMetrics.descent;

        long pregroupId, groupId = -1;

        for(int i = 0; i< childCount; i++){
            View view = parent.getChildAt(i);
            int pos = parent.getChildAdapterPosition(view);

            pregroupId = groupId;
            groupId = callBack.getGroupId(pos);

            if(groupId < 0 || groupId == pregroupId){
                continue;
            }

            String textLine = callBack.getGroupFirstLine(pos).toUpperCase();
            if(TextUtils.isEmpty(textLine)){
                continue;
            }

            int viewBottom = view.getBottom();
            float textY = Math.max(topGap,view.getTop());
            if( pos+1 <itemCount){

                long nextGroupId = callBack.getGroupId(pos+1);
                if(nextGroupId !=groupId && viewBottom < textY){
                    textY = viewBottom;
                }
            }
            c.drawRect(left,textY-topGap,right,textY,paint);
            c.drawText(textLine,left,textY,textPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int pos = parent.getChildAdapterPosition(view);
        long groupId = callBack.getGroupId(pos);
        if(groupId<0){return;}
        if(pos == 0 ||isFirstInGroup(pos)){
            outRect.top = topGap;
        }else {
            outRect.top = 0;
        }
    }

    private boolean isFirstInGroup(int pos){
        if(pos == 0){
            return true;
        }else {

            long preGroupId = callBack.getGroupId(pos -1);
            long groupId = callBack.getGroupId(pos);
            return preGroupId != groupId;
        }
    }

    public interface DecorationCallBack {

        long getGroupId(int pos);

        String getGroupFirstLine(int pos);
    }

}

