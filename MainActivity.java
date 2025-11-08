package com.example.android_devops_8;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnStart, btnSetting, btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart   = findViewById(R.id.btnStart);
        btnSetting = findViewById(R.id.btnSetting);
        btnChange  = findViewById(R.id.btnChange);

        // 시작 버튼 → StartActivity
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
                // 화면 전환 애니메이션 (선택)
                // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // 설정 버튼 → SettingsActivity
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // 전환 버튼 → ChangeActivity
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChangeActivity.class);
                startActivity(intent);
            }
        });
    }
}
