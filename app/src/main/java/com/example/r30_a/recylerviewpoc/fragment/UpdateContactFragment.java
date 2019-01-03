package com.example.r30_a.recylerviewpoc.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;

import com.example.r30_a.recylerviewpoc.helper.MyContactDBHelper;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.app.Activity.RESULT_OK;

public class UpdateContactFragment extends Fragment implements View.OnClickListener{

    private static final String USER_OLD_NAME = "name";
    private static final String USER_OLD_PHONE = "phoneNumber";
    private static final String USER_AVATAR = "avatar";
    private static final String CONTACT_ID = "contact_id";

    //取得結果用的Request Code
    private final int CAMERA_REQUEST = 1;
    private final int ALBUM_REQUEST = 2;
    private final int CROP_REQUEST = 3;

    Uri album_uri,camera_uri;

    String oldname,oldphoneNumber,contact_id;
    byte[] img_avatar_bytes;
    byte[] bytes;

    TextView txvDataName, txvDataPhone;
    Button btnUpdate;
    EditText edtName, edtPhone,edtNote;
    String updateName;
    String updatePhone;
    Toast toast;
    String dataId;
    ImageView img_avatar;
    FrameLayout pickUserPhoto;
    Bitmap old_avatar;
    Context context;
    ContentResolver resolver;
    ContentValues values;
    Bitmap update_avatar=null;
    File temp_file;

    MyContactDBHelper myContactDBHelper;

    View v;
    public UpdateContactFragment() {}

    public static UpdateContactFragment newInstance(String contact_id,String name, String phoneNumber, byte[] img_avatar_bytes) {
        UpdateContactFragment fragment = new UpdateContactFragment();
        Bundle args = new Bundle();
        args.putString(CONTACT_ID,contact_id);
        args.putString(USER_OLD_NAME, name);
        args.putString(USER_OLD_PHONE, phoneNumber);
        args.putByteArray(USER_AVATAR,img_avatar_bytes);

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
            img_avatar_bytes = getArguments().getByteArray(USER_AVATAR);
            temp_file = new File("/sdcard/a.jpg");
            toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
            myContactDBHelper = MyContactDBHelper.getInstance(context);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_update_contact, container, false);

        txvDataName = (TextView)v.findViewById(R.id.txvDataName);
        txvDataPhone = (TextView)v.findViewById(R.id.txvDataPhone);
        btnUpdate = (Button)v.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);
        img_avatar = (ImageView)v.findViewById(R.id.userPhoto);
        img_avatar.setOnClickListener(this);
        pickUserPhoto = (FrameLayout)v.findViewById(R.id.pickUserPhoto);
        pickUserPhoto.setOnClickListener(this);

        edtName = (EditText)v.findViewById(R.id.edtContactName);
        edtPhone = (EditText)v.findViewById(R.id.edtPhoneNumber);
        edtName.setText(oldname);
        edtPhone.setText(oldphoneNumber);

        txvDataName.setText(oldname);
        txvDataPhone.setText(oldphoneNumber);
        if(img_avatar_bytes != null && img_avatar_bytes.length>0){
            old_avatar = BitmapFactory.decodeByteArray(img_avatar_bytes,0,img_avatar_bytes.length);
            img_avatar.setImageBitmap(old_avatar);
            if(update_avatar != null){
                img_avatar.setImageBitmap(update_avatar);
            }
        }

        return v;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnUpdate) {

            updateName = edtName.getText().toString();
            updatePhone = edtPhone.getText().toString();

            if (updateName.equals(txvDataName.getText()) && updatePhone.equals(txvDataPhone.getText())
                    && old_avatar == update_avatar) {
                toast.setText(R.string.noUpdate);
                toast.show();
            } else if (TextUtils.isEmpty(updateName) || TextUtils.isEmpty(updatePhone)) {
                toast.setText(R.string.wrongInput);
                toast.show();
            } else {
                if (CommonUtil.isCellPhoneNumber(updatePhone)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.hint)
                            .setMessage(R.string.sureToUpdate)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                        Cursor c = resolver.query(ContactsContract.Data.CONTENT_URI,
                                                new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                                                ContactsContract.Contacts.DISPLAY_NAME + " =?",
                                                new String[]{ oldname },null);

                                        c.moveToFirst();
                                        String raw_contact_id = c.getString(c.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
                                        c.close();
                                        try{
                                            //更新電話
                                            ContentValues values = new ContentValues();
                                            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER,updatePhone);
                                            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                                            resolver.update(ContactsContract.Data.CONTENT_URI,
                                                    values,
                                                    ContactsContract.Data.RAW_CONTACT_ID+" =?" +" AND "+ ContactsContract.Data.MIMETYPE + " =?" ,
                                                    new String[]{raw_contact_id, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE});
                                            //更新名字
                                            values = new ContentValues();
                                            values.put(ContactsContract.Contacts.DISPLAY_NAME,updateName);
                                            resolver.update(
                                                    ContactsContract.RawContacts.CONTENT_URI,
                                                    values, ContactsContract.Data.CONTACT_ID+" =?",
                                                    new String[]{contact_id});
                                        }catch (Exception e){
                                            e.getMessage();
                                        }

                                    values = new ContentValues();
                                    values.put(MyContactDBHelper.NAME,updateName);
                                    values.put(MyContactDBHelper.PHONE_NUMBER,updatePhone);
                                    if(bytes != null && bytes.length>0){
                                        String img_base64 = Base64.encodeToString(bytes,Base64.DEFAULT);
                                        values.put(MyContactDBHelper.IMG_AVATAR,img_base64);
                                    }else {

                                    }
                                    try {

                                        myContactDBHelper.getWritableDatabase().update(MyContactDBHelper.TABLE_NAME,values,
                                                MyContactDBHelper.CONTACT_ID+"="+contact_id,null);
                                    }catch (Exception e){
                                        e.getMessage();
                                    }
                                        CommonUtil.isDataChanged = true;
                                        toast.setText(R.string.updateDataOK);
                                        toast.show();


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
        }else if((v.getId() ==  R.id.userPhoto) || (v.getId() ==R.id.pickUserPhoto)){
            showPopupMenu(v);
        }

    }
    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(context,v);
        popupMenu.inflate(R.menu.popupmenu_foravatar);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
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

        //API < 23的版本使用原來的方法
        if(Build.VERSION.SDK_INT <= 23){

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//使用拍照
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getActivity().setResult(RESULT_OK,intent);
            startActivityForResult(intent, CAMERA_REQUEST);

        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri);
            getActivity().setResult(RESULT_OK,intent);
            startActivityForResult(intent,CAMERA_REQUEST);
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

        if(resultCode == RESULT_OK){

            if(requestCode == ALBUM_REQUEST || requestCode == CAMERA_REQUEST){

                if(data != null && data.getData() != null){
                    album_uri = data.getData();
                    doCropPhoto(album_uri);
                }else {
                    doCropPhoto(camera_uri);
                }
            }else if(requestCode == CROP_REQUEST){

                try{
                    //設定縮圖大頭貼
                    setChangedAvatar(temp_file,img_avatar);
                    img_avatar.setImageDrawable(Drawable.createFromPath(temp_file.getAbsolutePath()));

                    Cursor cursor  = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = " + contact_id,null,null);
                    if(cursor != null && cursor.moveToNext()){

                        Long photo_ID = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID));
                        cursor.close();

                        cursor = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                                ContactsContract.Contacts.DISPLAY_NAME + " =?", new String[]{ txvDataName.getText().toString() },null);
                        if(cursor.moveToFirst()){
                            String raw_contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));

                            if(photo_ID > 0){//已有設定大頭貼時

                                values = new ContentValues();
                                values.put(ContactsContract.CommonDataKinds.Photo.PHOTO,bytes);
                                resolver.update(ContactsContract.Data.CONTENT_URI,values, ContactsContract.Data.RAW_CONTACT_ID+ "=? AND "
                                        + ContactsContract.Data.MIMETYPE+ "=?", new String[]{raw_contact_id, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE});
                            }else {//尚未有大頭貼時
                                values = new ContentValues();
                                values.put(ContactsContract.Data.RAW_CONTACT_ID,raw_contact_id);
                                values.put(ContactsContract.CommonDataKinds.Photo.PHOTO,bytes);
                                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
                                resolver.insert(ContactsContract.Data.CONTENT_URI,values);
                            }
                            CommonUtil.isDataChanged = true;
                        }
                    }

                }catch (Exception e){
                    e.getMessage();
                }
            }
        }
    }

    private void doCropPhoto(Uri uri) {
        Intent intent = getCropImageIntent(uri);
        startActivityForResult(intent,CROP_REQUEST);
    }
    //呼叫裁切圖片介面
    private Intent getCropImageIntent(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop","true");
        intent.putExtra("scale",true);
        intent.putExtra("aspectX", 1);// 这兩項為裁剪框的比例.
        intent.putExtra("aspectY", 1);// x:y=1:1
        intent.putExtra("outputX", 200);//回傳照片比例X
        intent.putExtra("outputY", 200);//回傳照片比例Y
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(temp_file));
        intent.putExtra("outputFormat","JPEG");

        return intent;

    }
    private void   setChangedAvatar(File file,ImageView img_avatar) {
        try {

            update_avatar = BitmapFactory.decodeFile(file.getAbsolutePath());
            //bitmap to byte[]
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            update_avatar.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            bytes = outputStream.toByteArray();
            outputStream.close();
            img_avatar.setImageBitmap(update_avatar);

        }catch (Exception e){
            e.getMessage();
        }
    }
}
