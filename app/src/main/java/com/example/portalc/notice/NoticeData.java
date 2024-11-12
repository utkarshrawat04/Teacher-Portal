package com.example.portalc.notice;

public class NoticeData {
    private String title;
    private String imageUrl;
    private String date;
    private String time;
    private String key;

    public NoticeData() {
        // Default constructor required for calls to DataSnapshot.getValue(NoticeData.class)
    }

    public NoticeData(String title, String imageUrl, String date, String time, String key) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.date = date;
        this.time = time;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

