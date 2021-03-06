package com.example.r30_a.recyclerviewpoc.controller;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.util.BitmapUtil;
import com.example.r30_a.recyclerviewpoc.util.Util;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Toast toast;
    EditText edtName, edtPhomeNumber, edtNote, edtEmail_Custom, edtCity, edtStreet;//使用者編輯區
    Button btnAddContact;
    Button btnUpdate;
    ContentResolver resolver;
    File file;
    SharedPreferences sf;
    String filePath;

    //取得結果用的Request Code
    private final int CAMERA_REQUEST = 1;
    private final int ALBUM_REQUEST = 2;
    private final int CROP_REQUEST = 3;

    //設定大頭貼用
    Uri album_uri, camera_uri;
    byte[] img_avatar_bytes;
    String img_avatar_base64;
    File temp_file;
    ImageView img_avatar;
    Bitmap update_avatar = null;

    SharedPreferences sp;
    //FB功能
    LoginButton btnLoginFB;
    LoginManager loginManager;
    CallbackManager callbackManager;
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
        FacebookSdk.sdkInitialize(this);
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        sf = getSharedPreferences("profile", MODE_PRIVATE);

        initView();

    }

    @Override
    protected void onStop() {
        super.onStop();
        loginManager.logOut();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userPhoto:
                showPopupMenu(v);
                break;
            case R.id.btnFBLogin:
                loginFB();
                break;
            case R.id.btnUpdate:
                sf.edit().putString("name", (getEditStr(edtName))).commit();
                sf.edit().putString("phoneNum", (getEditStr(edtPhomeNumber))).commit();
                sf.edit().putString("email_custom", (getEditStr(edtEmail_Custom))).commit();
                sf.edit().putString("city", (getEditStr(edtCity))).commit();
                sf.edit().putString("street", (getEditStr(edtStreet))).commit();
                sf.edit().putString("note", (getEditStr(edtNote))).commit();

                if (img_avatar_base64 != null && img_avatar_base64.length() > 0) {
                    sf.edit().putString("avatar", img_avatar_base64).commit();
                }

                toast.setText("done");
                toast.show();
                finish();

        }
    }
    public String getEditStr(EditText editText){
        return editText.getText().toString();
    }

    //登入FB功能
    private void loginFB() {

        if (!isLogin) {
            loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);//預設fb login的顯示方式
            //可基本取得的權限，不需經過fb的審核
            List<String> permissions = new ArrayList<>();

            permissions.add("public_profile");
            permissions.add("email");

            loginManager.logInWithReadPermissions(this, permissions);
            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    //登入成功時, 透過GraphRequest來取得用戶的FB資訊
                    GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                if (response.getConnection().getResponseCode() == 200) {
                                    //long id = object.getLong("id");
                                    edtName.setText(object.getString("name"));
                                    edtEmail_Custom.setText(object.getString("email"));
                                    edtName.setEnabled(false);
                                    edtEmail_Custom.setEnabled(false);

                                    //String birth = object.getString("birthday");
                                    //取得用戶大頭照
                                    Profile profile = Profile.getCurrentProfile();
                                    //設定大頭照大小
                                    Uri userPhoto = profile.getProfilePictureUri(300, 300);
                                    Glide.with(AddProfileActivity.this)
                                            .load(userPhoto.toString())
                                            .fitCenter()
                                            .crossFade()
                                            .into(img_avatar);
                                    isLogin = true;

                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email");
                    graphRequest.setParameters(parameters);
                    graphRequest.executeAsync();

                }

                @Override
                public void onCancel() {
                    //取消登入
                }

                @Override
                public void onError(FacebookException error) {
                    //登入失敗時
                }
            });

        } else {
            loginManager.logOut();
        }

    }

    private void initView() {
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        resolver = getContentResolver();
        temp_file = new File("/sdcard/a.jpg");
        //myDBHelper = MyDBHelper.getInstance(this);
        sp = getSharedPreferences("favorTags", Context.MODE_PRIVATE);
        edtName = findViewById(R.id.edtContactName);
        edtPhomeNumber = findViewById(R.id.edtPhoneNumber);
        edtNote = findViewById(R.id.edtNote);
        edtCity = findViewById(R.id.edtCity);
        edtStreet = findViewById(R.id.edtStreet);
        edtEmail_Custom = findViewById(R.id.edtEmail_custom);
        btnAddContact = findViewById(R.id.btnUpdate);
        btnLoginFB = findViewById(R.id.btnFBLogin);

        btnLoginFB.setOnClickListener(this);
        img_avatar = findViewById(R.id.userPhoto);
        img_avatar.setOnClickListener(this);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        edtName.setText(sf.getString("name", ""));
        edtPhomeNumber.setText(sf.getString("phoneNum", ""));
        edtEmail_Custom.setText(sf.getString("email_custom", ""));
        edtCity.setText(sf.getString("city", ""));
        edtStreet.setText(sf.getString("street", ""));

        String img_avatarBase64 = (sf.getString("avatar", ""));
        if (!TextUtils.isEmpty(img_avatarBase64)) {
            byte[] bytes = Base64.decode(img_avatarBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            img_avatar.setImageBitmap(bitmap);
        }

    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popupmenu_foravatar);

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_camera:
                    cameraStart();
                    break;
                case R.id.item_picture:
                    albumStart();
                    break;
            }
            return true;
        });
        popupMenu.show();

    }


    private void albumStart() {

        Intent albumIntent = new Intent();
        albumIntent.setType("image/*");//設定只顯示圖片區，不要秀其它的資料夾
        albumIntent.setAction(Intent.ACTION_GET_CONTENT);//取得本機相簿的action
        startActivityForResult(albumIntent, ALBUM_REQUEST);

    }

    private void cameraStart() {
        if (PermissionsUtil.hasPermission(this, Manifest.permission.CAMERA)) {
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
//                filePath = folderPath + File.separator + "temp.png";
//                file = new File(filePath);
//                camera_uri = Uri.fromFile(file);
//
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri);//將拍照的檔案放入暫存檔路徑
                startActivityForResult(Util.getCameraIntentUnder23(camera_uri), CAMERA_REQUEST);

            } else {
                camera_uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.r30_a.recyclerviewpoc.fileprovider", temp_file);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//使用拍照
                intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {
                }
            }, new String[]{Manifest.permission.CAMERA});
        }
    }

    private void doCropPhoto(Uri uri, int degree) {
        Intent intent = new Intent(this, CropImageActivity.class);
        intent.setData(uri);
        intent.putExtra("degree", degree);
        startActivityForResult(intent, CROP_REQUEST);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == ALBUM_REQUEST || requestCode == CAMERA_REQUEST) {

                if (data != null && data.getData() != null) {
                    album_uri = data.getData();

                    doCropPhoto(album_uri, 0);

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
                                img_avatar.setImageBitmap(realBitmap);
                                byte[] avatar_bytes = BitmapUtil.Bitmap2Bytes(realBitmap);
                                img_avatar_base64 = Base64.encodeToString(avatar_bytes, Base64.DEFAULT);
                                //Toast.makeText(this,R.string.updateOK,Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        }
    }
}
