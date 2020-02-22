package com.example.memoapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.memoapplication.MainActivity.globalMemoInfoData;
import static com.example.memoapplication.MainActivity.memoListArrayAdapter;

public class AddMemoActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar toolbar;
    private EditText add_title;
    private EditText add_content;
    private TextView add_addImageTextView;
    private TextView add_addImageMinTextView;
    private RecyclerView imageRecyclerView;
    private ImageListViewAdapter imageListViewAdapter;
    private ArrayList<String> imagePathList = new ArrayList<>();
    private Intent intent;

    static final int POPUP_RES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        initLayout();

        add_addImageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 레이어 팝업으로 이동
                intent = new Intent(AddMemoActivity.this, ImageAddPopup.class);
                startActivityForResult(intent, 1);
            }
        });

        add_addImageMinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 레이어 팝업으로 이동
                intent = new Intent(AddMemoActivity.this, ImageAddPopup.class);
                startActivityForResult(intent, POPUP_RES);
            }
        });

        imageRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), imageRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddMemoActivity.this, R.style.MyDialogTheme);
                View builder_view = LayoutInflater.from(AddMemoActivity.this).inflate(R.layout.image_dialog, null, false);
                builder.setView(builder_view);

                final Button dialog_enlargeImage = (Button) builder_view.findViewById(R.id.dialog_enlargeImage);
                final Button dialog_deleteImage = (Button) builder_view.findViewById(R.id.dialog_deleteImage);

                final AlertDialog dialog = builder.create();

                final int pos = position;
                dialog_enlargeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(AddMemoActivity.this, ImagePreview.class);
                        intent.putExtra("IMAGE_ITEM_PATH", imagePathList.get(pos));
                        dialog.dismiss();
                        startActivity(intent);
                    }
                });

                dialog_deleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagePathList.remove(pos);
                        imageListViewAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        Toast.makeText(AddMemoActivity.this, "이미지를 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));
    }

    private void initLayout() {
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.add_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        add_title = (EditText) findViewById(R.id.add_title);
        add_content = (EditText) findViewById(R.id.add_content);
        add_addImageTextView = (TextView) findViewById(R.id.add_addImageTextView);
        add_addImageMinTextView= (TextView) findViewById(R.id.add_addImageMinTextView);
        imageRecyclerView = (RecyclerView) findViewById(R.id.add_recyclerListView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageRecyclerView.setLayoutManager(layoutManager);

        imageListViewAdapter = new ImageListViewAdapter(this, imagePathList);
        imageRecyclerView.setAdapter(imageListViewAdapter);
    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    // RecyclerView item 클릭시 반응
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private AddMemoActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final AddMemoActivity.ClickListener clickListener) {
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
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    // 팝업으로 부터 값 받기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 추가하기 텍스트 크기를 줄이고 이미지 보여주기
        if (add_addImageTextView.getVisibility() == View.VISIBLE) {
            add_addImageTextView.setVisibility(View.GONE);
            add_addImageMinTextView.setVisibility(View.VISIBLE);
            imageRecyclerView.setVisibility(View.VISIBLE);
        }

        if( requestCode == POPUP_RES && resultCode == RESULT_OK ) {
            String imagePath = "";
            imagePath = data.getStringExtra("IMAGE_PATH");
            imagePathList.add(0, imagePath);
            imageListViewAdapter.notifyDataSetChanged();
        }
    }

    // 저장 버튼 툴바에 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_toolbar_button, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // toolbar 아이템 동작
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            // 툴바의 뒤로가기 버튼 눌렀을 때 동작
            case android.R.id.home: {
                String title = add_title.getText().toString();
                String content = add_content.getText().toString();
                if( title.equals("") && content.equals("") && imagePathList.size() == 0 ) {
                    finish();
                } else {
                    showSaveDialog();
                }
                return true;
            }
            // 툴바의 저장 버튼 눌렀을 때 동작
            case R.id.add_save_button_toolbar: {
                saveRecyclerItemAndNotify();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 편집 도중 종료시 저장 여부 묻기
    @Override
    public void onBackPressed() {
        String title = add_title.getText().toString();
        String content = add_content.getText().toString();
        // 입력한 내용이 있다면 저장 여부 다이얼로그 생성
        if( title.equals("") && content.equals("") && imagePathList.size() == 0 ) {
            finish();
        } else {
            showSaveDialog();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddMemoActivity.this);
        View builder_view = LayoutInflater.from(AddMemoActivity.this).inflate(R.layout.save_dialog, null, false);
        builder.setView(builder_view);

        final Button dialog_save = (Button) builder_view.findViewById(R.id.dialog_save);
        final Button dialog_notSave = (Button) builder_view.findViewById(R.id.dialog_notSave);

        final AlertDialog dialog = builder.create();

        dialog_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                saveRecyclerItemAndNotify();
            }
        });

        dialog_notSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog.show();
    }

    private void saveRecyclerItemAndNotify() {
        String title = add_title.getText().toString();
        String content = add_content.getText().toString();

        if( title.equals("") ) {
            Toast.makeText(AddMemoActivity.this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
        } else {
            String thumbnail = "";
            if(imagePathList.size() != 0) {
                thumbnail = imagePathList.get(0);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분");
            Date date = new Date();
            String cur_date = simpleDateFormat.format(date);

            MemoInfoData memoInfoData = new MemoInfoData(thumbnail, imagePathList, title, content, cur_date, false);
            globalMemoInfoData.add(0, memoInfoData);

            Toast.makeText(AddMemoActivity.this, "메모가 저장되었습니다.", Toast.LENGTH_SHORT).show();

            intent = new Intent(AddMemoActivity.this, MainActivity.class);
            setResult(RESULT_OK, intent);
            memoListArrayAdapter.notifyDataSetChanged();
            finish();
        }
    }
}
