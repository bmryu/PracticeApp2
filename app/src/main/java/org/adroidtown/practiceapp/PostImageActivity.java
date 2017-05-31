package org.adroidtown.practiceapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Button;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bomeeryu_c on 2017. 5. 15..
 */

public class PostImageActivity extends BaseActivity {
    PostImageFragment postImageFragment;
    Context context;
    static int REQUEST_PICTURE = 100;
    static int REQUEST_PHOTO_ALBUM = 200;
    static String SAMPLEIMG = "picture.png";
    static int REQUEST_DIALOG = 300;
    @BindView(R.id.backButton)
    Button backButton;
    Intent intentResult;

    @Override
    public int getContentView() {
        return R.layout.activity_post_image;
    }

    @Override
    public void butterKnifeInject() {
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        postImageFragment = new PostImageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, postImageFragment).commit();
    }

    @Override
    public void setupListener() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //인텐트 초기화
        intentResult = data;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PHOTO_ALBUM) {
                postImageFragment.imageView.setImageURI(data.getData());

            } else if (requestCode == REQUEST_PICTURE) {
                postImageFragment.imageView.setImageBitmap(loadPictureToImageView());
            }
        }
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

        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }


}
