package com.example.memoapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<MemoInfoData> originalMemoInfoData = new ArrayList<MemoInfoData>();
    public static ArrayList<MemoInfoData> globalMemoInfoData = new ArrayList<MemoInfoData>();
    public static MemoListArrayAdapter memoListArrayAdapter;

    private androidx.appcompat.widget.Toolbar toolbar;
    private RecyclerView memo_recyclerView;
    private FloatingActionButton floatingActionButton;
    private Intent intent;
    private Bundle bundle;

    // 권한 리스트 (저장소, 카메라)
    String[] permission_list = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 앱 강제 종료시 핸들링을 위한 서비스 시작
        startService(new Intent(this, TaskService.class));
        // 파일 읽고 쓰기 및 카메라 권한 체크
        checkPermission();
        initLayout();
        // 로컬에서 메모 파일 불러와 전역 변수에 저장
        getMemoData();
        globalMemoInfoData.addAll(originalMemoInfoData);

        memoListArrayAdapter = new MemoListArrayAdapter(this, globalMemoInfoData);
        memoListArrayAdapter.setDeleteMode(false);
        memo_recyclerView.setAdapter(memoListArrayAdapter);

        memo_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), memo_recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if( !memoListArrayAdapter.getDeleteMode() ) {
                    intent = new Intent(MainActivity.this, ShowMemoItem.class);
                    bundle = new Bundle();
                    bundle.putSerializable("memo_item", globalMemoInfoData.get(position));
                    intent.putExtras(bundle);
                    intent.putExtra("ITEM_INDEX", position);
                    startActivity(intent);
                } else { // 삭제 모드 일때
                    if( globalMemoInfoData.get(position).getChecked() == true ) {
                        globalMemoInfoData.get(position).setChecked(false);
                    } else {
                        globalMemoInfoData.get(position).setChecked(true);
                    }
                    memoListArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, AddMemoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initLayout() {
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        memo_recyclerView = (RecyclerView) findViewById(R.id.memo_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        memo_recyclerView.setLayoutManager(layoutManager);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.main_floatingButton);

        globalMemoInfoData.clear();
        originalMemoInfoData.clear();
    }

    private void getMemoData() {
        try {
            FileOutputStream fos = null;
            File file = getApplicationContext().getFileStreamPath("memo_info.json");
            // 파일이 없으면 빈 파일 생성
            if(file == null || !file.exists()) {
                String inputData = "";
                fos = openFileOutput("memo_info.json", Context.MODE_PRIVATE);
                fos.write(inputData.getBytes());
                fos.close();
            }
            // 파일이 있으면 내용 불러오기
            else {
                String buffer = "";
                String data = null;
                FileInputStream fis = null;
                try {
                    fis = openFileInput("memo_info.json");
                    BufferedReader iReader = new BufferedReader(new InputStreamReader((fis)));

                    data = iReader.readLine();
                    while(data != null)
                    {
                        buffer += data;
                        data = iReader.readLine();
                    }
                    iReader.close();

                    try {
                        JSONArray array;
                        if( buffer == "" ) {
                            array = new JSONArray();
                        } else {
                            array = new JSONArray(buffer);
                        }
                        for(int i=0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            String thumbnail = object.getString("thumbnail");
                            ArrayList<String> images = null;
                            if(!thumbnail.equals("")) {
                                thumbnail = object.getString("thumbnail");

                                // String to ArrayList<String>
                                String image = object.getString("images");
                                image = image.replace("[","");
                                image = image.replace("]","");
                                image = image.replace(" ","");
                                images = new ArrayList<>(Arrays.asList(image.split(",")));
                            }
                            String title = object.getString("title");
                            String content = object.getString("content");
                            String date = object.getString("date");

                            MemoInfoData memoInfoData = new MemoInfoData(thumbnail, images, title, content, date, false);
                            originalMemoInfoData.add(memoInfoData);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 메모 추가, 삭제 버튼 툴바에 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        // appbar 메뉴 재설정
        if(memoListArrayAdapter.getDeleteMode() == true ) {
            menuInflater.inflate(R.menu.main_toolbar_delete_button, menu);
        } else {
            menuInflater.inflate(R.menu.main_toolbar_button, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    // toolbar 아이템 동작
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            // 일반 모드, 툴바의 메모 추가 버튼 눌렀을 때 동작
            case R.id.main_addMemo: {
                Toast.makeText(MainActivity.this, "메모 추가 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, AddMemoActivity.class);
                startActivity(intent);
                return true;
            }
            // 일반 모드, 툴바의 메모 삭제 버튼 눌렀을 때 동작
            case R.id.main_deleteMode: {
                // 삭제할 메모가 없을 때
                if( globalMemoInfoData.size() == 0 ) {
                    Toast.makeText(MainActivity.this, "삭제할 메모가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    memoListArrayAdapter.setDeleteMode(true);
                    // appbar 메뉴 변경 위한 onCreateOptionsMenu 호출
                    invalidateOptionsMenu();
                    memo_recyclerView.setAdapter(memoListArrayAdapter);
                }
                return true;
            }
            // 삭제 모드, 툴바의 메모 전체 선택 버튼 눌렀을 때 동작
            case R.id.main_selectAll: {
                // 전체 선택
                for(int i=0; i<globalMemoInfoData.size(); i++) {
                    globalMemoInfoData.get(i).setChecked(true);
                }
                memoListArrayAdapter.notifyDataSetChanged();
                return true;
            }
            // 삭제 모드, 툴바의 메모 삭제 버튼 눌렀을 때 동작
            case R.id.main_deleteMemo: {
                // 선택된 항목 삭제
                for(int i=0; i<globalMemoInfoData.size(); i++) {
                    if( globalMemoInfoData.get(i).getChecked() == true ) {
                        globalMemoInfoData.remove(i);
                        i--;
                    }
                }
                memoListArrayAdapter.notifyDataSetChanged();

                memoListArrayAdapter.setDeleteMode(false);
                // appbar 메뉴 변경 위한 onCreateOptionsMenu 호출
                invalidateOptionsMenu();
                memo_recyclerView.setAdapter(memoListArrayAdapter);
                Toast.makeText(MainActivity.this, "선택한 메모를 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                return true;
            }
            // 삭제 모드, 툴바의 메모 취소 버튼 눌렀을 때 동작 => 일반 모드로 변경
            case R.id.main_cancel: {
                memoListArrayAdapter.setDeleteMode(false);
                // appbar 메뉴 변경 위한 onCreateOptionsMenu 호출
                invalidateOptionsMenu();
                memo_recyclerView.setAdapter(memoListArrayAdapter);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 권한 허용 체크
    public void checkPermission() {
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for(String permission : permission_list) {
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);
            if(chk == PackageManager.PERMISSION_DENIED) {
                //권한 허용을여부를 확인하는 창을 띄운다
                ActivityCompat.requestPermissions(this, permission_list,0);
            }
        }
    }

    // 모든 권한 허용을 하지 않으면 앱 종료
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0)
        {
            for(int i=0; i<grantResults.length; i++)
            {
                // 모든 권한이 허용 되었다면
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    System.out.println("All Permission is accepted");
                }
                else {
                    Toast.makeText(getApplicationContext(),"권한 설정을 해주세요",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    // RecyclerView item 클릭시 반응
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    // 뒤로가기 버튼
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // 삭제 모드이면 일반모드로 변경
        if( memoListArrayAdapter.getDeleteMode() == true ) {
            memoListArrayAdapter.setDeleteMode(false);
            invalidateOptionsMenu();
            memo_recyclerView.setAdapter(memoListArrayAdapter);
        }
    }

    // 메모 내용 저장
    @Override
    protected void onDestroy() {
        super.onDestroy();

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
    }
}
