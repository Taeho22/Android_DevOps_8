package com.example.android_devops_project_8;

import android.content.Context;
import android.content.res.AssetManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;

public class NoteLoader {

    public static ArrayList<Note> loadFromAssets(Context ctx, String filename, GameConfig.Difficulty diff, int songLengthMs) {
        try {
            AssetManager am = ctx.getAssets();
            InputStream is = am.open(filename);
            byte[] b = new byte[is.available()];
            is.read(b);
            is.close();
            String json = new String(b,"UTF-8");
            JSONObject root = new JSONObject(json);

            JSONArray arr = root.getJSONArray("notes");
            ArrayList<Note> list = new ArrayList<>();
            for (int i=0;i<arr.length();i++){
                JSONObject o = arr.getJSONObject(i);
                Note n = new Note(o.getLong("time"), o.getInt("lane"), o.getString("type"));
                if(o.has("longId")) n.longId = o.getInt("longId");
                list.add(n);
            }
            return list;

        } catch (Exception e){
            // assets 파일이 없거나 파싱 에러면 자동 생성
            return NoteGenerator.generate(diff, songLengthMs);
        }
    }
}
