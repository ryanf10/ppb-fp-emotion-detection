package com.example.emotiondetection.model;

public class Detect {
    private int id;
    private int idEmotion;
    private float probability;
    private String timestamp;

    public Detect(int id, int idEmotion, float probability, String timestamp) {
        this.id = id;
        this.idEmotion = idEmotion;
        this.probability = probability;
        this.timestamp = timestamp;
    }

    public Detect(int idEmotion, float probability, String timestamp) {
        this.idEmotion = idEmotion;
        this.probability = probability;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getIdEmotion() {
        return idEmotion;
    }

    public float getProbability() {
        return probability;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
