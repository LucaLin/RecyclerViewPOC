package com.example.r30_a.recyclerviewpoc.controller;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.adapter.MyViewPagerAdapter;
import com.example.r30_a.recyclerviewpoc.model.ViewPagerData;
import com.example.r30_a.recyclerviewpoc.util.CommonUtil;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_toContactPage, btn_toSettingPage, btn_toProfile;
    AlertDialog.Builder builder;
    ViewPager viewPager;
    MyViewPagerAdapter myViewPagerAdapter;
    private boolean isStop = false;//線程是否停止
    ArrayList<ViewPagerData> myNewsList = new ArrayList<>();

    Button btnInput;

//    InputMethodManager inputMethodManager;
//    Thread thread;
//    String packageName = "com.mitake.android.scb";
//    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //第一次使用的話先開歡迎畫面
        if (CommonUtil.isFirstTimeUse(this)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        initView();
        autoplayView();


        //取得28字元的金鑰(註冊fb用)
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.example.r30_a.recyclerviewpoc",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//
//                String s = Base64.encodeToString(
//                        md.digest(),
//                        Base64.DEFAULT);
//                Log.d("MyKeyHash:", Base64.encodeToString(
//                        md.digest(),
//                        Base64.DEFAULT));
//            }
//        } catch (Exception e) {
//            Log.d("FacebookHashKeyError",e.toString());
//        }

        //myViewPagerAdapter = new MyViewPagerAdapter(this,viewPager,)


    }

//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//
//            do {
//                if(isThisImeOpen(packageName,inputMethodManager)){
//                    Intent intent = new Intent();
////                    intent.setFlags(
////                            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED |
////                                    Intent.FLAG_ACTIVITY_SINGLE_TOP
////                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.setClass(MainActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    break;
//                }
//
//            } while (true);
//        }
//    };

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if(flag ==0) {
//                switch (msg.what) {
//                    case 0:
//                        if (isThisImeOpen(packageName, inputMethodManager)) {
//                            Intent intent = new Intent();
//                            intent.setFlags(
//                                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED |
//                                            Intent.FLAG_ACTIVITY_SINGLE_TOP
//                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.setClass(MainActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            break;
//                        }
//                    case 1:
//                        if (!isThisImeOpen(packageName, inputMethodManager)) {
//                            Intent intent = new Intent();
//                            intent.setFlags(
//                                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED |
//                                            Intent.FLAG_ACTIVITY_SINGLE_TOP
//                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.setClass(MainActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            break;
//                        }
//                }
//            }
//
//        }
//    };

    private void autoplayView() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (viewPager != null) {
                                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                            }
                        }
                    });
                    SystemClock.sleep(5000);
                }
            }
        }).start();
    }

    private void initView() {
        getNews();

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        myViewPagerAdapter = new MyViewPagerAdapter(MainActivity.this, viewPager, myNewsList);
        viewPager.setAdapter(myViewPagerAdapter);

        btn_toContactPage = (Button) findViewById(R.id.btnContactPage);
        btn_toSettingPage = (Button) findViewById(R.id.btnSettingPage);
        btn_toProfile = (Button) findViewById(R.id.btnProfile);
        btn_toContactPage.setOnClickListener(this);
        btn_toSettingPage.setOnClickListener(this);
        btn_toProfile.setOnClickListener(this);
        builder = new AlertDialog.Builder(this);
        builder.setTitle("loading")
                .setView(R.layout.layout_progress_view)
                .create();

        if (PermissionsUtil.hasPermission(this, Manifest.permission.CALL_PHONE) &&

                PermissionsUtil.hasPermission(this, Manifest.permission.SEND_SMS) &&
                PermissionsUtil.hasPermission(this, Manifest.permission.READ_CONTACTS) &&
                PermissionsUtil.hasPermission(this, Manifest.permission.WRITE_CONTACTS) &&
                PermissionsUtil.hasPermission(this, Manifest.permission.CAMERA) &&
                PermissionsUtil.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionsUtil.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ) {
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {
                }
            }, new String[]{Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.SEND_SMS,


            });
        }
//
//        btnInput = (Button) findViewById(R.id.btnInput);
//        btnInput.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnProfile:
                startActivity(new Intent(this, AddProfileActivity.class));
                break;
            case R.id.btnContactPage:
                startActivity(new Intent(this, ContactPageActivity.class));
                break;
            case R.id.btnSettingPage:
                startActivity(new Intent(this, SettingPageActivity.class));
                break;

//
//            case R.id.btnInput:
////
//                if(!isThisImeOpen(packageName,inputMethodManager)){
//                    thread = new Thread(runnable);
//                    thread.start();
//                    }
//                Intent inputIntent = new Intent();
//                inputIntent.setAction("android.settings.INPUT_METHOD_SETTINGS");
////
////                int pendingIntentId = 123456;
////                PendingIntent pendingIntent = PendingIntent.getActivity(this, pendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
////                AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
////                manager.set(AlarmManager.RTC,System.currentTimeMillis() +3000,pendingIntent);
//
//                startActivity(inputIntent);
//
////                System.exit(0);

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    public void getNews() {

        new Thread(new Runnable() {//使用線程確資料能順利抓取
            @Override
            public void run() {
                try {

                    Document doc = Jsoup.connect("https://tw.yahoo.com/").get();//要連結的新聞網址

                    Element element = doc.getElementById("t1");
                    Elements titles = element.select("a[href]");//標題列
                    for (int i = 3; i < titles.size(); i++) {
                        String title = titles.get(i).select("a[href] > span").text();
                        String url = titles.get(i).select("a").attr("href");

                        ViewPagerData data = new ViewPagerData(MainActivity.this, title, url);
                        myNewsList.add(data);

                    }
//
//                    Message msg = new Message()
                } catch (IOException e) {
                    e.printStackTrace();
                    getNews();
                }

            }

        }) {
        }.start();
    }
    //判斷指定輸入法是否已開啟使用
    public  boolean isThisImeOpen(String packageName, InputMethodManager imm) {

        Iterator iterator = imm.getEnabledInputMethodList().iterator();
        String inputPackageName;
        InputMethodInfo inputMethodInfo;
        do {
            if (!iterator.hasNext()) {
                return false;
            }
            inputMethodInfo = (InputMethodInfo) iterator.next();
            inputPackageName = inputMethodInfo.getPackageName();
        } while (!packageName.equals(inputPackageName));
        return true;
    }


}

