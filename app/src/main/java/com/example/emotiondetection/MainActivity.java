package com.example.emotiondetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.emotiondetection.repository.EmotionRepository;


public class MainActivity extends AppCompatActivity {
    private EmotionRepository emotionRepository;
    private static boolean isChecked = false;

    private void check(){
        emotionRepository.initEmotionData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.emotionRepository = new EmotionRepository(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isChecked){
            isChecked = true;
            this.check();
        }

    }

    public void openCamera(View v){
        Intent cameraIntent = new Intent(this, CameraActivity.class);
        this.startActivity(cameraIntent);
    }

    public void openAllTimeStatistic(View v){
        Intent cameraIntent = new Intent(this, AllTimeStatisticActivity.class);
        this.startActivity(cameraIntent);
    }

    public void openTodayStatistic(View v){
        Intent cameraIntent = new Intent(this, TodayStatisticActivity.class);
        this.startActivity(cameraIntent);
    }
}