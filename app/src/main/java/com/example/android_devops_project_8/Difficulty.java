package com.example.android_devops_project_8;

/**
 * 난이도 Enum
 * multiplier: 점수 가중치
 * label: UI 표시용
 */
public enum Difficulty {
    EASY(0.9f, "Easy"),
    NORMAL(1.0f, "Normal"),
    HARD(1.1f, "Hard");

    public final float multiplier;
    public final String label;

    Difficulty(float multiplier, String label) {
        this.multiplier = multiplier;
        this.label = label;
    }
}