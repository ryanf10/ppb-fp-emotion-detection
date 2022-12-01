package com.example.emotiondetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.emotiondetection.model.Detect;
import com.example.emotiondetection.model.Emotion;
import com.example.emotiondetection.repository.EmotionRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllTimeStatisticActivity extends AppCompatActivity {
    private EmotionRepository emotionRepository;
    private ListView listHistory;
    private ArrayList<HashMap<String, String>> detects;
    private AnyChartView pieChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_time_statistic);

        this.emotionRepository = new EmotionRepository(getApplicationContext());
        this.listHistory = findViewById(R.id.listHistory);
        this.detects = new ArrayList<>();

        this.pieChart = (AnyChartView) findViewById(R.id.pieChart);

        this.setPieChart();
        this.setListHistory();


    }

    private void setPieChart(){
        ArrayList<Emotion> emotionsAllTime = emotionRepository.getAllEmotion();
        int total = 0;
        List<DataEntry> dataAllTime = new ArrayList<>();
        for (Emotion emotion: emotionsAllTime){
            total += emotion.getTotal();
            dataAllTime.add(new ValueDataEntry(emotion.getNama(), emotion.getTotal()));
        }
        pieChart.setChart(generatePieChart(dataAllTime, "All Time Visitor Emotion"));
    }


    private Pie generatePieChart(List<DataEntry> data, String title){
        Pie pie = AnyChart.pie();
        pie.data(data);

        pie.title(title);

        pie.labels().position("outside");

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);
        return pie;
    }

    private void setListHistory(){
        ArrayList<Detect> detectData = this.emotionRepository.getHistory(false);
        for (Detect detect : detectData) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put("prediction",  String.format("%s %.2f%%", detect.getEmotion().getNama(),detect.getProbability()));
            temp.put("timestamp", String.valueOf(detect.getTimestamp()));
            detects.add(temp);
        }

        ListAdapter adapter = new SimpleAdapter(AllTimeStatisticActivity.this, detects, R.layout.list_view, new String[]{"prediction", "timestamp"}, new int[]{R.id.prediction, R.id.timestamp});
        listHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "EXAMPLE_EMOTION_DETECTION");
                intent.setDataAndType(Uri.parse(mediaStorageDir.getPath()  + File.separator + detectData.get(i).getFilename()), "image/*");
                startActivity(intent);
            }
        });
        listHistory.setAdapter(adapter);
    }
}