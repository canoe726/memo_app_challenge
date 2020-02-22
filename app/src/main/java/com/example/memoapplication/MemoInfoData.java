package com.example.memoapplication;

import android.app.Application;

import java.io.Serializable;
import java.util.ArrayList;

public class MemoInfoData extends Application implements Serializable {

    private String thumbnail;
    private ArrayList<String> images;
    private String title;
    private String content;
    private String date;
    private Boolean checked;

    MemoInfoData(String thumbnail, ArrayList<String> images, String title, String content, String date, Boolean checked) {
        this.thumbnail = thumbnail;
        this.images = images;
        this.title = title;
        this.content = content;
        this.date = date;
        this.checked = checked;
    }

    // setter
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    // getter
    public String getThumbnail() {
        return thumbnail;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public Boolean getChecked() {
        return checked;
    }
}
