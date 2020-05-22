package com.example.r30_a.recyclerviewpoc.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.controller.MainActivity;
import com.example.r30_a.recyclerviewpoc.controller.WelcomeActivity;
import com.example.r30_a.recyclerviewpoc.util.Util;

public class WelcomeFragment extends Fragment implements View.OnClickListener{
    private static final String PAGE_NUMBER = "page-number";
    public static int MAX_PAGE_NUMBER = 4;
    Intent intent = null;

    public WelcomeFragment() {}


    public static WelcomeFragment newInstance(int pageNumber) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_NUMBER, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome,container,false);

        int pageNumber = getArguments().getInt(PAGE_NUMBER);
        ImageView imgWelcome = v.findViewById(R.id.imgWelcome);
        TextView txvWelcomeTitle = v.findViewById(R.id.txvWelcomeTitle);


        //設定文字跟圖片隨著翻頁而改變
        txvWelcomeTitle.setText(((WelcomeActivity)getActivity()).getWelcomeTitle(pageNumber-1));
        imgWelcome.setImageResource(((WelcomeActivity)getActivity()).getImage(pageNumber-1));

        //翻到最後一頁時顯示可進行下一步的button
        if(pageNumber == MAX_PAGE_NUMBER){
            Button btnStart = v.findViewById(R.id.btn_start);
            btnStart.setVisibility(View.VISIBLE);
            btnStart.setOnClickListener(this);
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        intent = null;

        intent = new Intent(getActivity(), MainActivity.class);

        if ((intent!= null)){//第二次開啟就不進入歡迎畫面
            Util.setFirstTimeUse(getActivity(),false);
            startActivity(intent);
            getActivity().finish();
            intent = null;
        }
    }
}
