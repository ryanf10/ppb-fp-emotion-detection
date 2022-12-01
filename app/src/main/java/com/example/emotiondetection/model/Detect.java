package com.example.emotiondetection.model;

public class Detect {
    private int id;
    private Emotion emotion;
    private float probability;
    private String timestamp;
    private String filename;

    public Detect(int id, Emotion emotion, float probability, String timestamp, String filename) {
        this.id = id;
        this.emotion = emotion;
        this.probability = probability;
        this.timestamp = timestamp;
        this.filename = filename;
    }

    public Detect(Emotion emotion, float probability, String timestamp, String filename) {
        this.emotion = emotion;
        this.probability = probability;
        this.timestamp = timestamp;
        this.filename = filename;
    }

    public int getId() {
        return id;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public float getProbability() {
        return probability;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getFilename() {
        return filename;
    }
}
