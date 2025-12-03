package com.example.android_devops_project_8;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private boolean running = false;
    private SurfaceHolder holder;
    private GameView view;

    public GameThread(SurfaceHolder h, GameView v){
        this.holder = h;
        this.view = v;
    }

    public void setRunning(boolean r){ this.running = r; }

    @Override
    public void run(){
        long targetMs = 1000/60; // 60fps
        while(running){
            long t0 = System.currentTimeMillis();
            Canvas c = null;
            try{
                c = holder.lockCanvas();
                synchronized(holder){
                    if (c!=null) view.draw(c);
                }
            }catch(Exception ignored){}
            finally{
                if(c!=null) holder.unlockCanvasAndPost(c);
            }
            long dt = System.currentTimeMillis()-t0;
            long sleep = targetMs - dt;
            if(sleep>0){
                try{ Thread.sleep(sleep); } catch (InterruptedException e){}
            }
        }
    }
}
