package com.example.memoapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageAddPopup extends Activity {

    private Button popup_getImageFromGallery;
    private Button popup_getImageFromCamera;
    private Button popup_getImageFromURL;
    private Uri cameraImageUri;

    static final int GET_GALLERY_IMAGE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    static final int GET_IMAGE_URL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_add_popup);

        initLayout();

        popup_getImageFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 갤러리로 넘어가기
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        popup_getImageFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromCamera();
            }
        });

        popup_getImageFromURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageAddPopup.this, InputImageUrlPopup.class);
                startActivityForResult(intent, GET_IMAGE_URL);
            }
        });
    }

    private void initLayout() {
        popup_getImageFromGallery = (Button) findViewById(R.id.popup_getImageFromGallery);
        popup_getImageFromCamera = (Button) findViewById(R.id.popup_getImageFromCamera);
        popup_getImageFromURL = (Button) findViewById(R.id.popup_getImageFromURL);
    }

    private File getLocalImagePath(Intent data) {
        File imageFile = null;
        // 로컬 이미지 경로 얻기
        Uri selectedImageUri = data.getData();

        Cursor cursor = null;
        try {
            // Uri 스키마를 content:/// 에서 file:/// 로  변경한다.
            String[] proj = { MediaStore.Images.Media.DATA };

            assert selectedImageUri != null;
            cursor = getContentResolver().query(selectedImageUri, proj, null, null, null);

            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();

            imageFile = new File(cursor.getString(column_index));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return imageFile;
    }

    // 이미지 추가하기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK) {
            // 메모 추가 화면으로 값을 넘기면서 이동
            Intent intent = new Intent(ImageAddPopup.this, AddMemoActivity.class);
            String path = getLocalImagePath(data).getAbsolutePath();
            intent.putExtra("IMAGE_PATH", path);
            setResult(RESULT_OK, intent);
            finish();
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File imageFile = null;
            Uri selectedImageUri = cameraImageUri;
            Cursor cursor = null;
            try {
                // Uri 스키마를 content:/// 에서 file:/// 로  변경한다.
                String[] proj = { MediaStore.Images.Media.DATA };

                assert selectedImageUri != null;
                cursor = getContentResolver().query(selectedImageUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                imageFile = new File(cursor.getString(column_index));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            // 메모 추가 화면으로 값을 넘기면서 이동
            Intent intent = new Intent(ImageAddPopup.this, AddMemoActivity.class);
            String path = imageFile.getAbsolutePath();
            intent.putExtra("IMAGE_PATH", path);
            setResult(RESULT_OK, intent);
            finish();
        } else if (requestCode == GET_IMAGE_URL && resultCode == RESULT_OK) {
            String path = data.getExtras().getString("IMAGE_PATH");
            // 메모 추가 화면으로 값을 넘기면서 이동
            Intent intent = new Intent(ImageAddPopup.this, AddMemoActivity.class);
            intent.putExtra("IMAGE_PATH", path);
            setResult(RESULT_OK, intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 카메라에서 이미지 가져오기
    private void getImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                Toast.makeText(this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                // 고화질 이미지 전송
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                cameraImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MEMO_" + timeStamp + "_";

        File storageDir = new File(Environment.getExternalStorageDirectory() + "/memo/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }
}
