package com.example.r30_a.recylerviewpoc.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.adapter.MyAdapter;
import com.example.r30_a.recylerviewpoc.model.ContactData;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LUCA on 2018/12/12.
 */

public class CommonUtil {

    private static final String MY_TEST_PREF = "MY_TEST_PREF";
    private static final String FIRST_USE = "FIRST_USE";
    public static boolean isDataChanged = false;

    /*簡單判斷字串是否為電話號碼格式*/
    public static boolean isCellPhoneNumber(String cellphone) {
        if (cellphone.length() < 10) {
            return false;
        } else {
            boolean isCellPhone;
            String sub = "";
            cellphone = cellphone.trim()
                    .replace("+", "")
                    .replace("-", "")
                    .replace("+886", "")
                    .replace("886", "0")
                    .replace(" ", "");
            if (cellphone.length() > 2) {
                sub = cellphone.substring(0, 2).trim();

                if (!sub.equals("09")) {
                    isCellPhone = false;
                } else {
                    Pattern pattern = Pattern.compile("[0-9]{4}[0-9]{3}[0-9]{3}");
                    Matcher matcher = pattern.matcher(cellphone);
                    isCellPhone = matcher.matches();
                }
            } else {
                isCellPhone = false;
            }
            return isCellPhone;
        }
    }
    /*取得格式化後的電話號碼*/
    public static String getFormatPhone(String phoneNumber) {
        //1: 開頭是+886的
        //2： 格式為xxxx-xxx-xxx的
        //3: 手機號碼在市話欄或傳真欄的
        //4: 根本沒有手機號碼的
        //5: 一人有多支號碼的
        //6: 不是09或+886就不取
        return phoneNumber.trim()
                .replace("+", "")
                .replace("-", "")
                .replace("+886", "")
                .replace("886", "0")
                .replace(" ", "");
    }


    public String objectToString(Object object){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;

        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String str_object = new String(Base64.encode(byteArrayOutputStream.toByteArray(),Base64.DEFAULT));
            objectOutputStream.close();
            return str_object;
        }catch (IOException e){
            e.getMessage();
            return null;
        }
    }

    //12: 檢查是否為第一次使用
    public static boolean isFirstTimeUse(Context context){
        SharedPreferences sf = context.getSharedPreferences(MY_TEST_PREF,Context.MODE_PRIVATE);
        return sf.getBoolean(FIRST_USE, true);
    }

    //13: 設定是否第一次使用
    public static void setFirstTimeUse(Context context, boolean value) {
        SharedPreferences sf = context.getSharedPreferences(MY_TEST_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putBoolean(FIRST_USE, value);
        editor.commit();

    }

    //更新通訊錄清單的方法
    public static void setContactList(Context context,RecyclerView recyclerView, MyAdapter adapter, ArrayList<ContactData> list) {

        adapter = new MyAdapter(context,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));//設定排版樣式
        recyclerView.setAdapter(adapter);

    }
    //設定抽屜layout
    public static void setDrawer(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int headerViewResId, NavigationView navigationView) {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        View headerView = activity.getLayoutInflater().inflate(headerViewResId, navigationView, false);

    }

    //設定側滑小菜單內容
    public static SwipeMenuItem setMenuItem(Context context, int width, int height, int text_resID, int textSize, int color) {
        SwipeMenuItem item = new SwipeMenuItem(context);

        item.setWidth(width)
                .setHeight(height)
                .setText(text_resID)
                .setTextSize(textSize)
                .setBackgroundColor(color);

        return item;
    }
}
