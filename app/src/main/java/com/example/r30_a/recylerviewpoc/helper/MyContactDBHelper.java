package com.example.r30_a.recylerviewpoc.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by R30-A on 2019/1/3.
 */

public class MyContactDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="CONTACT_DB";//資料庫名稱
    private static final int DB_VERSION=1;//資料庫版本
    public static final String TABLE_NAME="CONTACT_LIST";//資料表名稱
    public static final String id = "_id";
    public static final String CONTACT_ID = "ID";
    public static final String NUMBER = "NUMBER";
    public static final String NAME = "NAME";
    public static final String PHONE_NUMBER = "PHONENUMBER";
    public static final String IMG_AVATAR = "IMGAVATAR";
    public static final String FAVOR_TAG ="FAVOR_TAG";
    public static final String NOTE = "NOTE";
    public static final String ADDRESS = "ADDRESS";

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
            +ADDRESS+" TEXT ); ";

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
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
