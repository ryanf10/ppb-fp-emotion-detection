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

    public void initEmotionData(){
        for (String emotion: Emotion.emotions) {
            try{
                this.getOneDataByName(emotion);
            }catch (Resources.NotFoundException e){
                ContentValues data = new ContentValues();
                data.put("nama", emotion);
                data.put("total", 0);
                System.out.println("Insert emotion " + emotion);
                this.database.getDb().insert("emotion", null, data);
            }
        }
    }

    public ArrayList<Emotion> getAll(){
        ArrayList<Emotion> emotions = new ArrayList<>();
        Cursor cursor = database.getDb().rawQuery("select id, nama, total from emotion", null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                System.out.println(cursor.toString());
                int id = Integer.parseInt(cursor.getString(0));
                String nama = cursor.getString(1);
                int total = Integer.parseInt(cursor.getString(2));
                emotions.add(new Emotion(id, nama, total));
            }
        }
        return emotions;
    }

    public Emotion getOneDataByName(String name){
        Cursor cursor = database.getDb().rawQuery("select id, nama, total from emotion WHERE nama = '" + name + "';", null);

        if (cursor.getCount() == 0) {
            throw new Resources.NotFoundException();
        }
        cursor.moveToFirst();
        return new Emotion(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
    }

    public void insertDetect(Prediction prediction){
        ContentValues data = new ContentValues();
        Emotion emotion = getOneDataByName(prediction.getLabel());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timestamp  = dateFormat.format(new Date());

        Detect detect = new Detect(emotion.getId(), prediction.getProbability(), timestamp);
        data.put("id_emotion", detect.getIdEmotion());
        data.put("probability", detect.getProbability());
        data.put("timestamp", detect.getTimestamp());

        this.database.getDb().insert("detect", null, data);
    }

    public ArrayList<Detect> getAllDetect(){
        ArrayList<Detect> detects = new ArrayList<>();
        Cursor cursor = database.getDb().rawQuery("select id, id_emotion, probability, timestamp from detect", null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                System.out.println(cursor.toString());
                int id = Integer.parseInt(cursor.getString(0));
                int idEmotion = Integer.parseInt(cursor.getString(1));
                float probability = Float.parseFloat(cursor.getString(2));
                String timestamp = cursor.getString(3);
                detects.add(new Detect(id, idEmotion, probability, timestamp));
            }
        }
        return detects;
    }


}
