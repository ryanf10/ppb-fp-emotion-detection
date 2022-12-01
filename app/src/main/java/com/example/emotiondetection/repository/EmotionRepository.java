package com.example.emotiondetection.repository;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;

import com.example.emotiondetection.database.Database;
import com.example.emotiondetection.model.Detect;
import com.example.emotiondetection.model.Emotion;
import com.example.emotiondetection.model.Prediction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EmotionRepository {
    private Database database;

    public EmotionRepository(Context context) {
        this.database = Database.getInstance(context);
    }

    public void initEmotionData() {
        for (String emotion : Emotion.emotions) {
            try {
                this.getOneMotionByName(emotion);
            } catch (Resources.NotFoundException e) {
                ContentValues data = new ContentValues();
                data.put("nama", emotion);
                data.put("total", 0);
                this.database.getDb().insert("emotion", null, data);
            }
        }
    }

    public ArrayList<Emotion> getAllEmotion() {
        ArrayList<Emotion> emotions = new ArrayList<>();
        Cursor cursor = database.getDb().rawQuery("select id, nama, total from emotion", null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = Integer.parseInt(cursor.getString(0));
                String nama = cursor.getString(1);
                int total = Integer.parseInt(cursor.getString(2));
                emotions.add(new Emotion(id, nama, total));
            }
        }
        return emotions;
    }

    public ArrayList<Emotion> getEmotionIdAndName() {
        ArrayList<Emotion> emotions = new ArrayList<>();
        Cursor cursor = database.getDb().rawQuery("select id, nama from emotion", null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = Integer.parseInt(cursor.getString(0));
                String nama = cursor.getString(1);
                int total = 0;
                emotions.add(new Emotion(id, nama, total));
            }
        }
        return emotions;
    }

    public Emotion getOneMotionByName(String name) {
        Cursor cursor = database.getDb().rawQuery("select id, nama, total from emotion WHERE nama = '" + name + "';", null);

        if (cursor.getCount() == 0) {
            throw new Resources.NotFoundException();
        }
        cursor.moveToFirst();
        return new Emotion(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
    }

    public Emotion getEmotionById(int id) {
        Cursor cursor = database.getDb().rawQuery("select id, nama, total from emotion WHERE id = " + id + ";", null);

        if (cursor.getCount() == 0) {
            throw new Resources.NotFoundException();
        }
        cursor.moveToFirst();
        return new Emotion(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
    }

    public void incrementTotalEmotion(Emotion emotion) {
        ContentValues data = new ContentValues();
        data.put("total", emotion.getTotal() + 1);
        database.getDb().update("emotion", data, "id = " + emotion.getId(), null);
    }

    public void insertDetect(Prediction prediction, String filename) {
        ContentValues data = new ContentValues();
        Emotion emotion = getOneMotionByName(prediction.getLabel());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timestamp = dateFormat.format(new Date());

        Detect detect = new Detect(emotion, prediction.getProbability(), timestamp, filename);
        data.put("id_emotion", detect.getEmotion().getId());
        data.put("probability", detect.getProbability());
        data.put("timestamp", detect.getTimestamp());
        data.put("filename", detect.getFilename());

        this.database.getDb().insert("detect", null, data);
        this.incrementTotalEmotion(emotion);
    }

    public ArrayList<Detect> getHistory(boolean limitToday) {
        ArrayList<Detect> detects = new ArrayList<>();
        String orderClause = "order by cast(substr(timestamp,7,4) as int) desc, cast(substr(timestamp,4,2) as int) desc, cast(substr(timestamp,1,2) as int) desc, cast(substr(timestamp,12,2) as int) desc, cast(substr(timestamp,15,2) as int) desc, cast(substr(timestamp,18,2) as int) desc";
        String where = "";

        if(limitToday){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String todayDate = dateFormat.format(new Date());
            where = "where substr(timestamp, 1,10)='" + todayDate + "' ";
        }

        Cursor cursor = database.getDb().rawQuery("select id, id_emotion, probability, timestamp, filename from detect " + where + orderClause, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                System.out.println(cursor.toString());
                int id = Integer.parseInt(cursor.getString(0));
                int idEmotion = Integer.parseInt(cursor.getString(1));
                Emotion emotion = getEmotionById(idEmotion);
                float probability = Float.parseFloat(cursor.getString(2));
                String timestamp = cursor.getString(3);
                String filename = cursor.getString(4);
                detects.add(new Detect(id, emotion, probability, timestamp, filename));
            }
        }
        return detects;
    }

    public ArrayList<Emotion> getTodayEmotionSummary() {
        ArrayList<Detect> detects = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String todayDate = dateFormat.format(new Date());
        Cursor cursor = database.getDb().rawQuery("select id, id_emotion, probability, timestamp, filename from detect where substr(timestamp, 1,10)='" + todayDate + "'", null);

        ArrayList<Emotion> emotions = getEmotionIdAndName();

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                System.out.println(cursor.toString());
                int id = Integer.parseInt(cursor.getString(0));
                int idEmotion = Integer.parseInt(cursor.getString(1));
                Emotion emotion = getEmotionById(idEmotion);
                float probability = Float.parseFloat(cursor.getString(2));
                String timestamp = cursor.getString(3);
                String filename = cursor.getString(4);

                Detect detect = new Detect(id, emotion, probability, timestamp, filename);
                for (Emotion e: emotions){
                    if(e.getId() == detect.getEmotion().getId()){
                        e.setTotal(e.getTotal() + 1);
                    }
                }
            }
        }
        return emotions;
    }


}
