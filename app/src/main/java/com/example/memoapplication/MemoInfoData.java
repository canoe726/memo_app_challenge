package com.example.memoapplication;

import java.io.Serializable;
import java.util.ArrayList;

public class MemoInfoData implements Serializable, Comparable<MemoInfoData> {

    private int id;
    private String thumbnail;
    private ArrayList<String> images;
    private String title;
    private String content;
    private String date;
    private Integer checked;

    MemoInfoData(int id, String thumbnail, ArrayList<String> images, String title, String content, String date, Integer checked) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.images = images;
        this.title = title;
        this.content = content;
        this.date = date;
        this.checked = checked;
    }

    @Override
    public int compareTo(MemoInfoData data) {
        return data.date.compareTo(this.date);          // 최신 순으로 메모 정렬
    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

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

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    // getter
    public int getId() {
        return id;
    }

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

    public Integer getChecked() {
        return checked;
    }
}
