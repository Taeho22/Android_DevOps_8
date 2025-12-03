package com.example.android_devops_project_8;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndActivity extends Activity {

    private TextView tvSongName, tvScore, tvDifficulty, tvGrade;
    private Button btnRestart, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_page);  // ✅ XML 연결

        // 뷰 연결
        tvSongName = findViewById(R.id.tv_song_name);
        tvScore = findViewById(R.id.tv_score);
        tvDifficulty = findViewById(R.id.tv_difficulty);
        tvGrade = findViewById(R.id.tv_grade);
        btnRestart = findViewById(R.id.btn_restart);
        btnBack = findViewById(R.id.btn_back);

        // ✅ 이전 액티비티에서 전달된 데이터 받기
        Intent intent = getIntent();
        String songName = intent.getStringExtra("song_name");
        int score = intent.getIntExtra("score", 0);
        String difficulty = intent.getStringExtra("difficulty");
        String grade = intent.getStringExtra("grade");

        // ✅ UI에 데이터 표시
        tvSongName.setText("곡 이름: " + songName);
        tvScore.setText("획득 점수: " + score + "점");
        tvDifficulty.setText("난이도: " + difficulty);
        tvGrade.setText("등급: " + grade);

        // ✅ 버튼 리스너 설정
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 게임 다시 시작 (예시)
                Intent restartIntent = new Intent(EndActivity.this, GamePage.class);
                startActivity(restartIntent);
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메인 화면으로 돌아가기 (예시)
                Intent backIntent = new Intent(EndActivity.this, Main.class);
                startActivity(backIntent);
                finish();
            }
        });
    }
}
