package com.example.android_devops_project_8;

public class HPManager {
    public int hp = 100;

    public void damage(int v) {
        hp -= v;
        if (hp < 0) hp = 0;
    }
}
