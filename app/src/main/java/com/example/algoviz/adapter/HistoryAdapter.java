package com.example.algoviz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.algoviz.R;
import com.example.algoviz.model.LearningRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 학습 기록 화면의 RecyclerView 어댑터.
 * <p>
 * {@link DiffUtil}로 Firestore에서 불러온 목록을 효율적으로 갱신하며,
 * 북마크 버튼 클릭 시 {@link OnBookmarkToggle} 콜백을 통해 상위 액티비티에 이벤트를 전달한다.
 * </p>
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    public interface OnBookmarkToggle {
        void toggle(LearningRecord record, int position);
    }

    private List<LearningRecord> items = new ArrayList<>();
    private OnBookmarkToggle listener;
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);

    public void setListener(OnBookmarkToggle listener) { this.listener = listener; }

    /**
     * 학습 기록 목록을 갱신한다.
     * <p>
     * {@link DiffUtil}을 사용해 변경된 항목만 업데이트하며,
     * 아이디({@code id})와 북마크 상태를 기준으로 동일 여부를 판별한다.
     * </p>
     *
     * @param newList Firestore에서 불러온 새 학습 기록 목록
     */
    public void setItems(List<LearningRecord> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return items.size(); }
            @Override public int getNewListSize() { return newList.size(); }
            @Override public boolean areItemsTheSame(int o, int n) {
                String oldId = items.get(o).getId();
                String newId = newList.get(n).getId();
                return oldId != null && oldId.equals(newId);
            }
            @Override public boolean areContentsTheSame(int o, int n) {
                LearningRecord a = items.get(o), b = newList.get(n);
                String aId = a.getId();
                return aId != null && aId.equals(b.getId()) && a.isBookmarked() == b.isBookmarked();
            }
        });
        items = new ArrayList<>(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * 특정 위치 항목의 북마크 상태를 업데이트하고 해당 아이템만 다시 그린다.
     *
     * @param position  업데이트할 항목의 위치
     * @param bookmarked 설정할 북마크 상태
     */
    public void updateBookmark(int position, boolean bookmarked) {
        if (position < items.size()) {
            items.get(position).setBookmarked(bookmarked);
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() { return items.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvCategory, tvDate;
        final ImageButton btnBookmark;

        ViewHolder(View v) {
            super(v);
            tvTitle     = v.findViewById(R.id.tvHistoryTitle);
            tvCategory  = v.findViewById(R.id.tvHistoryCategory);
            tvDate      = v.findViewById(R.id.tvHistoryDate);
            btnBookmark = v.findViewById(R.id.btnHistoryBookmark);
        }

        void bind(LearningRecord r) {
            tvTitle.setText(r.getAlgorithmTitle());
            tvCategory.setText(r.getCategory());
            if (r.getStudiedAt() != null) {
                Date d = r.getStudiedAt().toDate();
                tvDate.setText(sdf.format(d));
            }
            btnBookmark.setImageResource(
                    r.isBookmarked() ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
            btnBookmark.setOnClickListener(v -> {
                if (listener == null) return;
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) listener.toggle(r, pos);
            });
        }
    }
}
