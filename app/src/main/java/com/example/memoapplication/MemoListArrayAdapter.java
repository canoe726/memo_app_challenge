package com.example.memoapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import static com.example.memoapplication.MainActivity.sqLiteDatabase;

// 이미지 로딩 시 Picasso 라이브러리 사용 [출처 : https://github.com/square/picasso]

public class MemoListArrayAdapter extends RecyclerView.Adapter<MemoListArrayAdapter.ViewHolder> {
    private static boolean delete_mode = false;

    private ArrayList<MemoInfoData> memoInfoData;
    private Handler handler = new Handler();
    private Context context;

    MemoListArrayAdapter(Context context, ArrayList<MemoInfoData> memoInfoData) {
        this.context = context;
        this.memoInfoData = memoInfoData;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView imageView;
        TextView title;
        TextView content;
        TextView date;
        CheckBox checkBox;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.memo_imageView);
            title = itemView.findViewById(R.id.memo_title);
            content = itemView.findViewById(R.id.memo_content);
            date = itemView.findViewById(R.id.memo_date);
            checkBox = itemView.findViewById(R.id.list_checkBoxImageView);

            if( delete_mode && checkBox.getVisibility() == View.GONE ) {            // 삭제 모드일 때 체크박스 활성화
                checkBox.setVisibility(View.VISIBLE);
            }

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        private MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1001:                                              // 편집 항목을 선택시
                        Intent intent = new Intent(itemView.getContext(), ShowMemoItem.class);
                        intent.putExtra("memo_item", memoInfoData.get(getAdapterPosition()));
                        context.startActivity(intent);
                        break;
                    case 1002:                                              // 삭제 항목을 선택시
                        int memoId = memoInfoData.get(getAdapterPosition()).getId();
                        String sqlQuery = "DELETE FROM MEMO_INFO WHERE ID = " + "'" + memoId + "'";
                        System.out.println(sqlQuery);
                        sqLiteDatabase.execSQL(sqlQuery);                   // 데이터 베이스에 존재하는 메모 정보 삭제

                        memoInfoData.remove(getAdapterPosition());

                        notifyItemRemoved(getAdapterPosition());
                        notifyItemChanged(getAdapterPosition(), memoInfoData.size());         // 화면상 보이는 메모 삭제

                        Toast.makeText(context, "메모를 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        };
    }

    @NonNull
    @Override
    public MemoListArrayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.memo_list_item, parent, false);
        return new MemoListArrayAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(memoInfoData.get(position).getChecked() == 1 ) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        String item = memoInfoData.get(position).getThumbnail();
        if(!TextUtils.isEmpty(item)) {
            if( item.contains("https://") || item.contains("http://") ) {           // 웹 이미지 이면 thread 생성
                Thread thread = new Thread(new MemoListArrayAdapter.ImageLoadThread(item, holder));
                thread.start();
            } else {                                                               // 이미지 경로가 있으면 로컬어서 이미지 불러오기
                item = "file://" + item;
               Picasso.get().load(item).into(holder.imageView);
            }
        } else {                                                                    // 이미지 경로가 없으면 default 이미지 출력
            holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_image_black_24dp));
        }

        holder.title.setText(memoInfoData.get(position).getTitle());
        holder.content.setText(memoInfoData.get(position).getContent());
        holder.date.setText(memoInfoData.get(position).getDate());
    }

    public boolean getDeleteMode() { return delete_mode; }

    public void setDeleteMode(boolean delete_mode) { this.delete_mode = delete_mode; }

    @Override
    public int getItemCount() { return memoInfoData.size(); }

    public class ImageLoadThread implements Runnable {
        private String item;
        private MemoListArrayAdapter.ViewHolder holder;
        private ImageLoadThread(String item, MemoListArrayAdapter.ViewHolder holder) {
            this.item = item;
            this.holder = holder;
        }

        @Override
        public void run() {
            try{
                URL url = new URL(item);
                InputStream is = url.openStream();
                handler.post(new Runnable() {
                    @Override
                    public void run() {             // 화면에 그려줄 작업
                        Picasso.get().load(item).into(holder.imageView);
                    }
                });
                Picasso.get().load(item).into(holder.imageView);
            } catch(Exception e){
                System.out.println(e);
            }
        }
    }
}
