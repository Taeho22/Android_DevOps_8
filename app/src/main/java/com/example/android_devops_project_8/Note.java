package com.example.android_devops_project_8;

public class Note {
    public long time;       // 판정 기준 시간(ms)
    public int lane;        // 0~3
    public String type;     // "tap", "long_start", "long_end"
    public int longId = -1; // long note 그룹 id

    // runtime
    public boolean judged = false;

    public Note(long time, int lane, String type) {
        this.time = time;
        this.lane = lane;
        this.type = type;
    }
}
