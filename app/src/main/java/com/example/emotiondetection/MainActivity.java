package com.example.emotiondetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.emotiondetection.model.Detect;
import com.example.emotiondetection.repository.EmotionRepository;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EmotionRepository emotionRepository;
    private static boolean isChecked = false;

    private void check(){
        System.out.println("masuk");
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

        ArrayList<Detect> detects = emotionRepository.getAllDetect();
        for (Detect detect: detects){
            System.out.println(detect.getId() + " " + detect.getIdEmotion() + " " + detect.getProbability() + " " + detect.getTimestamp());
        }
    }

    public void openCamera(View v){
        Intent cameraIntent = new Intent(this, CameraActivity.class);
        this.startActivity(cameraIntent);
    }
}