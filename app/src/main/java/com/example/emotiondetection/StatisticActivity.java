package com.example.emotiondetection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.emotiondetection.model.Detect;
import com.example.emotiondetection.repository.EmotionRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class StatisticActivity extends AppCompatActivity {
    private EmotionRepository emotionRepository;
    private ListView listHistory;
    private ArrayList<HashMap<String, String>> detects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        this.emotionRepository = new EmotionRepository(getApplicationContext());
        this.listHistory = findViewById(R.id.listHistory);
        this.detects = new ArrayList<>();


        ArrayList<Detect> detectData = this.emotionRepository.getAllDetect();
        for (Detect detect : detectData) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put("prediction",  String.format("%s %.2f%%", detect.getEmotion().getNama(),detect.getProbability()));
            temp.put("timestamp", String.valueOf(detect.getTimestamp()));
            detects.add(temp);
        }

        ListAdapter adapter = new SimpleAdapter(StatisticActivity.this, detects, R.layout.list_view, new String[]{"prediction", "timestamp"}, new int[]{R.id.prediction, R.id.timestamp});
        listHistory.setAdapter(adapter);
    }
}