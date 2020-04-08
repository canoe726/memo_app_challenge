package com.example.memoapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    public static int databaseMemoId;
    public static SQLiteDatabase sqLiteDatabase;

    private MemoListArrayAdapter memoListArrayAdapter;
    private ArrayList<MemoInfoData> memoInfoData = new ArrayList<MemoInfoData>();
    private SharedPreferences sharedPreferences;
    private androidx.appcompat.widget.Toolbar toolbar;
    private RecyclerView memo_recyclerView;
    private FloatingActionButton floatingActionButton;
    private Intent intent;
    private Bundle bundle;

    String[] permission_list = {        // 권한 리스트 (저장소, 카메라)
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("memoId", MODE_PRIVATE);      // 메모 id 값 생성
        databaseMemoId = sharedPreferences.getInt("ID", 0);             // ID 라는 key 에 저장된 값이 있으면 반환, 그렇지 않으면 0 반환

        sqLiteDatabase = init_database();       // 데이터 베이스가 없으면 생성
        init_table();                           // 데이터 베이스 테이블 정의

        setContentView(R.layout.activity_main);
        checkPermission();                      // 파일 읽고 쓰기 및 카메라 권한 체크
        initLayout();

        memoListArrayAdapter = new MemoListArrayAdapter(this, memoInfoData);
        memoListArrayAdapter.setDeleteMode(false);
        memo_recyclerView.setAdapter(memoListArrayAdapter);
        memo_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), memo_recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if( !memoListArrayAdapter.getDeleteMode() ) {
                    intent = new Intent(MainActivity.this, ShowMemoItem.class);
                    bundle = new Bundle();
                    bundle.putSerializable("memo_item", memoInfoData.get(position));
                    intent.putExtras(bundle);
                    intent.putExtra("ITEM_INDEX", position);
                    startActivity(intent);
                } else {                                                    // 삭제 모드 일때
                    if( memoInfoData.get(position).getChecked() == 1 ) {
                        memoInfoData.get(position).setChecked(0);
                    } else {
                        memoInfoData.get(position).setChecked(1);
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

    @Override
    protected void onResume() {
        super.onResume();

        load_memoInfo();                        // 데이터 베이스 테이블 값 쿼리로 불러오기
    }

    @Override
    protected void onStop() {
        super.onStop();

        sharedPreferences = getSharedPreferences("memoId", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ID", databaseMemoId);
        editor.commit();
    }

    private SQLiteDatabase init_database() {
        SQLiteDatabase database = null;
        File file = new File(getFilesDir(), "memoInfo.db");
        System.out.println("PATH : " + file.toString());

        try {
            database = SQLiteDatabase.openOrCreateDatabase(file, null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if( database == null ) {
            System.out.println("DB creation failed. " + file.getAbsolutePath());
        }

        return database;
    }

    private void init_table() {
        if( sqLiteDatabase != null ) {
            String sqlCreateQuery = "CREATE TABLE IF NOT EXISTS MEMO_INFO (" +
                    "ID "          + "INT PRIMARY KEY, " +
                    "THUMBNAIL "   + "VARCHAR, " +
                    "IMAGES "      + "TEXT, " +
                    "TITLE "       + "VARCHAR NOT NULL, " +
                    "CONTENT "     + "TEXT, " +
                    "DATE "        + "VARCHAR, " +
                    "CHECKED "     + "INTEGER" + ")";
            System.out.println(sqlCreateQuery);
            sqLiteDatabase.execSQL(sqlCreateQuery);
        }
    }

    private void load_memoInfo() {
        if( sqLiteDatabase != null ) {
            memoInfoData.clear();

            String sqlQuery = "SELECT * FROM MEMO_INFO";
            Cursor cursor = null;
            cursor = sqLiteDatabase.rawQuery(sqlQuery, null);       // 쿼리 실행

            if( cursor.moveToLast() ) {                                         // 레코드가 존재하면 실행
                while(!cursor.isBeforeFirst()) {
                    int id = cursor.getInt(0);
                    String thumbnail = cursor.getString(1);

                    String temp_images = cursor.getString(2);
                    temp_images = temp_images.replace("[", "");
                    temp_images = temp_images.replace("]", "");
                    temp_images = temp_images.replace(" ", "");
                    ArrayList<String> images = new ArrayList<String>(Arrays.asList(temp_images.split(",")));

                    String title = cursor.getString(3);
                    String content = cursor.getString(4);
                    String date = cursor.getString(5);
                    Integer checked = cursor.getInt(6);

                    MemoInfoData data = new MemoInfoData(id, thumbnail, images, title, content, date, checked);
                    memoInfoData.add(data);

                    cursor.moveToPrevious();
                }
                cursor.close();
            }
            Collections.sort(memoInfoData);
            memoListArrayAdapter.notifyDataSetChanged();
        }
    }

    private void initLayout() {
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        memo_recyclerView = (RecyclerView) findViewById(R.id.memo_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        memo_recyclerView.setLayoutManager(layoutManager);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.main_floatingButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {          // 메모 추가, 삭제 버튼 툴바에 추가
        MenuInflater menuInflater = getMenuInflater();

        if(memoListArrayAdapter.getDeleteMode()) {           // appbar 메뉴 재설정
            menuInflater.inflate(R.menu.main_toolbar_delete_button, menu);
        } else {
            menuInflater.inflate(R.menu.main_toolbar_button, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {      // toolbar 아이템 동작
        switch(item.getItemId()) {
            case R.id.main_addMemo: {                  // 일반 모드, 툴바의 메모 추가 버튼 눌렀을 때 동작
                Toast.makeText(MainActivity.this, "메모 추가 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, AddMemoActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.main_deleteMode: {                // 일반 모드, 툴바의 메모 삭제 버튼 눌렀을 때 동작
                if( memoInfoData.size() == 0 ) {        // 삭제할 메모가 없을 때
                    Toast.makeText(MainActivity.this, "삭제할 메모가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    memoListArrayAdapter.setDeleteMode(true);
                    invalidateOptionsMenu();             // appbar 메뉴 변경 위한 onCreateOptionsMenu 호출
                    memo_recyclerView.setAdapter(memoListArrayAdapter);
                }
                return true;
            }
            case R.id.main_selectAll: {                           // 삭제 모드, 툴바의 메모 전체 선택 버튼 눌렀을 때 동작
                for(int i=0; i<memoInfoData.size(); i++) {        // 전체 선택, 보이는 화면의 목록 제거
                    memoInfoData.get(i).setChecked(1);
                }
                memoListArrayAdapter.notifyDataSetChanged();
                return true;
            }
            case R.id.main_deleteMemo: {                               // 삭제 모드, 툴바의 메모 삭제 버튼 눌렀을 때 동작
                for(int i=0; i<memoInfoData.size(); i++) {
                    if( memoInfoData.get(i).getChecked() == 1 ) {      // 선택된 항목 삭제
                        int delete_id = memoInfoData.get(i).getId();
                        String sqlQuery = "DELETE FROM MEMO_INFO WHERE ID = " + "'" +  delete_id + "'";
                        System.out.println(sqlQuery);
                        sqLiteDatabase.execSQL(sqlQuery);              // 데이터 베이스에서 선택한 메모 정보 삭제

                        memoInfoData.remove(i);
                        i--;
                    }
                }
                memoListArrayAdapter.notifyDataSetChanged();
                memoListArrayAdapter.setDeleteMode(false);

                invalidateOptionsMenu();        // appbar 메뉴 변경 위한 onCreateOptionsMenu 호출
                memo_recyclerView.setAdapter(memoListArrayAdapter);
                Toast.makeText(MainActivity.this, "선택한 메모를 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.main_cancel: {            // 삭제 모드, 툴바의 메모 취소 버튼 눌렀을 때 동작 => 일반 모드로 변경
                memoListArrayAdapter.setDeleteMode(false);
                invalidateOptionsMenu();         // appbar 메뉴 변경 위한 onCreateOptionsMenu 호출
                memo_recyclerView.setAdapter(memoListArrayAdapter);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkPermission() {                               // 권한 허용 체크
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {       // 현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
            return;
        }

        for(String permission : permission_list) {
            int chk = checkCallingOrSelfPermission(permission);      // 권한 허용 여부를 확인한다.
            if(chk == PackageManager.PERMISSION_DENIED) {            // 권한 허용을여부를 확인하는 창을 띄운다
                ActivityCompat.requestPermissions(this, permission_list,0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {        // 모든 권한 허용을 하지 않으면 앱 종료
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0)
        {
            for(int i=0; i<grantResults.length; i++)
            {
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){         // 모든 권한이 허용 되었다면
                    System.out.println("All Permission is accepted");
                } else {
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

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {     // RecyclerView item 클릭시 반응
        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        private RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
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
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
    }

    @Override
    public void onBackPressed() {                         // 뒤로가기 버튼
        super.onBackPressed();

        if(memoListArrayAdapter.getDeleteMode()) {        // 삭제 모드이면 일반모드로 변경
            memoListArrayAdapter.setDeleteMode(false);
            invalidateOptionsMenu();
            memo_recyclerView.setAdapter(memoListArrayAdapter);
        }
    }
}
