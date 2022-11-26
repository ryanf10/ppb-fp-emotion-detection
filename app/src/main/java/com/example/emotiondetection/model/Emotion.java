package com.example.emotiondetection.model;

import android.content.ContentValues;

public class Emotion {
    public static final String[] emotions = {"Angry", "Disgusted", "Fearful", "Happy", "Neutral", "Sad", "Surprised"};

    private int id;
    private String nama;
    private int total;

    public Emotion(int id, String nama, int total) {
        this.id = id;
        this.nama = nama;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
