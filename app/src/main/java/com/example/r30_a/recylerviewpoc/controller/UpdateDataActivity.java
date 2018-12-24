package com.example.r30_a.recylerviewpoc.controller;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;
import com.example.r30_a.recylerviewpoc.util.PermissionUtil;
import com.github.dfqin.grantor.PermissionsUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;

public class UpdateDataActivity extends AppCompatActivity implements View.OnClickListener{

    TextView txvDataName, txvDataPhone;
    Button btnUpdate;
    EditText edtName, edtPhone;
    Toast toast;
    String dataId;
    ImageView img_avatar;
    FrameLayout pickUserPhoto;
    Bitmap old_avatar;
    byte[] bytes;
    ContentResolver resolver;
    ContentValues values;
    Bitmap update_avatar;
    File temp_file;

    //取得結果用的Request Code
    private final int CAMERA_REQUEST = 1;
    private final int ALBUM_REQUEST = 2;
    private final int CROP_REQUEST = 3;

    Uri album_uri,camera_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);
        initView();

    }

    private void initView() {

        toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        txvDataName = (TextView)findViewById(R.id.txvDataName);
        txvDataPhone = (TextView)findViewById(R.id.txvDataPhone);
        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        Intent intent = getIntent();
        txvDataName.setText(intent.getStringExtra("name"));
        txvDataPhone.setText(intent.getStringExtra("phone"));
        dataId = intent.getStringExtra("id");

        edtName = (EditText)findViewById(R.id.edtContactName);
        edtPhone = (EditText)findViewById(R.id.edtPhoneNumber);
        edtName.setText(intent.getStringExtra("name"));
        edtPhone.setText(intent.getStringExtra("phone"));

        img_avatar = (ImageView)findViewById(R.id.userPhoto);
        img_avatar.setOnClickListener(this);

        byte[] avatar_byte = intent.getByteArrayExtra("avatar" );
        if(avatar_byte != null && avatar_byte.length  != 0 ){
            old_avatar = BitmapFactory.decodeByteArray(avatar_byte,0,avatar_byte.length);
            img_avatar.setImageBitmap(old_avatar);
        }
        pickUserPhoto = (FrameLayout)findViewById(R.id.pickUserPhoto);
        pickUserPhoto.setOnClickListener(this);

        resolver = this.getContentResolver();
        temp_file = new File("/sdcard/a.jpg");

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnUpdate) {

            final String updateName = edtName.getText().toString();
            final String updatePhone = edtPhone.getText().toString();

            if (updateName.equals(txvDataName.getText()) && updatePhone.equals(txvDataPhone.getText())
                    && old_avatar == update_avatar) {
                toast.setText(R.string.noUpdate);
                toast.show();
            } else if (TextUtils.isEmpty(updateName) || TextUtils.isEmpty(updatePhone)) {
                toast.setText(R.string.wrongInput);
                toast.show();
            } else {
                if (CommonUtil.isCellPhoneNumber(updatePhone)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.hint)
                            .setMessage(R.string.sureToUpdate)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                try {

                                    Intent intent = new Intent();
                                    intent.putExtra("id", dataId);
                                    intent.putExtra("Name", updateName);
                                    intent.putExtra("Phone", updatePhone);
                                    intent.putExtra("oldName", txvDataName.getText());
                                    setResult(RESULT_OK, intent);
                                    intent.setClass(UpdateDataActivity.this, ContactsPageActivity.class);
                                    toast.setText(R.string.updateDataOK);
                                    toast.show();
                                    finish();

                                }catch (Exception e){
                                    e.getMessage();
                                }


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
        PopupMenu popupMenu = new PopupMenu(this,v);
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

        //先確認權限
        String[] permissionCamera = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,};
                boolean s = !PermissionUtil.needGrantRuntimePermission(UpdateDataActivity.this,permissionCamera,PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE);
        if(PermissionUtil.needGrantRuntimePermission(UpdateDataActivity.this,permissionCamera,PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE)) {
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
                setResult(RESULT_OK,intent);
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
                setResult(RESULT_OK,intent);
                //intent.setDataAndType(uri, MediaStore.Images.Media.MIME_TYPE);
                startActivityForResult(intent,CAMERA_REQUEST);

            }
        }
    }

    private void albumStart() {
        String[] permissionAlbum = {//先檢查權限
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,};
        if(!PermissionsUtil.hasPermission(this,permissionAlbum)) {

            Intent albumIntent = new Intent();
            albumIntent.setType("image/*");//設定只顯示圖片區，不要秀其它的資料夾
            albumIntent.setAction(Intent.ACTION_GET_CONTENT);//取得本機相簿的action
            startActivityForResult(albumIntent, ALBUM_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    setChangedAvatar(temp_file,img_avatar,resolver);
                    img_avatar.setImageDrawable(Drawable.createFromPath(temp_file.getAbsolutePath()));

                    Cursor cursor  = resolver.query(Phone.CONTENT_URI,null, Phone.CONTACT_ID+" = " + dataId,null,null);
                    if(cursor != null && cursor.moveToNext()){

                        Long photo_ID = cursor.getLong(cursor.getColumnIndex(Phone.PHOTO_ID));
                        cursor.close();

                        cursor = resolver.query(Data.CONTENT_URI, new String[]{Data.RAW_CONTACT_ID},
                                Contacts.DISPLAY_NAME + " =?", new String[]{ txvDataName.getText().toString() },null);
                        if(cursor.moveToFirst()){
                            String raw_contact_id = cursor.getString(cursor.getColumnIndex(Data.RAW_CONTACT_ID));

                            if(photo_ID > 0){//已有設定大頭貼時

                                values = new ContentValues();
                                values.put(Photo.PHOTO,bytes);
                                resolver.update(Data.CONTENT_URI,values, Data.RAW_CONTACT_ID+ "=? AND "
                                        + Data.MIMETYPE+ "=?", new String[]{raw_contact_id, Photo.CONTENT_ITEM_TYPE});
                            }else {//尚未有大頭貼時
                                values = new ContentValues();
                                values.put(Data.RAW_CONTACT_ID,raw_contact_id);
                                values.put(Photo.PHOTO,bytes);
                                values.put(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE);
                                resolver.insert(Data.CONTENT_URI,values);
                            }
                        }
                    }

                }catch (Exception e){
                    e.getMessage();
                }
            }
        }
    }


private void setChangedAvatar(File file,ImageView img_avatar, ContentResolver resolver) {
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


    //7: 取得圖片旋轉的角度
    public static int getBitmapDegree(String path){
        int degree = 0;

        try {
            //使用exif類取得或設定圖片的細部參數，此處只處理旋轉角度
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);

            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    //8: 使用指定的角度旋轉圖片
    public static Bitmap rotateBitmap(Bitmap bitmap, int degree){
        Bitmap returnbitmap = null;

        //根據角度生成旋轉矩陣，並設定取得的需旋轉角度
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        //創建一個有角度的圖，寬高與傳入的圖一樣
        try{
            returnbitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        }catch (OutOfMemoryError e){}

        if(returnbitmap != null){
            returnbitmap = bitmap;
        }
        if(bitmap != returnbitmap){
            bitmap.recycle();
        }
        return  returnbitmap;

    }
}
