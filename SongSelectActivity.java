package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 곡 선택 + 난이도 + 점수/등급 표시 + 미리듣기 + 게임 시작까지 담당하는 액티비티
 * 실제 "게임 플레이 화면"은 다른 팀원이 GameActivity로 구현한다는 가정.
 */
public class SongSelectActivity extends AppCompatActivity {

    // --- UI 컴포넌트 ---
    private RecyclerView rvSongs;      // 왼쪽 곡 리스트
    private SongAdapter adapter;       // RecyclerView 어댑터
    private RadioGroup rgDifficulty;   // 난이도 선택 RadioGroup
    private TextView tvTitle;          // 오른쪽 카드 - 곡 제목
    private TextView tvRank;           // 오른쪽 카드 - 등급
    private TextView tvScore;          // 오른쪽 카드 - 점수
    private ImageButton btnPlay;       // 미리듣기 재생/정지 버튼
    private Button btnStartGame;       // 🔥 게임 시작 버튼

    // --- 데이터 ---
    private final List<Song> songs = new ArrayList<>();
    private int selectedIndex = 0;                     // 현재 선택된 곡 인덱스
    private Difficulty difficulty = Difficulty.NORMAL; // 현재 난이도

    // --- 오디오 재생용 ---
    private MediaPlayer player;

    // --- SharedPreferences: 곡별/난이도별 점수 저장용 ---
    private SharedPreferences prefs;
    private static final String PREF_NAME = "song_prefs";

    // --- 화면 회전 대비 키 ---
    private static final String STATE_SEL_INDEX = "state_sel_index";
    private static final String STATE_DIFF      = "state_diff";

    // --- GameActivity 호출용 requestCode ---
    private static final int REQ_GAME = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_select);

        // 1) SharedPreferences 준비
        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // 2) XML에서 View 찾기
        rvSongs      = findViewById(R.id.rv_songs);
        rgDifficulty = findViewById(R.id.rg_difficulty);
        tvTitle      = findViewById(R.id.tv_title);
        tvRank       = findViewById(R.id.tv_rank);
        tvScore      = findViewById(R.id.tv_score);
        btnPlay      = findViewById(R.id.btn_play);
        btnStartGame = findViewById(R.id.btn_start_game); // 🔥 새로 추가한 버튼

        // 3) 곡 데이터 세팅
        seedSongs();

        // 4) 이전에 선택했던 곡/난이도(있다면) 복원
        restoreSelectionBasic();  // 기본 선택(곡/난이도)만 복원

        // 5) RecyclerView, Adapter 설정
        adapter = new SongAdapter(songs, pos -> {
            selectedIndex = pos;
            adapter.setSelectedPos(pos);
            updatePanel(false);  // 오른쪽 패널 갱신 (점수/등급 포함)
        });
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvSongs.setAdapter(adapter);
        adapter.setSelectedPos(selectedIndex);

        // 6) 난이도 라디오 초기 선택
        selectDifficulty(difficulty);

        // 7) 게임 시작 버튼 클릭 시: GameActivity로 넘어가도록 설계
        btnStartGame.setOnClickListener(v -> {
            // 선택된 곡과 난이도 정보를 게임 화면으로 전달
            Song s = songs.get(selectedIndex);

            Intent intent = new Intent(this, GameActivity.class); // ✅ 팀원이 만들 GameActivity
            intent.putExtra("song_id", s.id);
            intent.putExtra("song_title", s.title);
            intent.putExtra("difficulty", difficulty.name());

            // 게임 결과(점수)를 받아오기 위해 startActivityForResult 사용
            startActivityForResult(intent, REQ_GAME);
        });

        // 8) 난이도 변경 리스너
        rgDifficulty.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_easy)   difficulty = Difficulty.EASY;
            if (checkedId == R.id.rb_normal) difficulty = Difficulty.NORMAL;
            if (checkedId == R.id.rb_hard)   difficulty = Difficulty.HARD;

            // 난이도가 바뀌었으니,
            // 해당 곡 + 난이도의 점수/등급을 다시 불러와서 화면에 반영
            updatePanel(false);
        });

        // 9) 미리듣기 버튼 (이전과 동일)
        btnPlay.setOnClickListener(v -> {
            if (player != null && player.isPlaying()) {
                player.pause();
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
            } else {
                startPreview();
            }
        });

        // 10) 회전 복원 처리
        if (savedInstanceState != null) {
            selectedIndex = savedInstanceState.getInt(STATE_SEL_INDEX, selectedIndex);
            String diffName = savedInstanceState.getString(STATE_DIFF, difficulty.name());
            difficulty = Difficulty.valueOf(diffName);
            adapter.setSelectedPos(selectedIndex);
            selectDifficulty(difficulty);
        }

        // 11) 최종 패널 업데이트 (선택/난이도/점수/등급 반영)
        updatePanel(true);
    }

    /**
     * 곡 목록 초기 데이터 셋업
     */
    private void seedSongs() {
        songs.clear();
        songs.add(new Song("s1", "1",      0)); // baseScore는 이제 안 씀, 0으로 둬도 됨
        songs.add(new Song("s2", "2",        0));
        songs.add(new Song("s3", "3", 0));
        songs.add(new Song("s4", "4",   0));
        songs.add(new Song("s5", "5",           0));
    }

    /**
     * 곡/난이도 기본 선택만 SharedPreferences에서 복원
     * (점수는 나중에 updatePanel에서 따로 불러옴)
     */
    private void restoreSelectionBasic() {
        // 기본은 첫 번째 곡 + NORMAL 난이도
        String defaultId = songs.get(0).id;
        String savedId   = prefs.getString("last_song_id", defaultId);
        String savedDf   = prefs.getString("last_diff", Difficulty.NORMAL.name());
        difficulty = Difficulty.valueOf(savedDf);

        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).id.equals(savedId)) {
                selectedIndex = i;
                break;
            }
        }
    }

    /**
     * 현재 선택된 곡/난이도만 따로 저장
     */
    private void saveSelectionBasic() {
        Song s = songs.get(selectedIndex);
        prefs.edit()
                .putString("last_song_id", s.id)
                .putString("last_diff", difficulty.name())
                .apply();
    }

    /**
     * 난이도 Enum → 해당 RadioButton 체크
     */
    private void selectDifficulty(Difficulty d) {
        @IdRes int id = R.id.rb_normal;
        if (d == Difficulty.EASY)  id = R.id.rb_easy;
        if (d == Difficulty.HARD)  id = R.id.rb_hard;
        rgDifficulty.check(id);
    }

    /**
     * 오른쪽 상세 패널 갱신
     * - 곡 이름 표시
     * - 현재 곡 + 난이도 조합의 점수/등급 불러와서 보여줌
     */
    private void updatePanel(boolean initial) {
        Song s = songs.get(selectedIndex);

        // 현재 곡 + 난이도에 대한 저장된 점수 가져오기 (없으면 0)
        int score = loadScore(s.id, difficulty);

        // 곡 제목
        tvTitle.setText(s.title);

        // 점수 출력 (콤마 포함)
        tvScore.setText("점수: " + String.format("%,d", score));

        // 점수가 0이면 등급은 E
        String grade;
        if (score <= 0) {
            grade = "E";
        } else {
            grade = gradeFromScore(score);
        }

        // 등급 텍스트 + 색상
        tvRank.setText(grade);
        tvRank.setTextColor(getColorForGrade(grade));

        // 마지막 선택 상태 저장 (곡/난이도)
        saveSelectionBasic();

        // 리스트 쪽 스크롤 (최초 초기화일 땐 굳이 안 움직여도 됨)
        if (!initial) {
            rvSongs.scrollToPosition(selectedIndex);
        }
    }

    /**
     * 곡 id + 난이도로 유니크한 키 생성
     * 예: score_s1_NORMAL
     */
    private String scoreKey(String songId, Difficulty diff) {
        return "score_" + songId + "_" + diff.name();
    }

    /**
     * SharedPreferences에서 점수 읽기 (없으면 0)
     */
    private int loadScore(String songId, Difficulty diff) {
        return prefs.getInt(scoreKey(songId, diff), 0);
    }

    /**
     * SharedPreferences에 점수 저장
     */
    private void saveScore(String songId, Difficulty diff, int score) {
        prefs.edit()
                .putInt(scoreKey(songId, diff), score)
                .apply();
    }

    /**
     * 점수를 등급 문자열로 변환
     * (0점은 여기 오기 전에 E로 처리)
     */
    private String gradeFromScore(int score) {
        if (score >= 990_000) return "SSS";
        if (score >= 970_000) return "SS";
        if (score >= 940_000) return "S";
        if (score >= 900_000) return "A";
        if (score >= 850_000) return "B";
        if (score >= 800_000) return "C";
        return "D";
    }

    /**
     * 등급에 따른 색상 반환
     */
    private int getColorForGrade(String grade) {
        switch (grade) {
            case "SSS":
                return ContextCompat.getColor(this, R.color.grade_sss);
            case "SS":
                return ContextCompat.getColor(this, R.color.grade_ss);
            case "S":
                return ContextCompat.getColor(this, R.color.grade_s);
            case "A":
                return ContextCompat.getColor(this, R.color.grade_a);
            case "B":
                return ContextCompat.getColor(this, R.color.grade_b);
            case "C":
                return ContextCompat.getColor(this, R.color.grade_c);
            case "D":
                return ContextCompat.getColor(this, R.color.grade_d);
            case "E":
            default:
                // E는 제일 낮은 등급 → 살짝 흐린 색으로
                return ContextCompat.getColor(this, R.color.grade_d);
        }
    }

    /**
     * 현재 선택된 곡의 미리듣기 재생
     * (점수랑은 별개, 단순 BGM 프리뷰용)
     */
    private void startPreview() {
        stopPreview();

        Song s = songs.get(selectedIndex);
        int resId = getPreviewResIdBySongId(s.id);
        if (resId == 0) return;

        player = MediaPlayer.create(this, resId);
        if (player == null) return;

        player.setOnCompletionListener(mp ->
                btnPlay.setImageResource(android.R.drawable.ic_media_play));
        player.start();
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
    }

    /**
     * 미리듣기 정지 및 리소스 해제
     */
    private void stopPreview() {
        if (player != null) {
            try {
                if (player.isPlaying()) player.stop();
            } catch (Exception ignored) {}
            player.release();
            player = null;
        }
        btnPlay.setImageResource(android.R.drawable.ic_media_play);
    }

    /**
     * 곡 id → raw 리소스 id 매핑
     * (팀원이 넣어줄 실제 mp3 파일 이름과 맞춰야 함)
     */
    private int getPreviewResIdBySongId(String id) {
        switch (id) {
            case "s1": return R.raw.s1_preview;
            case "s2": return R.raw.s2_preview;
            case "s3": return R.raw.s3_preview;
            case "s4": return R.raw.s4_preview;
            case "s5": return R.raw.s5_preview;
            default:   return 0;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPreview();
    }

    /**
     * 화면 회전 전에 상태 저장
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SEL_INDEX, selectedIndex);
        outState.putString(STATE_DIFF, difficulty.name());
    }

    /**
     * 화면 회전 후 상태 복원
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedIndex = savedInstanceState.getInt(STATE_SEL_INDEX, selectedIndex);
        String diffName = savedInstanceState.getString(STATE_DIFF, difficulty.name());
        difficulty = Difficulty.valueOf(diffName);

        adapter.setSelectedPos(selectedIndex);
        selectDifficulty(difficulty);
        updatePanel(true);
    }

    /**
     * GameActivity에서 결과를 받고 돌아왔을 때 호출됨
     * - 약속: GameActivity는 resultCode = RESULT_OK,
     *         data에 "score" (int)를 넣어서 돌려준다.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_GAME && resultCode == RESULT_OK && data != null) {
            // 게임 결과 점수 받아오기 (없으면 0)
            int newScore = data.getIntExtra("score", 0);

            // 현재 선택된 곡 + 난이도에 대해 저장
            Song s = songs.get(selectedIndex);
            saveScore(s.id, difficulty, newScore);

            // 패널 갱신 (점수/등급 재계산)
            updatePanel(false);
        }
    }
}