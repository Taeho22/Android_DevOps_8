package com.example.android_devops_project_8;

import static android.view.View.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;


public class Main extends AppCompatActivity {

    Button StartBtn,MPBtn,SetBtn;

    Button closeBtn;
    ImageButton charBtn;
    LinearLayout settingBox;
    TextView text;
    LinearLayout textBox;

    public static Player player;
    SwitchCompat switchTheme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        switchTheme = (SwitchCompat) findViewById(R.id.switchTheme);

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });


        // 플레이어 생성
        player = new Player();

        // 메인 페이지 버튼 설정
        StartBtn = (Button) findViewById(R.id.btnStart);
        SetBtn = (Button) findViewById(R.id.btnSetting);
        MPBtn = (Button) findViewById(R.id.btnMP);
        settingBox = (LinearLayout)findViewById(R.id.settingBox);
        closeBtn = (Button)findViewById(R.id.btnClose);
        charBtn = (ImageButton)findViewById(R.id.CharImgBtn);

        text = (TextView)findViewById(R.id.tvSpeech) ;
        textBox = (LinearLayout)findViewById(R.id.TextBox);

        // 시작 버튼
        StartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SelectMusicPage.class);
                startActivity(intent);
            }
        });

        // MusicPlayer 버튼
        MPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MP3Player.class);
                startActivity(intent);

            }
        });

        // 설정 버튼
        SetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingBox.setVisibility(VISIBLE);
            }
        });
        closeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                settingBox.setVisibility(INVISIBLE);
            }
        });

        charBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                textBox.setVisibility(VISIBLE);
                //text.setText("안녕?");
                setText();
            }
        });
    }
    public void setText(){
        Random random = new Random();
        int n = random.nextInt(4);
        if(n == 0) text.setText("안녕?");
        else if (n == 1) text.setText("만나서 반가워.");
        else if (n == 2) text.setText("또 만났네.");
        else text.setText("오늘도 잘 부탁해.");

    }


}
