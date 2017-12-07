package com.syxrobot.syscrop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    private ImageView mIvShow;
    private Button mBtnPic;
    private Context mContext;
    private Switch mSwitch;

    private static final String IMAGE_PATH = "/storage/emulated/0/DCIM/temp.jpg";
    private Uri mImageUri;

    private static final int CODE_IMG = 1;
    private static final int CODE_CROP = 2;
    private static final int CODE_CROP_URI = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mBtnPic = findViewById(R.id.btn_pic);
        mIvShow = findViewById(R.id.iv_show);
        mSwitch = findViewById(R.id.sw_return_data);

        mBtnPic.setOnClickListener(v -> {
            Intent intent1 = new Intent(Intent.ACTION_PICK, null);
            intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent1, CODE_IMG);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODE_IMG:
                if (resultCode == RESULT_OK) {
                    //剪裁图片
                    if (mSwitch.isChecked()) {
                        cropPhoto(data.getData());
                    } else {
                        cropPhotoNoData(data.getData());
                    }

                }
                break;
            case CODE_CROP:
                if (data != null) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    Glide.with(mContext)
                            .load(bitmap)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(mIvShow);
                }
                break;
            case CODE_CROP_URI:
                if (data != null) {
                    if (mImageUri != null) {
                        Bitmap bitmap = decodeUriAsBitmap(mImageUri);
                        if (bitmap != null) {
                            Glide.with(mContext)
                                    .load(bitmap)
                                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                                    .into(mIvShow);
                        }
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 将Uri转换成bitmap
     *
     * @param mImageUri
     * @return
     */
    private Bitmap decodeUriAsBitmap(Uri mImageUri) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 剪裁后不直接返回图片
     *
     * @param data
     */
    private void cropPhotoNoData(Uri data) {
        mImageUri = Uri.fromFile(new File(IMAGE_PATH));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CODE_CROP_URI);
    }

    /**
     * 剪裁后直接返回图片
     *
     * @param data
     */
    private void cropPhoto(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        //是否直接返回图片数据 需要使用到内存 如果图片过大 可能会导致应用崩溃
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CODE_CROP);
    }
}
