package com.example.android_devops_project_8;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

public class MP3Player extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TextView txtTime;
    private Handler handler = new Handler();

    private String[] songs = {"candy_land", "luminous_memory"}; // raw 폴더에 mp3 파일 넣기
    private int currentIndex = 0;

    Button btnPrev, btnNext, btnPlayPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        ListView songList = findViewById(R.id.songList);
        seekBar = findViewById(R.id.seekBar);
        txtTime = findViewById(R.id.txtTime);
        btnPrev = (Button)findViewById(R.id.btnPrev);
        btnNext = (Button)findViewById(R.id.btnNext);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, songs);
        songList.setAdapter(adapter);

        // 곡 선택 시 재생
        songList.setOnItemClickListener((parent, view, position, id) -> {
            currentIndex = position;
            playSong(songs[currentIndex]);
        });

        btnPlayPause = (Button)findViewById(R.id.btnPlayPause);

        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    // 현재 재생 중이면 → 일시정지
                    mediaPlayer.pause();
                    btnPlayPause.setText("시작"); // 버튼 텍스트 변경
                } else {
                    // 현재 멈춰있으면 → 재생 시작
                    mediaPlayer.start();
                    updateSeekBar();
                    btnPlayPause.setText("일시정지"); // 버튼 텍스트 변경
                }
            }
        });

        // 이전곡 버튼
        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                playSong(songs[currentIndex]);
            }
            else if(currentIndex == 0){
                currentIndex = songs.length-1;
                playSong(songs[currentIndex]);
            }
        });

        // 다음곡 버튼
        btnNext.setOnClickListener(v -> {
            if (currentIndex < songs.length - 1) {
                currentIndex++;
                playSong(songs[currentIndex]);
            } else if (currentIndex == songs.length - 1) {
                currentIndex = 0;
                playSong(songs[currentIndex]);
            }
        });
    }

    private void playSong(String songName) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        int resId = getResources().getIdentifier(songName, "raw", getPackageName());
        mediaPlayer = MediaPlayer.create(this, resId);

        seekBar.setMax(mediaPlayer.getDuration());
        txtTime.setText("00:00 / " + formatTime(mediaPlayer.getDuration()));

        mediaPlayer.start();
        updateSeekBar();

        // 곡이 끝나면 자동으로 다음 곡 재생
        mediaPlayer.setOnCompletionListener(mp -> {
            if (currentIndex < songs.length - 1) {
                currentIndex++;
                playSong(songs[currentIndex]);
            }
        });
    }

    private void updateSeekBar() {
        if (mediaPlayer != null) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            txtTime.setText(formatTime(mediaPlayer.getCurrentPosition()) + " / " + formatTime(mediaPlayer.getDuration()));

            if (mediaPlayer.isPlaying()) {
                handler.postDelayed(this::updateSeekBar, 1000);
            }
        }
    }

    private String formatTime(int millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
