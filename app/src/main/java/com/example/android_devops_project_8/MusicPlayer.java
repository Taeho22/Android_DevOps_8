package com.example.android_devops_project_8;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicPlayer {
    private MediaPlayer player;
    private boolean prepared = false;

    public MusicPlayer(Context ctx, int resRawId){
        player = MediaPlayer.create(ctx, resRawId);
        if (player != null) prepared = true;
    }

    public void start(){
        if (player != null && prepared) player.start();
    }

    public void stop(){
        if (player != null) player.stop();
    }

    public int getPosition(){
        if (player==null) return 0;
        return player.getCurrentPosition();
    }

    public boolean isPlaying(){
        return player!=null && player.isPlaying();
    }

    public int getDuration(){
        if (player==null) return 0;
        return player.getDuration();
    }
}
