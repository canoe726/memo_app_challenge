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
    static final int GET_GALLERY_IMAGE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    static final int GET_IMAGE_URL = 3;

    private Button popup_getImageFromGallery;
    private Button popup_getImageFromCamera;
    private Button popup_getImageFromURL;
    private Uri cameraImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_add_popup);

        initLayout();

        popup_getImageFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);         // 갤러리로 넘어가기
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
        Uri selectedImageUri = data.getData();                              // 로컬 이미지 경로 얻기

        Cursor cursor = null;
        try {
            String[] project = { MediaStore.Images.Media.DATA };               // Uri 스키마를 content:/// 에서 file:/// 로  변경한다.

            assert selectedImageUri != null;
            cursor = getContentResolver().query(selectedImageUri, project, null, null, null);

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
            Intent intent = new Intent(ImageAddPopup.this, AddMemoActivity.class);              // 메모 추가 화면으로 값을 넘기면서 이동
            String path = getLocalImagePath(data).getAbsolutePath();
            intent.putExtra("IMAGE_PATH", path);
            setResult(RESULT_OK, intent);
            finish();
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File imageFile = null;
            Uri selectedImageUri = cameraImageUri;
            Cursor cursor = null;
            try {
                String[] project = { MediaStore.Images.Media.DATA };               // Uri 스키마를 content:/// 에서 file:/// 로  변경한다.

                assert selectedImageUri != null;
                cursor = getContentResolver().query(selectedImageUri, project, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                imageFile = new File(cursor.getString(column_index));

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            Intent intent = new Intent(ImageAddPopup.this, AddMemoActivity.class);              // 메모 추가 화면으로 값을 넘기면서 이동
            String path = imageFile.getAbsolutePath();
            intent.putExtra("IMAGE_PATH", path);
            setResult(RESULT_OK, intent);
            finish();

        } else if (requestCode == GET_IMAGE_URL && resultCode == RESULT_OK) {
            String path = data.getExtras().getString("IMAGE_PATH");

            Intent intent = new Intent(ImageAddPopup.this, AddMemoActivity.class);              // 메모 추가 화면으로 값을 넘기면서 이동
            intent.putExtra("IMAGE_PATH", path);
            setResult(RESULT_OK, intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getImageFromCamera() {                                             // 카메라에서 이미지 가져오기
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {                  // Ensure that there's a camera activity to handle the intent
            File photoFile = null;                                                  // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();          // Error occurred while creating the File
                finish();
                e.printStackTrace();
            }

            if (photoFile != null) {                                            // Continue only if the File was successfully created
                ContentValues values = new ContentValues();                     // 고화질 이미지 전송
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                cameraImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());                  // Create an image file name
        String imageFileName = "MEMO_" + timeStamp + "_";

        File storageDir = new File(Environment.getExternalStorageDirectory() + "/memo/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }
}
