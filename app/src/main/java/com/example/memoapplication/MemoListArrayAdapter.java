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

import static com.example.memoapplication.MainActivity.globalMemoInfoData;
import static com.example.memoapplication.MainActivity.memoListArrayAdapter;

// 이미지 로딩 시 Picasso 라이브러리 사용 [출처 : https://github.com/square/picasso]

public class MemoListArrayAdapter extends RecyclerView.Adapter<MemoListArrayAdapter.ViewHolder> {

    private Handler handler = new Handler();
    private Context context;
    private static boolean delete_mode = false;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView imageView;
        TextView title;
        TextView content;
        TextView date;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.memo_imageView);

            title = itemView.findViewById(R.id.memo_title);
            content = itemView.findViewById(R.id.memo_content);
            date = itemView.findViewById(R.id.memo_date);
            checkBox = itemView.findViewById(R.id.list_checkBoxImageView);

            // 삭제 모드일 때 체크박스 활성화
            if( delete_mode == true && checkBox.getVisibility() == View.GONE ) {
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
                    case 1001:  // 편집 항목을 선택시
                        Intent intent = new Intent(itemView.getContext(), ShowMemoItem.class);
                        intent.putExtra("memo_item", globalMemoInfoData.get(getAdapterPosition()));
                        context.startActivity(intent);
                        break;
                    case 1002: // 삭제 항목을 선택시
                        globalMemoInfoData.remove(getAdapterPosition());
                        memoListArrayAdapter.notifyDataSetChanged();
                        Toast.makeText(context, "메모를 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        };
    }

    public MemoListArrayAdapter(Context context, ArrayList<MemoInfoData> globalMemoInfoData) {
        this.context = context;
    }

    @NonNull
    @Override
    public MemoListArrayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.memo_list_item, parent, false);
        return new MemoListArrayAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(globalMemoInfoData.get(position).getChecked() == true ) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        String item = globalMemoInfoData.get(position).getThumbnail();
        if( !TextUtils.isEmpty(item) ) {
            if( item.contains("https://") || item.contains("http://") ) {
                Thread thread = new Thread(new MemoListArrayAdapter.ImageLoadThread(item, holder));
                thread.start();
            } else {
                item = "file://" + item;
               Picasso.get().load(item).into(holder.imageView);
            }
        } else {
            holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_image_black_24dp));
        }

        holder.title.setText(globalMemoInfoData.get(position).getTitle());
        holder.content.setText(globalMemoInfoData.get(position).getContent());
        holder.date.setText(globalMemoInfoData.get(position).getDate());
    }

    public boolean getDeleteMode() { return delete_mode; }

    public void setDeleteMode(boolean delete_mode) { this.delete_mode = delete_mode; }

    @Override
    public int getItemCount() { return globalMemoInfoData.size(); }

    public class ImageLoadThread implements Runnable {
        private String item;
        private MemoListArrayAdapter.ViewHolder holder;
        public ImageLoadThread(String item, MemoListArrayAdapter.ViewHolder holder) {
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
                    public void run() {  // 화면에 그려줄 작업
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
