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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    FirebaseListFragment firebaseListFragment;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    BroadcastReceiver receiver;
    Context mContext;
    PostImageFragment postImageFragment;
    static int REQUEST_PICTURE = 100;
    static int REQUEST_PHOTO_ALBUM = 200;
    static String SAMPLEIMG = "picture.png";
    Boolean isFromAlbum;

    Bitmap image;
    String imagePath;
    Uri uriAlbum;
    DatabaseReference mDatabase;
    String key;
    int index;

       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        ButterKnife.bind(this);

           mContext = this;

        if(!FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://practiceapp-ce6dc.firebaseio.com/");

        setSupportActionBar(toolbar);

        firebaseListFragment = new FirebaseListFragment();
        postImageFragment = new PostImageFragment();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
//        listFragment = new ListFragment();
//        getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();

           mDatabase.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot snapshot) {
                   Log.d("Service123","addValueEventListener onDataChange has DataSnapshot : " + snapshot);

               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });
        mDatabase.child("maxkey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
              //  System.out.println(snapshot.getValue());
                Log.d("Service123","onDataChange has DataSnapshot : " + snapshot);
                Object maxkey = snapshot.getValue();
                index = Integer.valueOf((String)maxkey);
//                index = (long)maxkey;

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }


        });

        getSupportFragmentManager().beginTransaction().replace(R.id.container, firebaseListFragment).commit();
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

        setupListener();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, firebaseListFragment).commit();

    }

    private void setupListener() {

        firebaseListFragment.setOnPostListener(new FirebaseListFragment.OnPostListener() {
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
                String content;
                String path;
                String choice = "";
                Intent intent = new Intent("action1");
                intent.setPackage("org.adroidtown.practiceapp");
                if (isFromAlbum == true) {
                    intent.putExtra("path", uriAlbum);
                    content = postImageFragment.editText.getText().toString();
                    intent.putExtra("editText", content);
                    startService(intent);
                    Log.d("Service123","startService() 앨범 ");
                    path = uriAlbum.toString();
                } else {
                    intent.putExtra("image", imagePath);
                    content = postImageFragment.editText.getText().toString();
                    intent.putExtra("editText", content);
                    startService(intent);
                    Log.d("Service123","startService() 촬영 ");
                    path = imagePath.toString();
                }

                writeNewPost(content, path);

                Log.d("Service123","writeNewPost");
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


                getSupportFragmentManager().beginTransaction().replace(R.id.container, firebaseListFragment).commit();

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
      //          postImageFragment.imageView.setImageURI(data.getData());
              uriAlbum = data.getData();
                isFromAlbum = true;
            } else if (requestCode == REQUEST_PICTURE) {
         //       postImageFragment.imageView.setImageBitmap(loadPictureToImageView());
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
        getSupportFragmentManager().beginTransaction().replace(R.id.container, firebaseListFragment).commit();
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
        File file = new File(Environment.getExternalStorageDirectory(), key+".png");
        Uri path = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, path);
        //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, REQUEST_PICTURE);
    }

    private Bitmap loadPictureToImageView() {
        File file = new File(Environment.getExternalStorageDirectory(),key+".png");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 16;
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

    private void writeNewPost(String content, String path) {
//        int updateIndex = index+1;
//        String key = String.valueOf(updateIndex);
        FirebaseItem firebaseItem = new FirebaseItem(content,path);
        mDatabase.child("post").push().setValue(firebaseItem);

//        Map<String, String> postValues = firebaseItem.toMap();
//        Map<String, Object> childUpdates = new HashMap<>();
//
//        childUpdates.put(key, postValues);
//        mDatabase.child("post").updateChildren(childUpdates);
//        key = mDatabase.child("post").push().getKey();
//        FirebaseItem firebaseItem = new FirebaseItem(content,path);
//        Map<String, String> postValues = firebaseItem.toMap();
//        Map<String, Object> childUpdates = new HashMap<>();
//        mDatabase.child("maxkey").setValue(key);
//        mDatabase.updateChildren(childUpdates);
    }
}

