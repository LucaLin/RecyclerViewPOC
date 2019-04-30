package com.example.r30_a.recyclerviewpoc.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;

import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.adapter.MyAdapter;

import com.example.r30_a.recyclerviewpoc.model.ContactData;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LUCA on 2018/12/12.
 */

public class CommonUtil {

    private static final String MY_TEST_PREF = "MY_TEST_PREF";
    private static final String FIRST_USE = "FIRST_USE";
    public static Uri ALL_CONTACTS_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    public static final Uri SIM_URI = Uri.parse("content://icc/adn");//讀取sim卡資料的uri string
    public static Set<String> favorIdSet = new HashSet();;

    public static String[] phoneNumberProjection = new String[]{//欲搜尋的欄位區塊
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Photo.PHOTO_ID,

    };

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


    public static String objectToString(Object object){

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
    public static void setContactList(Context context,RecyclerView recyclerView, MyAdapter adapter, ArrayList<ContactData> list,LinearLayoutManager manager) {
        adapter = new MyAdapter(context,list);
        recyclerView.setLayoutManager(manager);//設定排版樣式
        recyclerView.setAdapter(adapter);

    }

    //設定抽屜layout
    public static void setDrawer(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int headerViewResId,String userName, NavigationView navigationView) {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        View headerView = activity.getLayoutInflater().inflate(headerViewResId, navigationView, false);


    }

    //設定側滑小菜單內容
    public static SwipeMenuItem setMenuItem(Context context, int width, int height,int textId, int textSize, int color) {
        SwipeMenuItem item = new SwipeMenuItem(context);

        item.setWidth(width)
                .setHeight(height)
//                .setImage(iconId)
                .setText(textId)
                .setTextSize(textSize)
                .setBackgroundColor(color);

        return item;
    }

    //取得聯絡人大頭照資料
    public static Bitmap get_Avatar(ContentResolver resolver, long contact_ID){
        Bitmap bitmap = null;

        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contact_ID);
        Uri phontUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = resolver.query(phontUri,new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO},null,null,null);

        if(cursor == null){
            return null;
        }
        try {
            if(cursor.moveToNext()){
                byte[] data = cursor.getBlob(0);
                if(data != null){
                    InputStream inputStream = new ByteArrayInputStream(data);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
            }
        }finally {
            cursor.close();
        }
        return bitmap;

    }

    //bitmap to base64
    public static String bitmapToBase64(Bitmap bitmap){
        if(bitmap == null){
            return "";
        }
        String img_avatar_base64 = "";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        img_avatar_base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        return img_avatar_base64;
    }

    public static Intent getCameraIntentUnder23(Uri camera_uri){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//使用拍照
        //拍完的照片做成暫存檔
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Test";//取得目標folder
        File folder = new File(folderPath);
        //如果裝置沒有此folder，建立一個新的
        if (!folder.exists()) {
            if (!folder.mkdir()) {
            }
        }
        //組合成輸出路徑
        String filePath = folderPath + File.separator + "temp.png";
        File file = new File(filePath);
        camera_uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri);//將拍照的檔案放入暫存檔路徑

        return intent;
    }


}
