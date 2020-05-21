package com.example.r30_a.recyclerviewpoc.fragment;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.r30_a.recyclerviewpoc.R;

import com.example.r30_a.recyclerviewpoc.controller.CropImageActivity;
import com.example.r30_a.recyclerviewpoc.helper.MyContactDBHelper;
import com.example.r30_a.recyclerviewpoc.helper.UpdateHelper;
import com.example.r30_a.recyclerviewpoc.util.BitmapUtil;
import com.example.r30_a.recyclerviewpoc.util.Util;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class UpdateContactFragment extends Fragment implements View.OnClickListener {

    private static final String USER_OLD_NAME = "name";//名稱
    private static final String USER_OLD_PHONE = "phoneNumber";//手機號碼
    private static final String USER_AVATAR = "avatar";//大頭貼
    private static final String CONTACT_ID = "contact_id";//id
    private static final String NOTE = "note";//備註
    private static final String CITY = "city";//城市
    private static final String STREET = "street";//街道
    private static final String EMAIL_HOME = "email_home";
    private static final String EMAIL_COM = "email_com";
    private static final String EMAIL_OTHER = "email_other";
    private static final String EMAIL_CUSTOM = "email_custom";

    //取得結果用的Request Code
    private final int CAMERA_REQUEST = 1;
    private final int ALBUM_REQUEST = 2;
    private final int CROP_REQUEST = 3;

    Uri album_uri, camera_uri;

    String oldname,
            oldphoneNumber,
            contact_id,
            oldNote,
            oldCity,
            oldStreet,
            oldEmail_home,
            oldEmail_company,
            oldEmail_other,
            oldEmail_custom;

    byte[] img_avatar_bytes;
    byte[] bytes;

    Button btnUpdate;
    EditText edtName,
            edtPhone,
            edtNote,
            edtCity,
            edtStreet,
            edtEmail_home,
            edtEmail_company,
            edtEmail_other,
            edtEmail_custom;

    LinearLayout email_homeLayout, email_companyLayout, email_otherLayout, email_customLayout;

    Toast toast;
    ImageView img_avatar;
    FrameLayout pickUserPhoto;
    Bitmap old_avatar;
    Context context;
    ContentResolver resolver;
    ContentValues values;
    Bitmap update_avatar = null;
    File temp_file;
    File file;

    MyContactDBHelper myContactDBHelper;

    View v;

    public UpdateContactFragment() {
    }

    public static UpdateContactFragment newInstance(String contact_id, String name,
                                                    String phoneNumber, byte[] img_avatar_bytes,
                                                    String note, String city, String street,
                                                    String email_home, String email_company,
                                                    String email_other, String email_custom) {
        UpdateContactFragment fragment = new UpdateContactFragment();
        Bundle args = new Bundle();
        args.putString(CONTACT_ID, contact_id);
        args.putString(USER_OLD_NAME, name);
        args.putString(USER_OLD_PHONE, phoneNumber);
        args.putByteArray(USER_AVATAR, img_avatar_bytes);
        args.putString(NOTE, note);
        args.putString(CITY, city);
        args.putString(STREET, street);
        args.putString(EMAIL_HOME, email_home);
        args.putString(EMAIL_COM, email_company);
        args.putString(EMAIL_OTHER, email_other);
        args.putString(EMAIL_CUSTOM, email_custom);


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            context = getContext();
            resolver = context.getContentResolver();
            contact_id = getArguments().getString(CONTACT_ID);
            oldname = getArguments().getString(USER_OLD_NAME);
            oldphoneNumber = getArguments().getString(USER_OLD_PHONE);
            oldNote = getArguments().getString(NOTE);

            oldCity = getArguments().getString(CITY);
            oldStreet = getArguments().getString(STREET);
            oldEmail_home = getArguments().getString(EMAIL_HOME);
            oldEmail_company = getArguments().getString(EMAIL_COM);
            oldEmail_other = getArguments().getString(EMAIL_OTHER);
            oldEmail_custom = getArguments().getString(EMAIL_CUSTOM);

            img_avatar_bytes = getArguments().getByteArray(USER_AVATAR);
            temp_file = new File("/sdcard/a.jpg");
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
            myContactDBHelper = MyContactDBHelper.getInstance(context);

        }
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.btnUpdate) {

            final String updateName = edtName.getText().toString();
            final String updatePhone = edtPhone.getText().toString();
            final String updateNote = edtNote.getText().toString();
            final String updateCity = edtCity.getText().toString();
            final String updateStreet = edtStreet.getText().toString();
            final String updateEmail_home = edtEmail_home.getText().toString();
            final String updateEmail_company = edtEmail_company.getText().toString();
            final String updateEmail_other = edtEmail_other.getText().toString();
            final String updateEmail_custom = edtEmail_custom.getText().toString();

            //updateEmail = edtEmail.getText().toString();

//            if (updateName.equals(oldname) && updatePhone.equals(oldphoneNumber)
//                    && old_avatar == update_avatar) {
//                toast.setText(R.string.noUpdate);
//                toast.show();
//            } else
            if (TextUtils.isEmpty(updateName) || TextUtils.isEmpty(updatePhone)) {
                toast.setText(R.string.wrongInput);
                toast.show();
            } else {
                if (Util.isCellPhoneNumber(updatePhone)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.hint)
                            .setMessage(R.string.sureToUpdate)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Cursor c = resolver.query(Data.CONTENT_URI, new String[]{Data.RAW_CONTACT_ID},
                                            Phone.CONTACT_ID + "=?", new String[]{contact_id}, null);
                                    c.moveToFirst();
                                    String raw_contact_id = c.getString(c.getColumnIndex(Data.RAW_CONTACT_ID));
                                    c.close();
                                    try {
                                        //更新電話
                                        UpdateHelper.updatePhone(resolver, updatePhone, raw_contact_id);
                                        //更新名字
                                        UpdateHelper.updateName(resolver, updateName, contact_id);
                                        //更新備註
                                        UpdateHelper.updateNote(resolver, oldNote, updateNote, contact_id);

                                        //更新地址
                                        if (!TextUtils.isEmpty(updateCity) && !TextUtils.isEmpty(updateStreet)) {
                                            UpdateHelper.updateAddress(resolver, updateCity, updateStreet, contact_id, raw_contact_id);
                                        } else {
                                            toast.setText("地址不完整，本次尚未更新");
                                            toast.show();
                                        }
                                        //更新email
                                        UpdateHelper.updateEmail(resolver, contact_id, updateEmail_home, "1");
                                        UpdateHelper.updateEmail(resolver, contact_id, updateEmail_company, "2");
                                        UpdateHelper.updateEmail(resolver, contact_id, updateEmail_other, "3");
                                        UpdateHelper.updateEmail(resolver, contact_id, updateEmail_custom, "0");

                                    } catch (Exception e) {
                                        e.getMessage();
                                    }
                                    //更新資料庫

                                    updateDB(updateName, updatePhone, updateNote, updateCity, updateStreet,
                                            updateEmail_home, updateEmail_company, updateEmail_other, updateEmail_custom, bytes);

                                    toast.setText(R.string.updateDataOK);
                                    toast.show();

                                    changeToThisFrag(new ContactPageFragment());


                                }
                            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create();

                    builder.show();
                } else {
                    toast.setText(R.string.wrongInput);
                    toast.show();
                }
            }
        } else if ((v.getId() == R.id.userPhoto) || (v.getId() == R.id.pickUserPhoto)) {
            showPopupMenu(v);
        }

    }

    private void updateDB(String updateName, String updatePhone, String updateNote, String updateCity,
                          String updateStreet, String updateEmail_home, String updateEmail_company,
                          String updateEmail_other, String updateEmail_custom, byte[] img_avatar_bytes) {

        values = new ContentValues();
        values.put(MyContactDBHelper.NAME, updateName);
        values.put(MyContactDBHelper.PHONE_NUMBER, updatePhone);
        values.put(MyContactDBHelper.NOTE, updateNote);
        values.put(MyContactDBHelper.CITY, updateCity);
        values.put(MyContactDBHelper.STREET, updateStreet);
        values.put(MyContactDBHelper.EMAIL_DATA_HOME, updateEmail_home);
        values.put(MyContactDBHelper.EMAIL_DATA_COM, updateEmail_company);
        values.put(MyContactDBHelper.EMAIL_DATA_OTHER, updateEmail_other);
        values.put(MyContactDBHelper.EMAIL_DATA_CUSTOM, updateEmail_custom);
        if (img_avatar_bytes != null && img_avatar_bytes.length > 0) {
            String img_base64 = Base64.encodeToString(img_avatar_bytes, Base64.DEFAULT);
            values.put(MyContactDBHelper.IMG_AVATAR, img_base64);
        } else {//
        }
        try {

            myContactDBHelper.getWritableDatabase().update(MyContactDBHelper.TABLE_NAME, values,
                    MyContactDBHelper.CONTACT_ID + "=" + contact_id, null);
        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void changeToThisFrag(Fragment fragment) {

        android.support.v4.app.FragmentManager manager = getFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
        transaction.commit();
    }

    //設定資訊
    private void setOldinfo(String name, String phone, String note,
                            String email_home, String email_company, String email_other, String email_custom,
                            String city, String street) {

        edtName.setText(name);
        edtPhone.setText(phone);
        edtNote.setText(note);
        edtCity.setText(city);
        edtStreet.setText(street);

        setEmailText(email_homeLayout, email_home, edtEmail_home);
        setEmailText(email_companyLayout, email_company, edtEmail_company);
        setEmailText(email_otherLayout, email_other, edtEmail_other);
        setEmailText(email_customLayout, email_custom, edtEmail_custom);

    }

    private void setEmailText(LinearLayout layout, String email_data, EditText edt) {
        if (!TextUtils.isEmpty(email_data)) {
            edt.setText(email_data);
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.popupmenu_foravatar);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_camera:
                        cameraStart();
                        break;
                    case R.id.item_picture:
                        albumStart();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();

    }

    private void cameraStart() {
        if (PermissionsUtil.hasPermission(context, Manifest.permission.CAMERA)) {
            if (Build.VERSION.SDK_INT < 23) {

//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//使用拍照
//                //拍完的照片做成暫存檔
//                String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Test";//取得目標folder
//                File folder = new File(folderPath);
//                //如果裝置沒有此folder，建立一個新的
//                if (!folder.exists()) {
//                    if (!folder.mkdir()) {
//                    }
//                }
//                //組合成輸出路徑
//                String filePath = folderPath + File.separator + "temp.png";
//                file = new File(filePath);
//                camera_uri = Uri.fromFile(file);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri);//將拍照的檔案放入暫存檔路徑
                startActivityForResult(Util.getCameraIntentUnder23(camera_uri), CAMERA_REQUEST);

            } else {
                camera_uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.r30_a.recyclerviewpoc.fileprovider", temp_file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//使用拍照
                intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        } else {
            PermissionsUtil.requestPermission(getActivity(), new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {
                }
            }, new String[]{Manifest.permission.CAMERA});
        }
    }

    private void albumStart() {

        Intent albumIntent = new Intent();
        albumIntent.setType("image/*");//設定只顯示圖片區，不要秀其它的資料夾
        albumIntent.setAction(Intent.ACTION_GET_CONTENT);//取得本機相簿的action
        startActivityForResult(albumIntent, ALBUM_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == ALBUM_REQUEST || requestCode == CAMERA_REQUEST) {

                if (data != null && data.getData() != null) {
                    album_uri = data.getData();
                    doCropPhoto(album_uri, 90);
                } else {
                    int degree = 0;
                    //取file的絕對路徑
                    if (Build.VERSION.SDK_INT < 23) {
                        degree = BitmapUtil.getBitmapDegree(file.getAbsolutePath());
                    } else {
                        degree = BitmapUtil.getBitmapDegree(temp_file.getAbsolutePath());
                    }
                    doCropPhoto(camera_uri, degree);
                }
            } else if (requestCode == CROP_REQUEST) {

                try {
                    //設定縮圖大頭貼
                    if (data.hasExtra(CropImageActivity.EXTRA_IMAGE) && data != null) {
                        //取得裁切後圖片的暫存位置
                        String filePath = data.getStringExtra(CropImageActivity.EXTRA_IMAGE);
                        // !=-1代表有此路徑檔
                        if (filePath.indexOf(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM") != -1) {

                            File imgFile = new File(filePath);
                            if (imgFile.exists()) {
                                //代入已設定好的圖片size
                                int photoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
                                //使用寫好的方法將路徑檔做成bitmap檔
                                Bitmap realBitmap = BitmapUtil.decodeSampledBitmap(imgFile.getAbsolutePath(), photoSize, photoSize);

                                if (realBitmap != null) {
                                    img_avatar.setImageBitmap(realBitmap);
                                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                    realBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                    bytes = outputStream.toByteArray();
                                    outputStream.close();

                                }
                            }
                        }
                    }

                    Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contact_id, null, null);
                    if (cursor != null && cursor.moveToNext()) {

                        Long photo_ID = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID));
                        cursor.close();

                        cursor = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                                ContactsContract.Contacts.DISPLAY_NAME + " =?", new String[]{oldname}, null);
                        if (cursor.moveToFirst()) {
                            String raw_contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));

                            if (photo_ID > 0) {//已有設定大頭貼時

                                values = new ContentValues();
                                values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, bytes);
                                resolver.update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data.RAW_CONTACT_ID + "=? AND "
                                        + ContactsContract.Data.MIMETYPE + "=?", new String[]{raw_contact_id, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE});
                            } else {//尚未有大頭貼時
                                values = new ContentValues();
                                values.put(ContactsContract.Data.RAW_CONTACT_ID, raw_contact_id);
                                values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, bytes);
                                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
                                resolver.insert(ContactsContract.Data.CONTENT_URI, values);
                            }

                        }
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }
    }

    private void doCropPhoto(Uri uri, int degree) {
        Intent intent = new Intent(context, CropImageActivity.class);
        intent.setData(uri);
        intent.putExtra("degree", degree);
        startActivityForResult(intent, CROP_REQUEST);
    }

    //呼叫裁切圖片介面
//    private Intent getCropImageIntent(Uri uri) {
//
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("scale", true);
//        intent.putExtra("aspectX", 1);// 这兩項為裁剪框的比例.
//        intent.putExtra("aspectY", 1);// x:y=1:1
//        intent.putExtra("outputX", 200);//回傳照片比例X
//        intent.putExtra("outputY", 200);//回傳照片比例Y
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(temp_file));
//        intent.putExtra("outputFormat", "JPEG");
//
//        return intent;
//
//    }

//    private void setChangedAvatar(File file, ImageView img_avatar) {
//        try {
//
//            update_avatar = BitmapFactory.decodeFile(file.getAbsolutePath());
//            //bitmap to byte[]
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            update_avatar.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//            bytes = outputStream.toByteArray();
//            outputStream.close();
//            img_avatar.setImageBitmap(update_avatar);
//
//        } catch (Exception e) {
//            e.getMessage();
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_update_contact, container, false);

        btnUpdate = (Button) v.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);
        img_avatar = (ImageView) v.findViewById(R.id.userPhoto);
        img_avatar.setOnClickListener(this);
        pickUserPhoto = (FrameLayout) v.findViewById(R.id.pickUserPhoto);
        pickUserPhoto.setOnClickListener(this);

        edtName = (EditText) v.findViewById(R.id.edtDataName);
        edtPhone = (EditText) v.findViewById(R.id.edtDataPhone);
        edtNote = (EditText) v.findViewById(R.id.edtDataNote);
        edtCity = (EditText) v.findViewById(R.id.edtDataCity);
        edtStreet = (EditText) v.findViewById(R.id.edtDataStreet);

        edtEmail_home = (EditText) v.findViewById(R.id.edtEmail_home);
        edtEmail_company = (EditText) v.findViewById(R.id.edtEmail_company);
        edtEmail_other = (EditText) v.findViewById(R.id.edtEmail_other);
        edtEmail_custom = (EditText) v.findViewById(R.id.edtEmail_custom);

        email_homeLayout = (LinearLayout) v.findViewById(R.id.emailHomeLayout);
        email_companyLayout = (LinearLayout) v.findViewById(R.id.emailWorkLayout);
        email_otherLayout = (LinearLayout) v.findViewById(R.id.emailOtherLayout);
        email_customLayout = (LinearLayout) v.findViewById(R.id.emailCustomLayout);

        setOldinfo(oldname, oldphoneNumber, oldNote, oldEmail_home, oldEmail_company, oldEmail_other, oldEmail_custom, oldCity, oldStreet);

        if (img_avatar_bytes != null && img_avatar_bytes.length > 0) {
            old_avatar = BitmapFactory.decodeByteArray(img_avatar_bytes, 0, img_avatar_bytes.length);
            img_avatar.setImageBitmap(old_avatar);
            if (update_avatar != null) {
                img_avatar.setImageBitmap(update_avatar);
            }
        }

        return v;
    }
}
