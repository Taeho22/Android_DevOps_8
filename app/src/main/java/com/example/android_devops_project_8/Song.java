package com.example.android_devops_project_8;

/**
 * 곡 정보 클래스
 * id: 내부 식별용
 * title: 곡 이름
 * baseScore: 난이도 적용 전 기본 점수(예: 최고 점수)
 */
public class Song {
    public final String id;
    public final String title;
    public final int baseScore;

    public Song(String id, String title, int baseScore) {
        this.id = id;
        this.title = title;
        this.baseScore = baseScore;
    }
}