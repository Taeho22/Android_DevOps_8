package com.example.android_devops_project_8;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

public class GamePage extends AppCompatActivity {

    GameView gameView;
    MediaPlayer player;

    ArrayList<Note> notes;

    int hp = 100;

    Button btnA, btnB, btnC, btnD;
    View pauseOverlay;

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.game_page);

//        // Intent에서 값 받기
//        String songPath = getIntent().getStringExtra("songPath");
//        GameConfig.Difficulty diff =
//                (GameConfig.Difficulty) getIntent().getSerializableExtra("difficulty");
//
        //int songLengthMs = getIntent().getIntExtra("songLength", 60000);
        // Intent에서 값 받기
        String songPath = "R.raw.luminous_memory";
        GameConfig.Difficulty diff =GameConfig.Difficulty.EASY;

        int songLengthMs = getIntent().getIntExtra("songLength", 60000);

        LinearLayout setBox = (LinearLayout)findViewById(R.id.stopBox);
        Button stopBtn = (Button)findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBox.setVisibility(VISIBLE);
            }
        });

        Button endBtn = (Button) findViewById(R.id.btnEndGame);
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GamePage.this, EndPage.class);
                intent.putExtra("song_name", "");
                intent.putExtra("score", 0);
                intent.putExtra("difficulty","EASY");;
                intent.putExtra("grade","D");
                // 게임 결과(점수)를 받아오기 위해 startActivityForResult 사용
                startActivity(intent);
            }
        });

        gameView = findViewById(R.id.gameView);

        btnA = findViewById(R.id.btnA);
        btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC);
        btnD = findViewById(R.id.btnD);

        //pauseOverlay = findViewById(R.id.pauseOverlay);

        notes = NoteGenerator.generate(diff, songLengthMs);
        gameView.setNotes(notes);

        player = new MediaPlayer();
        try {
            player.setDataSource(songPath);
            player.prepare();
        } catch (IOException e) {}

        gameView.startTime = System.currentTimeMillis();

        player.start();

        setButtonEvents();

        gameView.judgeListener = new GameView.OnNoteJudgeListener() {
            @Override public void onPerfect(Note n) {}
            @Override public void onGood(Note n) {}
            @Override public void onMiss(Note n) {
                hp -= 10;
                if (hp <= 0) endGame();
            }
        };
    }

    void setButtonEvents() {
        btnA.setOnClickListener(v -> judgeLane(0));
        btnB.setOnClickListener(v -> judgeLane(1));
        btnC.setOnClickListener(v -> judgeLane(2));
        btnD.setOnClickListener(v -> judgeLane(3));
    }

    void judgeLane(int lane) {
        long now = System.currentTimeMillis() - gameView.startTime;

        for (Note n : notes) {
            if (n.judged || n.lane != lane) continue;

            long diff = Math.abs(n.time - now);

            if (diff <= 80) {
                n.judged = true;
                gameView.judgeListener.onPerfect(n);
                return;
            } else if (diff <= 150) {
                n.judged = true;
                gameView.judgeListener.onGood(n);
                return;
            }
        }
    }

    void endGame() {
        player.stop();
        finish();
    }

    public void onPauseClick(View v) {
        pauseOverlay.setVisibility(View.VISIBLE);
        player.pause();
    }

    public void onResumeClick(View v) {
        pauseOverlay.setVisibility(View.GONE);
        player.start();
    }

    public void onExitClick(View v) {
        finish();
    }

    public void onMainClick(View v) {
        Intent i = new Intent(this, Main.class);
        startActivity(i);
        finish();
    }
}
