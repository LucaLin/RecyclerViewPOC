package com.example.r30_a.recyclerviewpoc.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by R30-A on 2019/1/3.
 */

public class MyContactDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="CONTACT_DB";//資料庫名稱
    private static final int DB_VERSION = 2;//資料庫版本
    public static final String TABLE_NAME="CONTACT_LIST";//資料表名稱
    public static final String id = "_id";
    public static final String CONTACT_ID = "ID";//聯絡人id編號
    public static final String NUMBER = "NUMBER";//排序號
    public static final String NAME = "NAME";//名字
    public static final String PHONE_NUMBER = "PHONENUMBER";//手機號碼
    public static final String IMG_AVATAR = "IMGAVATAR";//大頭照
    public static final String FAVOR_TAG ="FAVOR_TAG";//最愛清單
    public static final String NOTE = "NOTE";//備註
    //---------地址----------//
    public static final String CITY = "CITY";//城市
    public static final String STREET = "STREET";//街道名稱
    //--------EMAIL---------//

//    public static final String EMAIL_TYPE_HOME = "EMAIL_TYPE_HOME";
//    public static final String EMAIL_TYPE_COM = "EMAIL_TYPE_COM";
//    public static final String EMAIL_TYPE_OTHER = "EMAIL_TYPE_OTHER";
//    public static final String EMAIL_TYPE_CUSTOM = "EMAIL_TYPE_CUSTOM";
    public static final String EMAIL_DATA_HOME = "EMAIL_DATA_HOME";
    public static final String EMAIL_DATA_COM = "EMAIL_DATA_COM";
    public static final String EMAIL_DATA_OTHER = "EMAIL_DATA_OTHER";
    public static final String EMAIL_DATA_CUSTOM = "EMAIL_DATA_CUSTOM";

    //資料表參數
    private static final String createTable = "CREATE TABLE IF NOT EXISTS "
            +TABLE_NAME+" ( "
            +id+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CONTACT_ID+" TEXT, "
            +NAME+" TEXT, "
            +NUMBER+" TEXT, "
            +PHONE_NUMBER+" TEXT, "
            +IMG_AVATAR+" TEXT, "
            +FAVOR_TAG+" INTEGER, "
            +NOTE+" TEXT, "
            +CITY+" TEXT, "
            +STREET+" TEXT, "
            +EMAIL_DATA_HOME+" TEXT, "
            +EMAIL_DATA_COM+" TEXT, "
            +EMAIL_DATA_OTHER+" TEXT, "
            +EMAIL_DATA_CUSTOM+" TEXT); ";

    private static MyContactDBHelper instance;
    //取得實體
    public static MyContactDBHelper getInstance(Context context){
        if(instance==null){instance= new MyContactDBHelper(context,DB_NAME,null,DB_VERSION);};
        return instance;
    }




    private MyContactDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {db.execSQL(createTable);}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
