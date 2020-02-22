package com.example.memoapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.memoapplication.MainActivity.globalMemoInfoData;
import static com.example.memoapplication.MainActivity.originalMemoInfoData;

public class TaskService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) { //핸들링 하는 부분
        Log.e("Error","onTaskRemoved - " + rootIntent);

        // 최근 실행된 앱화면에서 앱 강제 종료시 이벤트
        try {
            String inputData = "";

            boolean same = true;
            if( globalMemoInfoData.size() != originalMemoInfoData.size() ) {
                same = false;
            } else {
                for(int i=0; i<globalMemoInfoData.size(); i++) {
                    if(     (globalMemoInfoData.get(i).getThumbnail() != originalMemoInfoData.get(i).getThumbnail()) ||
                            (globalMemoInfoData.get(i).getTitle() != originalMemoInfoData.get(i).getTitle()) ||
                            (globalMemoInfoData.get(i).getContent() != originalMemoInfoData.get(i).getContent()) ) {
                        same = false;
                        break;
                    }
                }
            }

            if( !same ) {
                FileOutputStream fos = openFileOutput("memo_info.json", Context.MODE_PRIVATE);

                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < globalMemoInfoData.size(); i++) {
                    MemoInfoData memoInfoData = globalMemoInfoData.get(i);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("thumbnail", memoInfoData.getThumbnail());
                    jsonObject.put("images", memoInfoData.getImages());
                    jsonObject.put("title", memoInfoData.getTitle());
                    jsonObject.put("content", memoInfoData.getContent());
                    jsonObject.put("date", memoInfoData.getDate());

                    jsonArray.put(jsonObject);
                }
                inputData = jsonArray.toString();
                fos.write(inputData.getBytes());
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        stopSelf(); //서비스도 같이 종료
    }
}
