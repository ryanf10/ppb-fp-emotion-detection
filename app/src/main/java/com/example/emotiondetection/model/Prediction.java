package com.example.emotiondetection.model;

public class Prediction {
    private String label;
    private float probability;

    public Prediction(String label, float probability) {
        this.label = label;
        this.probability = probability;
    }

    public String getLabel() {
        return label;
    }

    public float getProbability() {
        return probability;
    }
}
