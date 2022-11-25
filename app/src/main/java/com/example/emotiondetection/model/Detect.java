package com.example.emotiondetection.model;

public class Detect {
    private int id;
    private Emotion emotion;
    private float probability;
    private String timestamp;

    public Detect(int id, Emotion emotion, float probability, String timestamp) {
        this.id = id;
        this.emotion = emotion;
        this.probability = probability;
        this.timestamp = timestamp;
    }

    public Detect(Emotion emotion, float probability, String timestamp) {
        this.emotion = emotion;
        this.probability = probability;
        this.timestamp = timestamp;
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
}
