package com.example.memoapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URL;

public class InputImageUrlPopup extends Activity {

    private Handler handler = new Handler();
    private EditText popup_getImageFromURL;
    private Button popup_sendButton;
    private Button popup_backButton;
    private TextView popup_invalidUrl;
    private Boolean url_result = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_image_url_popup);

        initLayout();

        popup_sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url_path = popup_getImageFromURL.getText().toString();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            url_result = true;
                            final URL url = new URL(url_path);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {  // 화면에 그려줄 작업
                                    Intent intent = new Intent(InputImageUrlPopup.this, ImageAddPopup.class);
                                    intent.putExtra("IMAGE_PATH", url_path);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            });
                        } catch(Exception e){
                            // 잘못된 URL 입력
                            url_result = false;
                            System.out.println(e);
                        }
                    }
                });

                thread.start();

                try {
                    thread.join();
                    if(url_result == true ) {
                        popup_invalidUrl.setVisibility(View.GONE);
                    } else {
                        popup_invalidUrl.setVisibility(View.VISIBLE);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        popup_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private void initLayout() {
        popup_getImageFromURL = (EditText) findViewById(R.id.popup_getImageFromURL);
        popup_sendButton = (Button) findViewById(R.id.popup_sendButton);
        popup_backButton = (Button) findViewById(R.id.popup_backButton);
        popup_invalidUrl = (TextView) findViewById(R.id.popup_invalidUrl);
    }
}
