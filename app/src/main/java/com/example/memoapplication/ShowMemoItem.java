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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import static com.example.memoapplication.AddMemoActivity.POPUP_RES;
import static com.example.memoapplication.MainActivity.globalMemoInfoData;
import static com.example.memoapplication.MainActivity.memoListArrayAdapter;

public class ShowMemoItem extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar toolbar;
    private TextView show_title;
    private TextView show_content;
    private EditText show_edit_title;
    private EditText show_edit_content;
    private Intent intent;
    private MemoInfoData memo_item;
    private RecyclerView imageRecyclerView;
    private ImageListViewAdapter imageListViewAdapter;
    private MenuItem editItem;
    private MenuItem saveItem;
    private LinearLayout show_wholeLinearLayout;
    private TextView show_addImageMinTextView;
    private View show_viewSpace;
    private ArrayList<String> imagePathList = new ArrayList<>();
    private int item_pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_memo_item);

        initLayout();

        show_addImageMinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 레이어 팝업으로 이동
                intent = new Intent(ShowMemoItem.this, ImageAddPopup.class);
                startActivityForResult(intent, POPUP_RES);
            }
        });

        show_wholeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 기존의 텍스트를 editText로 이동
                show_edit_title.setVisibility(View.VISIBLE);
                show_title.setVisibility(View.GONE);

                show_edit_content.setVisibility(View.VISIBLE);
                show_content.setVisibility(View.GONE);

                show_addImageMinTextView.setVisibility(View.VISIBLE);
                show_viewSpace.setVisibility(View.GONE);

                // 변경한 내용을 editText로 전달
                show_edit_title.setText(show_title.getText());
                show_edit_content.setText(show_content.getText());

                editItem.setVisible(false);
                saveItem.setVisible(true);
            }
        });
    }

    private void initLayout() {
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.show_toolbar);
        setSupportActionBar(toolbar);
        // 툴바 설정
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        memo_item = (MemoInfoData) intent.getSerializableExtra("memo_item");
        item_pos = intent.getExtras().getInt("ITEM_INDEX");
        show_title = (TextView) findViewById(R.id.show_title);
        show_content = (TextView) findViewById(R.id.show_content);
        show_title.setText(memo_item.getTitle());
        show_content.setText(memo_item.getContent());
        show_edit_title = (EditText) findViewById(R.id.show_edit_title);
        show_edit_content = (EditText) findViewById(R.id.show_edit_content);
        show_wholeLinearLayout = (LinearLayout) findViewById(R.id.show_wholeLinearLayout);
        show_addImageMinTextView = (TextView) findViewById(R.id.show_addImageMinTextView);
        show_viewSpace = (View) findViewById(R.id.show_viewSpace);

        if( memo_item.getImages() != null ) {
            imagePathList = memo_item.getImages();
        }

        imageRecyclerView = findViewById(R.id.show_recyclerListView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageRecyclerView.setLayoutManager(layoutManager);

        imageListViewAdapter = new ImageListViewAdapter(this, imagePathList);
        imageRecyclerView.setAdapter(imageListViewAdapter);

        imageRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), imageRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // 보기 기능일 때는 크게하기 기능만
                if( show_title.getVisibility() == View.VISIBLE ) {
                    intent = new Intent(ShowMemoItem.this, ImagePreview.class);
                    intent.putExtra("IMAGE_ITEM_PATH", imagePathList.get(position));
                    startActivity(intent);
                } else { // 편집 기능일 때는 크게하기, 삭제 기능
                    showImageDialog(position);
                }
            }
        }));
    }

    private void showImageDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowMemoItem.this);
        View builder_view = LayoutInflater.from(ShowMemoItem.this).inflate(R.layout.image_dialog, null, false);
        builder.setView(builder_view);

        final Button dialog_enlargeImage = (Button) builder_view.findViewById(R.id.dialog_enlargeImage);
        final Button dialog_deleteImage = (Button) builder_view.findViewById(R.id.dialog_deleteImage);

        final AlertDialog dialog = builder.create();

        final int pos = position;
        dialog_enlargeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ShowMemoItem.this, ImagePreview.class);
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
                Toast.makeText(ShowMemoItem.this, "이미지를 삭제하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }

    // 팝업으로 부터 값 받기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == POPUP_RES && resultCode == RESULT_OK ) {
            String imagePath = "";
            imagePath = data.getStringExtra("IMAGE_PATH");
            imagePathList.add(0, imagePath);
            imageListViewAdapter.notifyDataSetChanged();
        }
    }

    // RecyclerView item 클릭시 반응
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ShowMemoItem.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ShowMemoItem.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
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

    // 편집 버튼 툴바에 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.show_toolbar_button, menu);

        editItem = menu.findItem(R.id.show_edit_button_toolbar);
        saveItem = menu.findItem(R.id.show_save_button_toolbar);

        return super.onCreateOptionsMenu(menu);
    }

    // toolbar 키 뒤로가기
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            // 툴바의 뒤로가기 버튼 눌렀을 때 동작
            case android.R.id.home: {
                if( show_edit_title.getVisibility() == View.VISIBLE ) {
                    showSaveDialog();
                } else {
                    finish();
                }
                return true;
            }
            // 툴바의 저장 버튼 눌렀을 때 동작
            case R.id.show_save_button_toolbar: {
                saveRecyclerItemAndNotify();
                return true;
            }
            // 툴바의 편집 버튼 눌렀을 때 동작
            case R.id.show_edit_button_toolbar: {
                // 기존의 텍스트를 editText로 이동
                show_edit_title.setVisibility(View.VISIBLE);
                show_title.setVisibility(View.GONE);

                show_edit_content.setVisibility(View.VISIBLE);
                show_content.setVisibility(View.GONE);

                show_addImageMinTextView.setVisibility(View.VISIBLE);
                show_viewSpace.setVisibility(View.GONE);

                // 변경한 내용을 editText로 전달
                show_edit_title.setText(show_title.getText());
                show_edit_content.setText(show_content.getText());

                editItem.setVisible(false);
                saveItem.setVisible(true);

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 뒤로가기 버튼 클릭시 종료
    @Override
    public void onBackPressed() {
        // 편집 모드 일때만 물어봄
        if( show_edit_title.getVisibility() == View.VISIBLE ) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowMemoItem.this);
        View builder_view = LayoutInflater.from(ShowMemoItem.this).inflate(R.layout.save_dialog, null, false);
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
        // 기존의 텍스트를 textView로 이동
        show_edit_title.setVisibility(View.GONE);
        show_title.setVisibility(View.VISIBLE);

        show_edit_content.setVisibility(View.GONE);
        show_content.setVisibility(View.VISIBLE);

        // 변경한 내용을 editText로 전달
        show_title.setText(show_edit_title.getText());
        show_content.setText(show_edit_content.getText());

        String thumbnail = "";
        if(imagePathList.size() != 0) {
            thumbnail = imagePathList.get(0);
        }
        String title = show_title.getText().toString();
        String content = show_content.getText().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분");
        Date date = new Date();
        String cur_date = simpleDateFormat.format(date);

        MemoInfoData memoInfoData = new MemoInfoData(thumbnail, imagePathList, title, content, cur_date, false);
        globalMemoInfoData.set(item_pos, memoInfoData);
        memoListArrayAdapter.notifyDataSetChanged();
        finish();
    }
}
