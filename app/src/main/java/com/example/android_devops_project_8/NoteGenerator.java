package com.example.android_devops_project_8;




import java.util.ArrayList;
import java.util.Random;

public class NoteGenerator {

    // 간단한 규칙 기반 제너레이터
    public static ArrayList<Note> generate(GameConfig.Difficulty diff, int songLengthMs) {
        ArrayList<Note> notes = new ArrayList<>();
        Random rnd = new Random(12345);

        float density; // expected notes per second
        switch (diff) {
            case EASY:
                density = 0.8f;
                break;
            case NORMAL:
                density = 1.6f;
                break;
            default:
                density = 3.0f;
                break;
        }

        int totalSeconds = Math.max(1, songLengthMs / 1000);
        int approxNotes = Math.round(density * totalSeconds);

        // create taps, some simultaneous, a few long notes
        for (int i = 0; i < approxNotes; i++) {
            long t = Math.round((songLengthMs - 2000) * rnd.nextFloat()) + 500; // 500..len-1500
            int lane = rnd.nextInt(4);
            notes.add(new Note(t, lane, "tap"));
            // add simultaneous occasionally
            if (rnd.nextFloat() < 0.08f) {
                int other = rnd.nextInt(4);
                if (other != lane) notes.add(new Note(t, other, "tap"));
            }
            // add long note occasionally
            if (rnd.nextFloat() < (diff == GameConfig.Difficulty.HARD ? 0.12f : 0.05f)) {
                int laneL = rnd.nextInt(4);
                long start = Math.max(200, t - 200 + rnd.nextInt(300));
                long end = Math.min(songLengthMs - 200, start + 500 + rnd.nextInt(1500));
                Note s = new Note(start, laneL, "long_start");
                Note e = new Note(end, laneL, "long_end");
                int id = i + 10000;
                s.longId = id;
                e.longId = id;
                notes.add(s);
                notes.add(e);
            }
        }
        // sort by time
        notes.sort((a, b) -> Long.compare(a.time, b.time));
        return notes;
    }
}

