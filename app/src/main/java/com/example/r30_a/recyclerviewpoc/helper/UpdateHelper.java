package com.example.r30_a.recyclerviewpoc.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import static android.provider.ContactsContract.Data;
import static android.provider.ContactsContract.Data.*;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import static android.provider.ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import static android.provider.ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
import static android.provider.ContactsContract.CommonDataKinds.Phone.TYPE;
import static android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER;
import android.provider.ContactsContract.CommonDataKinds.Note;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.*;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
/**
 * Created by R30-A on 2019/2/12.
 */

public class UpdateHelper {

    //新增名字
    public static boolean insertName(ContentResolver resolver, long contactId, String name){
        ContentValues values = new ContentValues();
        values.put(RAW_CONTACT_ID, contactId );
        values.put(MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        values.put(GIVEN_NAME, name);
        resolver.insert(Data.CONTENT_URI,values);
        return true;
    }

    //新增電話
    public static boolean insertPhoneNum(ContentResolver resolver, long contactId, String phoneNum) {

        ContentValues values = new ContentValues();
        values.put(RAW_CONTACT_ID, contactId);
        values.put(MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        values.put(TYPE, TYPE_MOBILE);
        values.put(NUMBER, phoneNum);
        resolver.insert(Data.CONTENT_URI, values);

        return true;
    }
    //新增備註
    public static void insertNote(ContentResolver resolver,String note,long contactId){
        ContentValues values = new ContentValues();
        values.put(Note.NOTE, note);
        values.put(RAW_CONTACT_ID, contactId);
        values.put(MIMETYPE, Note.CONTENT_ITEM_TYPE);
        resolver.insert(Data.CONTENT_URI, values);
    }
    //新增地址
    public static void insertAddress(ContentResolver resolver,String city,String street,long contactId){
        ContentValues values = new ContentValues();
        values.put(CITY, city);
        values.put(STREET, street);
        values.put(RAW_CONTACT_ID, contactId);
        values.put(MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
        resolver.insert(Data.CONTENT_URI, values);
    }
    //新增大頭貼
    public static void insertAvatar(ContentResolver resolver, String raw_contact_id,byte[] img_avatar_bytes){
        ContentValues values = new ContentValues();
        values.put(RAW_CONTACT_ID, raw_contact_id);
        values.put(Photo.PHOTO, img_avatar_bytes);
        values.put(MIMETYPE, Photo.CONTENT_ITEM_TYPE);
        resolver.insert(Data.CONTENT_URI, values);
    }


    //更新電話
    public static void updatePhone(ContentResolver resolver, String updatePhone, String raw_contact_id) {
        ContentValues values = new ContentValues();
        values.put(NUMBER, updatePhone);
        values.put(TYPE, TYPE_MOBILE);
        resolver.update(Data.CONTENT_URI, values,
                RAW_CONTACT_ID + " =?" + " AND " + MIMETYPE + " =?",
                new String[]{raw_contact_id, Phone.CONTENT_ITEM_TYPE});
    }

    //更新名字
    public static void updateName(ContentResolver resolver, String updateName, String contact_id) {
        ContentValues values = new ContentValues();
        values.put(Contacts.DISPLAY_NAME, updateName);
        resolver.update(RawContacts.CONTENT_URI, values, CONTACT_ID + " =?", new String[]{contact_id});

    }

    //更新備註
    public static void updateNote(ContentResolver resolver, String oldNote, String updateNote, String contact_id) {

        if (!updateNote.equals(oldNote)) {
            ContentValues values = new ContentValues();
            values.put(Note.NOTE, updateNote);
            values.put(_ID, contact_id);
            values.put(MIMETYPE, Note.CONTENT_ITEM_TYPE);
            resolver.update(Data.CONTENT_URI, values,
                    CONTACT_ID + "=?" + " AND " + MIMETYPE + "='" + Note.CONTENT_ITEM_TYPE + "'"
                    , new String[]{contact_id});
        }
    }

    //更新地址
    public static void updateAddress(ContentResolver resolver, String updateCity, String updateStreet, String contact_id, String raw_contact_id) {
        ContentValues values = new ContentValues();
        values.put(CITY, updateCity);
        values.put(STREET, updateStreet);
        values.put(CONTACT_ID, contact_id);
        values.put(MIMETYPE, "vnd.android.cursor.item/postal-address_v2");

        resolver.update(Data.CONTENT_URI, values,
                RAW_CONTACT_ID + " =? AND "
                        + MIMETYPE + "='vnd.android.cursor.item/postal-address_v2'",
                new String[]{raw_contact_id});
    }

    public static void updateEmail(ContentResolver resolver, String contact_id, String updateEmail_home, String type) {
        ContentValues values = new ContentValues();
        values.put(Email.DATA, updateEmail_home);
        values.put(_ID, contact_id);
        resolver.update(Data.CONTENT_URI,
                values,
                Email.CONTACT_ID + "=?" + " AND "
                        + Email.MIMETYPE + "=?" + " AND "
                        + Email.TYPE + "=?",
                new String[]{contact_id, Email.CONTENT_ITEM_TYPE, type});

    }
}
