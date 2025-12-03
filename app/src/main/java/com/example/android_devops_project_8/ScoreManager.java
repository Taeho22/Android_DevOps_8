package com.example.android_devops_project_8;

public class ScoreManager {

    public int score = 0;
    public int perfectCount = 0;

    public void addPerfect() {
        score += 1000;
        perfectCount++;
    }

    public void addGreat() {
        score += 600;
    }

    public void addGood() {
        score += 300;
    }
}
