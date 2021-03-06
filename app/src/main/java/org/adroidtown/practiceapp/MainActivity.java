package org.adroidtown.practiceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    FragmentManager fm;
    ListFragment listFragment;
    WritePostFragment postFragment;
    Toolbar toolbar;
    BroadcastReceiver receiver;
    Context mContext;
    IntentFilter intentfilter;
    PostImageFragment postImageFragment;
    static int REQUEST_PICTURE = 100;
    static int REQUEST_PHOTO_ALBUM = 200;
    static String SAMPLEIMG = "picture.png";
    static int REQUEST_DIALOG = 300;
    Intent intentResult;
    Boolean isFromAlbum;
    Bitmap image;
    String imagePath;
    Uri uriAlbum;
   // private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        mContext = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        postImageFragment = new PostImageFragment();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        listFragment = new ListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();

        /*

        postFragment.setOnBackBtnListener(new WritePostFragment.OnBackBtnListener() {
            @Override
            public void onClick() {
                listFragmentTransaction();
            }
        });

        postFragment.setOnPostCompleteListener(new WritePostFragment.OnPostCompleteListener() {
            @Override
            public void onClick() {
                listFragmentTransaction();
            }
        });

        */

        listFragment.setOnPostListener(new ListFragment.OnPostListener() {
            @Override
            public void onClick() {
                postFragmentTransaction();
            }
        });

        postImageFragment.setOnImageClickListener(new PostImageFragment.OnImageClickListener() { //이미지 선택 혹은 카메라 찍기,카메라는 임시저장 사용해야 함 , 이미지선택은 경로를 가져와서
            @Override
            public void onClick() {
                final CharSequence[] items = {"앨범에서 불러오기", "카메라로 촬영하기"};
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //앨범에서 불러오기
                            dialog.dismiss();
                            loadPicture();
                        } else {
                            //카메라로 촬영하기
                            dialog.dismiss();
                            takePicture();
                        }
                    }
                });
                builder.create().show();

            }
        });




        postImageFragment.setOnPostBtnListener(new PostImageFragment.OnPostBtnListener() { //다이얼로그 알림 처리를 MainActivity에게 부탁
            @Override
            public void onClick() {
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);

                String choice = "";
                Intent intent = new Intent("action1");
                if (isFromAlbum == true) {
                    intent.putExtra("path", uriAlbum);
                    intent.putExtra("editText", postImageFragment.editText.getText().toString());
                    startService(intent);
                    Log.d("Service123","startService() 앨범 ");
                } else {
                    intent.putExtra("image", imagePath);
                    intent.putExtra("editText", postImageFragment.editText.getText().toString());
                    startService(intent);
                    Log.d("Service123","startService() 촬영 ");
                }
                if (isFromAlbum == true){
                    choice = "앨범";
                } else {
                    choice = "촬영";
                }
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "이미지 유형");
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, choice);
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                    Log.d("fire","이벤트 발생 - 전송 성공");
                getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();

            }
        });


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        CustomDialog customDialog = new CustomDialog(mContext, intent);
        customDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //인텐트 초기화

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PHOTO_ALBUM) {
                postImageFragment.imageView.setImageURI(data.getData());
              uriAlbum = data.getData();
                isFromAlbum = true;
            } else if (requestCode == REQUEST_PICTURE) {
                postImageFragment.imageView.setImageBitmap(loadPictureToImageView());
//                intentResult.putExtra("image", intentResult.getExtras().getParcelable(MediaStore.EXTRA_OUTPUT));
//
//                Log.d("kk9991", "절대경로 : onActicityResult " + imagePath);
//                data.putExtra("image", imagePath);
                isFromAlbum = false;

            }
        }
    }

    private void postFragmentTransaction() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, postImageFragment).commit();
    }

    private void listFragmentTransaction() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();
    }

    private void loadPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, REQUEST_PHOTO_ALBUM);
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), SAMPLEIMG);
        Uri path = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, path);
        //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, REQUEST_PICTURE);
    }

    private Bitmap loadPictureToImageView() {
        File file = new File(Environment.getExternalStorageDirectory(), SAMPLEIMG);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        image = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        Log.d("kk9991", "절대경로 : " + file.getAbsolutePath());
        imagePath = file.getAbsolutePath();
        return image;
    }



    @Override
    protected void onStart() {
        super.onStart();
        this.receiver = new Receiver();
        IntentFilter item =  new IntentFilter("action1");
        registerReceiver(this.receiver, item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(this.receiver);
    }
}

