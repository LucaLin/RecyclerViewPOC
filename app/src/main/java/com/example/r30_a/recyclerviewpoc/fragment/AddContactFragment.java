package com.example.r30_a.recyclerviewpoc.fragment;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
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
import android.widget.Toast;

import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.controller.CropImageActivity;
import com.example.r30_a.recyclerviewpoc.helper.MyContactDBHelper;
import com.example.r30_a.recyclerviewpoc.helper.UpdateHelper;
import com.example.r30_a.recyclerviewpoc.util.BitmapUtil;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.example.r30_a.recyclerviewpoc.util.Util.getCameraIntentUnder23;
import static com.example.r30_a.recyclerviewpoc.util.Util.isCellPhoneNumber;
import static com.facebook.FacebookSdk.getApplicationContext;

public class AddContactFragment extends Fragment {

    Toast toast;
    EditText edtName, edtPhomeNumber, edtNote, edtCity, edtStreet;//使用者編輯區
    Button btnAddContact;
    ContentResolver resolver;
    Context context;

    //取得結果用的Request Code
    private final int CAMERA_REQUEST = 1;
    private final int ALBUM_REQUEST = 2;
    private final int CROP_REQUEST = 3;

    Uri album_uri, camera_uri;
    byte[] img_avatar_bytes;
    String img_avatar_base64;
    File temp_file;
    ImageView img_avatar;
    FrameLayout pickUserPhoto;
    Bitmap update_avatar = null;
    ContentValues values;
    MyContactDBHelper myContactDBHelper;
    SharedPreferences sp;
    File file;

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
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        resolver = context.getContentResolver();
        temp_file = new File("/sdcard/a.jpg");
        myContactDBHelper = MyContactDBHelper.getInstance(context);
        sp = context.getSharedPreferences("favorTags", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_contact, container, false);

        edtName = (EditText) v.findViewById(R.id.edtContactName);
        edtPhomeNumber = (EditText) v.findViewById(R.id.edtPhoneNumber);
        edtNote = (EditText) v.findViewById(R.id.edtNote);
        edtCity = (EditText) v.findViewById(R.id.edtCity);
        edtStreet = (EditText) v.findViewById(R.id.edtStreet);
        btnAddContact = (Button) v.findViewById(R.id.btnUpdate);
        img_avatar = (ImageView) v.findViewById(R.id.userPhoto);
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
                String note = edtNote.getText().toString();
                String city = edtCity.getText().toString();
                String street = edtStreet.getText().toString();
                if (TextUtils.isEmpty(name) || !isCellPhoneNumber(phoneNum)) {
                    toast.setText(R.string.wrongInput);
                    toast.show();


                } else {
                    insertContact(name, phoneNum, note, city, street);
                    toast.setText(R.string.addSuccess);
                    toast.show();
                    Fragment fragment = new ContactPageFragment();
                    android.support.v4.app.FragmentManager manager = getFragmentManager();
                    android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.frameLayout, fragment);
                    transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
                    transaction.commit();

                }
            }
        });

        return v;
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

//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//使用拍照
//            //拍完的照片做成暫存檔
//            String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Test";//取得目標folder
//            File folder = new File(folderPath);
//            //如果裝置沒有此folder，建立一個新的
//            if (!folder.exists()) {
//                if (!folder.mkdir()) {
//                }
//            }
//            //組合成輸出路徑
//            String filePath = folderPath + File.separator + "temp.png";
//            file = new File(filePath);
//            camera_uri = Uri.fromFile(file);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri);//將拍照的檔案放入暫存檔路徑
                startActivityForResult(getCameraIntentUnder23(camera_uri), CAMERA_REQUEST);

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
                                try {
                                    img_avatar.setImageBitmap(realBitmap);
                                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                    realBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                    img_avatar_bytes = outputStream.toByteArray();
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //Toast.makeText(this,R.string.updateOK,Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
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
    private Intent getCropImageIntent(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", 1);// 这兩項為裁剪框的比例.
        intent.putExtra("aspectY", 1);// x:y=1:1
        intent.putExtra("outputX", 200);//回傳照片比例X
        intent.putExtra("outputY", 200);//回傳照片比例Y
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(temp_file));
        intent.putExtra("outputFormat", "JPEG");

        return intent;

    }

    private void setChangedAvatar(File file, ImageView img_avatar) {
        try {

            update_avatar = BitmapFactory.decodeFile(file.getAbsolutePath());
            //bitmap to byte[]
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            update_avatar.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            img_avatar_bytes = outputStream.toByteArray();
            outputStream.close();
            img_avatar.setImageBitmap(update_avatar);


        } catch (Exception e) {
            e.getMessage();
        }
    }

    public boolean insertContact(String name, String phoneNum, String note, String city, String street) {

        try {
            ContentValues values = new ContentValues();

            //建立一個空白ID供新增資料用
            Uri contactUri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
            long contactId = ContentUris.parseId(contactUri);

            //新增Name
            UpdateHelper.insertName(resolver, contactId, name);

            //新增PhoneNum
            UpdateHelper.insertPhoneNum(resolver, contactId, phoneNum);

            //新增備註
            UpdateHelper.insertNote(resolver, note, contactId);

            //新增地址
            if (!TextUtils.isEmpty(city) || !TextUtils.isEmpty(street)) {
                UpdateHelper.insertAddress(resolver, city, street, contactId);

            }


            if (img_avatar_bytes != null && img_avatar_bytes.length > 0) {
                img_avatar_base64 = Base64.encodeToString(img_avatar_bytes, Base64.DEFAULT);
                Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                        ContactsContract.Contacts.DISPLAY_NAME + " =?", new String[]{name}, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String raw_contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));

                    //尚未有大頭貼時
                    UpdateHelper.insertAvatar(resolver, raw_contact_id, img_avatar_bytes);

                }
            }

            //----------加入DB----------//
            values = new ContentValues();
            values.put(MyContactDBHelper.CONTACT_ID, contactId);
            values.put(MyContactDBHelper.NAME, name);
            values.put(MyContactDBHelper.PHONE_NUMBER, phoneNum);
            values.put(MyContactDBHelper.NOTE, note);
            values.put(MyContactDBHelper.CITY, city);
            values.put(MyContactDBHelper.STREET, street);
            if (img_avatar_base64 != null && img_avatar_base64.length() > 0) {
                values.put(MyContactDBHelper.IMG_AVATAR, img_avatar_base64);
            }
            values.put(MyContactDBHelper.NUMBER, (sp.getInt("listSize", 0)) + 1);

            myContactDBHelper.getWritableDatabase().insert(MyContactDBHelper.TABLE_NAME, null, values);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1");
            builder.setSmallIcon(R.drawable.icons8_sms_30)
                    .setContentText(context.getResources().getString(R.string.addSuccessnotify))
                    .setContentTitle(context.getResources().getString(R.string.hint));
            Notification notification = builder.build();
            manager.notify(1, notification);

        } catch (Exception e) {
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
                           Uri uri, ContentValues values) {

        values.clear();
        values.put(rawContactIdColumn, contactId);
        values.put(MIMETYPE_column, Content_Item_Type);
        values.put(phoneType, TypeMode);
        values.put(dataColumn, data);
        resolver.insert(uri, values);

    }

    public boolean insertNameData(String rawContactIdColumn, long contactId,
                                  String MIMETYPE_column, String Content_Item_Type,
                                  String dataColumn, String data,
                                  Uri uri, ContentValues values) {

        values.clear();
        values.put(rawContactIdColumn, contactId);
        values.put(MIMETYPE_column, Content_Item_Type);
        values.put(dataColumn, data);
        resolver.insert(uri, values);

        return true;
    }


}
