package com.example.r30_a.recylerviewpoc.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.example.r30_a.recylerviewpoc.R;

import java.util.ArrayList;

/**
 * Created by R30-A on 2018/8/20.
 */

public class PermissionUtil {

    public static final int PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE = 2;




    //1：確認權限使用
    public static boolean needGrantRuntimePermission(Activity activity, String[] permissions, final int requestCode){


        ArrayList<String> permissionList = new ArrayList<>();

        //Android 為6.0以前的版本時，需顯示權限使用提醒
        if(Build.VERSION.SDK_INT < 23){
            ArrayList<String> permissionString = getPrermissionsMessages(activity, permissions);
            //判斷權限是否提醒過了
            if(permissionString.size() != 0){
                String msg = "";
                for(String str : permissionString){
                    if(TextUtils.isEmpty(msg)){
                        msg += activity.getString(R.string.dialog_permission_label_and);
                    }
                    msg += str;
                }
            }


        }else {
            //Android 6.0以上，顯示訊息要求使用者允許
            for(int i = 0; i< permissions.length; i++){
                if(ContextCompat.checkSelfPermission(activity,permissions[i]) != PackageManager.PERMISSION_GRANTED){
                    permissionList.add(permissions[i]);

                }
            }
        }
        //如果權限還沒取得，要求權限
        if(permissionList.size() > 0){
            ActivityCompat.requestPermissions(activity,permissionList.toArray(new String[permissionList.size()]),requestCode);
            return true;
        }else {
            return false;
        }


    }

    //2: 判斷權限是否已提醒過，若沒有提醒過則回傳所需的顯示
    private static ArrayList<String> getPrermissionsMessages(Activity activity, String[] permissions){
        ArrayList<String> permissionsStringsItems = new ArrayList<>();

        for(int i = 0; i< permissions.length; i++){
            if(permissions[i].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)){
                if(!getPermissionSetted(activity,ENUM_PERMISSION_TYPE.READ_EXTERNAL_STORAGE)){
                    permissionsStringsItems.add(activity.getString(R.string.dialog_permission_label_storage_message));
                    setPermissionSetted(activity,true,ENUM_PERMISSION_TYPE.READ_EXTERNAL_STORAGE);
                    setPermissionSetted(activity,true,ENUM_PERMISSION_TYPE.WRITE_EXTERNAL_STORAGE);
                }

            }else if(permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                if(!getPermissionSetted(activity,ENUM_PERMISSION_TYPE.WRITE_EXTERNAL_STORAGE)){
                    permissionsStringsItems.add(activity.getString(R.string.dialog_permission_label_storage_message));
                    setPermissionSetted(activity,true,ENUM_PERMISSION_TYPE.READ_EXTERNAL_STORAGE);
                    setPermissionSetted(activity,true,ENUM_PERMISSION_TYPE.WRITE_EXTERNAL_STORAGE);
                }

            }else if(permissions[i].equalsIgnoreCase(Manifest.permission.CAMERA)){
                if(!getPermissionSetted(activity,ENUM_PERMISSION_TYPE.CAMERA)){
                    setPermissionSetted(activity,true,ENUM_PERMISSION_TYPE.CAMERA);
                }
            }
        }
        return permissionsStringsItems;
    }

    //3: 確認本地是否已有設定權限，回傳true/false
    public static boolean getPermissionSetted(Context context, ENUM_PERMISSION_TYPE permission_type){
        SharedPreferences sf = context.getSharedPreferences("permission", Context.MODE_PRIVATE);

        switch (permission_type){
            case CAMERA:
                return sf.getBoolean("openCamera",false);
            case READ_EXTERNAL_STORAGE:
                return sf.getBoolean("readExternalStorage",false);
            case WRITE_EXTERNAL_STORAGE:
                return sf.getBoolean("WriteExternalStorage", false);
            case WRITE_SETTINGS:
                return sf.getBoolean("WriteSetting",false);

            default:return false;
        }

    }
    //4: 設定權限許可給指定的項目
    public static void setPermissionSetted(Context context, boolean value, ENUM_PERMISSION_TYPE permission_type){
        SharedPreferences sf = context.getSharedPreferences("permission", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sf.edit();

        switch (permission_type){
            case CAMERA:
                ed.putBoolean("openCamera",value);
            case READ_EXTERNAL_STORAGE:
                ed.putBoolean("readExternalStorage",value);
            case WRITE_EXTERNAL_STORAGE:
                ed.putBoolean("WriteExternalStorage", value);
            case WRITE_SETTINGS:
                ed.putBoolean("WriteSetting",value);

        }
        ed.commit();

    }



    public enum ENUM_PERMISSION_TYPE
    {
        CAMERA,
        READ_EXTERNAL_STORAGE,
        WRITE_EXTERNAL_STORAGE,
        WRITE_SETTINGS
    }
}