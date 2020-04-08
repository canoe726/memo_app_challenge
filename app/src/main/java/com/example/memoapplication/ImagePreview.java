package com.example.memoapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

// 이미지 로딩 시 Picasso 라이브러리 사용 [출처 : https://github.com/square/picasso]

public class ImagePreview extends AppCompatActivity {
    private androidx.appcompat.widget.Toolbar toolbar;
    private Handler handler = new Handler();
    private ImageView preview_ImageView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        initLayout();

        intent = getIntent();
        String image_path = intent.getExtras().getString("IMAGE_ITEM_PATH");

        if(image_path.contains("https://")) {
            Thread thread = new Thread(new ImageLoadThread(image_path));
            thread.start();
        } else {
            image_path = "file://" + image_path;
            Picasso.get().load(image_path).into(preview_ImageView);
        }
    }

    private void initLayout() {
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.string_showImage);                  // 툴바 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preview_ImageView = (ImageView) findViewById(R.id.preview_ImageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                   // 툴바의 뒤로가기 버튼을 눌렀을 때 동작
        switch (item.getItemId()){
            case android.R.id.home: {                                       // toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public class ImageLoadThread implements Runnable {
        private String item;
        private ImageLoadThread(String item) {
            this.item = item;
        }

        @Override
        public void run() {
            try{
                URL url = new URL(item);
                InputStream is = url.openStream();
                handler.post(new Runnable() {
                    @Override
                    public void run() {                                             // 화면에 그려줄 작업
                        Picasso.get().load(item).into(preview_ImageView);
                    }
                });
                Picasso.get().load(item).into(preview_ImageView);
            } catch(Exception e){
                System.out.println(e);
            }
        }
    }
}
