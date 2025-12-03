package com.example.android_devops_project_8;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView 어댑터
 * 곡 목록을 화면에 표시, 선택된 곡은 파란색 보더로 표시됨
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.VH> {

    /** 아이템 클릭 콜백 */
    public interface OnItemClick {
        void onClick(int position);
    }

    private final List<Song> data;
    private final OnItemClick listener;

    /** 현재 선택된 아이템 위치 */
    private int selectedPos = RecyclerView.NO_POSITION;

    public SongAdapter(List<Song> data, OnItemClick listener) {
        this.data = data;
        this.listener = listener;
    }

    /** 외부에서 선택 변경 */
    public void setSelectedPos(int pos) {
        int old = selectedPos;
        selectedPos = pos;

        // 오래된 선택, 새 선택 둘 다 갱신
        if (old != RecyclerView.NO_POSITION) notifyItemChanged(old);
        if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_song.xml inflate
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Song s = data.get(position);

        // 곡 이름 설정
        holder.title.setText(s.title);

        // 선택 상태 반영 → item_song_bg.xml가 자동 적용됨
        holder.itemView.setActivated(position == selectedPos);

        // 클릭하면 콜백 호출
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /** 뷰홀더: 각 아이템의 View 참조 */
    static class VH extends RecyclerView.ViewHolder {
        TextView title;

        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_item_title);
        }
    }
}