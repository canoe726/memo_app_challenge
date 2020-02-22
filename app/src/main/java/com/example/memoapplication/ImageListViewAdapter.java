package com.example.memoapplication;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

// 이미지 로딩 시 Picasso 라이브러리 사용 [출처 : https://github.com/square/picasso]

public class ImageListViewAdapter extends RecyclerView.Adapter<ImageListViewAdapter.ViewHolder> {

    private Handler handler = new Handler();
    private ArrayList<String> imageList;
    private Context context;
    private int height = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView item_imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_imageView = itemView.findViewById(R.id.item_imageView);
            item_imageView.getLayoutParams().height = height;
            item_imageView.getLayoutParams().width = height;
        }
    }

    public ImageListViewAdapter(Context context, ArrayList<String> imageList) {
        this.imageList = imageList;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_list_item, parent, false);
        height = parent.getMeasuredHeight();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListViewAdapter.ViewHolder holder, int position) {
        String item = imageList.get(position);

        if( item.contains("https://") ) {
            Thread thread = new Thread(new ImageLoadThread(item, holder));
            thread.start();
        } else {
            item = "file://" + item;
            Picasso.get().load(item).into(holder.item_imageView);
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImageLoadThread implements Runnable {
        private String item;
        private ImageListViewAdapter.ViewHolder holder;
        public ImageLoadThread(String item, ImageListViewAdapter.ViewHolder holder) {
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
                        Picasso.get().load(item).into(holder.item_imageView);
                    }
                });
                Picasso.get().load(item).into(holder.item_imageView);
            } catch(Exception e){
                System.out.println(e);
            }
        }
    }
}
