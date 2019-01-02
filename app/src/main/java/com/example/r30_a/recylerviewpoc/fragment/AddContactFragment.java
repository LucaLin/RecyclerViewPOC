package com.example.r30_a.recylerviewpoc.fragment;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.model.ContactData;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.app.Activity.RESULT_OK;
import static com.example.r30_a.recylerviewpoc.util.CommonUtil.isCellPhoneNumber;


public class AddContactFragment extends Fragment {

    Toast toast;
    EditText edtName, edtPhomeNumber;//使用者編輯區
    Button btnAddContact;
    ContentResolver resolver;
    Context context;

    //取得結果用的Request Code
    private final int CAMERA_REQUEST = 1;
    private final int ALBUM_REQUEST = 2;
    private final int CROP_REQUEST = 3;

    Uri album_uri,camera_uri;
    byte[] img_avatar_bytes;
    File temp_file;
    ImageView img_avatar;
    FrameLayout pickUserPhoto;
    Bitmap update_avatar=null;
    ContentValues values;

    public AddContactFragment() {

    }


    public static AddContactFragment newInstance(String param1, String param2) {
        AddContactFragment fragment = new AddContactFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        toast = Toast.makeText(context, "",Toast.LENGTH_SHORT);
        resolver = context.getContentResolver();
        temp_file = new File("/sdcard/a.jpg");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_contact, container, false);

        edtName = (EditText)v.findViewById(R.id.edtContactName);
        edtPhomeNumber = (EditText)v.findViewById(R.id.edtPhoneNumber);
        btnAddContact = (Button)v.findViewById(R.id.btnUpdate);
        img_avatar = (ImageView)v.findViewById(R.id.userPhoto);
        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = edtName.getText().toString();
                String phoneNum = edtPhomeNumber.getText().toString();
                if(TextUtils.isEmpty(name) || !isCellPhoneNumber(phoneNum)){
                    toast.setText(R.string.wrongInput);
                    toast.show();
                }else {
                    insertContact(name, phoneNum);
                    toast.setText(R.string.addSuccess);
                    toast.show();

                }
            }
        });

        return v;
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

            //拍完的照片做成暫存檔
//                String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Test";//取得目標folder
//                File folder = new File(folderPath);
//                //如果裝置沒有此folder，建立一個新的
//                if (!folder.exists()) {
//                    if (!folder.mkdir()) {
//                    }
//                }
//                //組合成輸出路徑
//                String filePath = folderPath + File.separator + "temp.png";
//                camera_uri = Uri.fromFile(new File(filePath));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri);//將拍照的檔案放入暫存檔路徑
            getActivity().setResult(RESULT_OK,intent);
            startActivityForResult(intent, CAMERA_REQUEST);


        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//                String filePath = Environment.getExternalStorageDirectory()+ "/images/"+System.currentTimeMillis() + ".jpg";
//                File file = new File(getFilesDir() + "/images",System.currentTimeMillis() + ".jpg");
//                if(!file.getParentFile().exists()){
//                    file.getParentFile().mkdir();
//                }
//
//
//                camera_uri = FileProvider.getUriForFile(UpdateDataActivity.this,getPackageName()+".fireprovider" ,file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri);
            getActivity().setResult(RESULT_OK,intent);
            //intent.setDataAndType(uri, MediaStore.Images.Media.MIME_TYPE);
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
            img_avatar_bytes = outputStream.toByteArray();
            outputStream.close();
            img_avatar.setImageBitmap(update_avatar);

        }catch (Exception e){
            e.getMessage();
        }
    }

    public boolean insertContact(String name, String phoneNum) {

        try {
            ContentValues values = new ContentValues();

            //建立一個空白ID供新增資料用
            Uri contactUri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
            long contactId = ContentUris.parseId(contactUri);

            //新增Name
            insertNameData(ContactsContract.Data.RAW_CONTACT_ID, contactId, ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                    ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name, ContactsContract.Data.CONTENT_URI, values);


            //新增PhoneNum
            insertData(ContactsContract.Data.RAW_CONTACT_ID, contactId,
                    ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                    ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNum, ContactsContract.Data.CONTENT_URI, values);

            if(img_avatar_bytes != null && img_avatar_bytes.length>0){
                Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                        ContactsContract.Contacts.DISPLAY_NAME + " =?", new String[]{ name },null);
                if(cursor != null && cursor.moveToFirst()){
                    String raw_contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));

                    //尚未有大頭貼時
                    values = new ContentValues();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID,raw_contact_id);
                    values.put(ContactsContract.CommonDataKinds.Photo.PHOTO,img_avatar_bytes);
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
                    resolver.insert(ContactsContract.Data.CONTENT_URI,values);
                    }
            }
            CommonUtil.isDataChanged = true;
        }catch (Exception e){
            e.getMessage();
            return false;
        }
        return true;
    }

    //新增資料到聯絡人表格中
    public void insertData(String rawContactIdColumn, long contactId,
                           String MIMETYPE_column, String Content_Item_Type,
                           String phoneType, int TypeMode,
                           String dataColumn, String data,
                           Uri uri, ContentValues values){

        values.clear();
        values.put(rawContactIdColumn, contactId );
        values.put(MIMETYPE_column,Content_Item_Type);
        values.put(phoneType,TypeMode);
        values.put(dataColumn, data);
        resolver.insert(uri,values);

    }

    public boolean insertNameData(String rawContactIdColumn, long contactId,
                                  String MIMETYPE_column, String Content_Item_Type,
                                  String dataColumn, String data,
                                  Uri uri, ContentValues values){

        values.clear();
        values.put(rawContactIdColumn, contactId );
        values.put(MIMETYPE_column,Content_Item_Type);
        values.put(dataColumn, data);
        resolver.insert(uri,values);
        return true;
    }



}
