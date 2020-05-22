package com.example.r30_a.recyclerviewpoc.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Data;
/**
 * Created by R30-A on 2019/2/12.
 */

public class UpdateHelper {

    //新增名字
    public static boolean insertName(ContentResolver resolver, long contactId, String name){
        ContentValues values = new ContentValues();
        values.put(Data.RAW_CONTACT_ID, contactId );
        values.put(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(CommonDataKinds.StructuredName.GIVEN_NAME, name);
        resolver.insert(Data.CONTENT_URI,values);
        return true;
    }

    //新增電話
    public static boolean insertPhoneNum(ContentResolver resolver, long contactId, String phoneNum) {

        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNum);
        resolver.insert(ContactsContract.Data.CONTENT_URI, values);

        return true;
    }
    //新增備註
    public static void insertNote(ContentResolver resolver,String note,long contactId){
        ContentValues values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.Note.NOTE, note);
        values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
        resolver.insert(ContactsContract.Data.CONTENT_URI, values);
    }
    //新增地址
    public static void insertAddress(ContentResolver resolver,String city,String street,long contactId){
        ContentValues values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.StructuredPostal.CITY, city);
        values.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET, street);
        values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
        resolver.insert(ContactsContract.Data.CONTENT_URI, values);
    }
    //新增大頭貼
    public static void insertAvatar(ContentResolver resolver, String raw_contact_id,byte[] img_avatar_bytes){
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, raw_contact_id);
        values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, img_avatar_bytes);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
        resolver.insert(ContactsContract.Data.CONTENT_URI, values);
    }


    //更新電話
    public static void updatePhone(ContentResolver resolver, String updatePhone, String raw_contact_id) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, updatePhone);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        resolver.update(ContactsContract.Data.CONTENT_URI, values,
                ContactsContract.Data.RAW_CONTACT_ID + " =?" + " AND " + ContactsContract.Data.MIMETYPE + " =?",
                new String[]{raw_contact_id, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE});
    }

    //更新名字
    public static void updateName(ContentResolver resolver, String updateName, String contact_id) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Contacts.DISPLAY_NAME, updateName);
        resolver.update(
                ContactsContract.RawContacts.CONTENT_URI,
                values, ContactsContract.Data.CONTACT_ID + " =?",
                new String[]{contact_id});

    }

    //更新備註
    public static void updateNote(ContentResolver resolver, String oldNote, String updateNote, String contact_id) {

        if (!updateNote.equals(oldNote)) {
            ContentValues values = new ContentValues();
            values.put(ContactsContract.CommonDataKinds.Note.NOTE, updateNote);
            values.put(ContactsContract.Data._ID, contact_id);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
            resolver.update(ContactsContract.Data.CONTENT_URI, values,
                    ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE + "'"
                    , new String[]{contact_id});
        }
    }

    //更新地址
    public static void updateAddress(ContentResolver resolver, String updateCity, String updateStreet, String contact_id, String raw_contact_id) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.StructuredPostal.CITY, updateCity);
        values.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET, updateStreet);
        values.put(ContactsContract.CommonDataKinds.Phone.CONTACT_ID, contact_id);
        values.put(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/postal-address_v2");

        resolver.update(ContactsContract.Data.CONTENT_URI, values,
                ContactsContract.Data.RAW_CONTACT_ID + " =? AND "
                        + ContactsContract.Data.MIMETYPE + "='vnd.android.cursor.item/postal-address_v2'",
                new String[]{raw_contact_id});
    }

    public static void updateEmail(ContentResolver resolver, String contact_id, String updateEmail_home, String type) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.Email.DATA, updateEmail_home);
        values.put(ContactsContract.Data._ID, contact_id);
        resolver.update(ContactsContract.Data.CONTENT_URI,
                values,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?" + " AND "
                        + ContactsContract.CommonDataKinds.Email.MIMETYPE + "=?" + " AND "
                        + ContactsContract.CommonDataKinds.Email.TYPE + "=?",
                new String[]{contact_id, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, type});

    }
}
