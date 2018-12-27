package com.example.r30_a.recylerviewpoc.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.controller.MainActivity;
import com.example.r30_a.recylerviewpoc.controller.WelcomeActivity;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;

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
        View view = inflater.inflate(R.layout.fragment_welcome,container,false);

        int pageNumber = getArguments().getInt(PAGE_NUMBER);
        ImageView imgWelcome = (ImageView)view.findViewById(R.id.imgWelcome);
        TextView txvWelcomeTitle = (TextView)view.findViewById(R.id.txvWelcomeTitle);


        //設定文字跟圖片隨著翻頁而改變
        txvWelcomeTitle.setText(((WelcomeActivity)getActivity()).getWelcomeTitle(pageNumber-1));
        imgWelcome.setImageResource(((WelcomeActivity)getActivity()).getImage(pageNumber-1));

        //翻到最後一頁時顯示可進行下一步的button
        if(pageNumber == MAX_PAGE_NUMBER){
            Button btnStart = (Button)view.findViewById(R.id.btn_start);
            btnStart.setVisibility(View.VISIBLE);
            btnStart.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        intent = null;

        intent = new Intent(getActivity(), MainActivity.class);

        if ((intent!= null)){//第二次開啟就不進入歡迎畫面
            CommonUtil.setFirstTimeUse(getActivity(),false);
            startActivity(intent);
            getActivity().finish();
            intent = null;
        }
    }
}
