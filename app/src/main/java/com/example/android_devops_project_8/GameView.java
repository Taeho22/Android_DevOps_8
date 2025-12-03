package com.example.android_devops_project_8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class GameView extends View {

    public ArrayList<Note> notes = new ArrayList<>();
    public long startTime = 0;

    public float noteSpeed = 0.40f;   // px per ms (Activity에서 난이도에 맞게 조절)

    Paint notePaint = new Paint();
    Paint judgeLinePaint = new Paint();

    public interface OnNoteJudgeListener {
        void onPerfect(Note n);
        void onGood(Note n);
        void onMiss(Note n);
    }

    public OnNoteJudgeListener judgeListener;


    public GameView(Context context) { super(context); init(); }
    public GameView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public GameView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        notePaint.setColor(Color.WHITE);
        judgeLinePaint.setColor(Color.YELLOW);
        judgeLinePaint.setStrokeWidth(5);
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public void setStartTime(long t) {
        this.startTime = t;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (notes == null || startTime == 0) {
            invalidate();
            return;
        }

        long now = System.currentTimeMillis();
        float judgeY = getHeight() - 180;

        // 판정선 그리기
        canvas.drawLine(0, judgeY, getWidth(), judgeY, judgeLinePaint);

        float laneWidth = getWidth() / 4f;
        float noteHeight = 90f;

        long elapsed = now - startTime;

        for (Note n : notes) {

            if (n.judged) continue;

            long remain = n.time - elapsed;  // 도착까지 남은 시간(ms)

            // y 위치 계산
            float y = judgeY - (remain * noteSpeed);

            // 화면 밖 패스
            if (y < -200 || y > getHeight() + 200) continue;

            // 노트 그리기
            RectF rect = new RectF(
                    n.lane * laneWidth + 20,
                    y,
                    (n.lane + 1) * laneWidth - 20,
                    y + noteHeight
            );

            canvas.drawRect(rect, notePaint);

            // 판정 확인
            float diff = Math.abs(y - judgeY);

            if (remain < -150) {  // 완전히 지나침 = MISS
                n.judged = true;
                if (judgeListener != null) judgeListener.onMiss(n);
            }
        }

        invalidate();
    }
}
