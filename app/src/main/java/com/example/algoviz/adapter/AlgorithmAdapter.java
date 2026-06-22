package com.example.algoviz.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.algoviz.R;
import com.example.algoviz.model.Algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * 알고리즘 목록 화면의 RecyclerView 어댑터.
 * <p>
 * {@link DiffUtil}을 사용해 목록 변경 시 최소 범위의 아이템만 갱신하여 성능을 최적화한다.
 * 아이콘과 배경색은 알고리즘의 {@code renderer} 필드(언어에 독립적)를 기준으로 결정한다.
 * </p>
 */
public class AlgorithmAdapter extends RecyclerView.Adapter<AlgorithmAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onCardClick(Algorithm algorithm);
        void onBookmarkClick(Algorithm algorithm);
    }

    private List<Algorithm> items = new ArrayList<>();
    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 새 알고리즘 목록으로 RecyclerView를 갱신한다.
     * <p>
     * {@link DiffUtil}이 이전 목록과 비교해 변경된 아이템만 최소 범위로 업데이트하므로,
     * 전체 {@code notifyDataSetChanged()} 대비 성능이 우수하고 애니메이션이 자연스럽다.
     * </p>
     *
     * @param newList 표시할 새 알고리즘 목록
     */
    public void submitList(List<Algorithm> newList) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return items.size(); }
            @Override public int getNewListSize() { return newList.size(); }
            @Override public boolean areItemsTheSame(int o, int n) {
                return items.get(o).getKey().equals(newList.get(n).getKey());
            }
            @Override public boolean areContentsTheSame(int o, int n) {
                Algorithm a = items.get(o), b = newList.get(n);
                return a.getKey().equals(b.getKey()) && a.isBookmarked() == b.isBookmarked();
            }
        });
        items = new ArrayList<>(newList);
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_algorithm_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ── 카테고리 → 아이콘 / 배경색 매핑 ──────────────────────────

    /**
     * renderer 타입에 대응하는 아이콘 drawable 리소스 ID를 반환한다.
     * <p>
     * renderer 값은 언어에 독립적이므로 언어 전환 후에도 동일하게 동작한다.
     * </p>
     *
     * @param renderer 알고리즘 렌더러 타입 ("sequence" | "search" | "graph" | "tree")
     * @return 아이콘 drawable 리소스 ID
     */
    @DrawableRes
    private static int iconFor(String renderer) {
        switch (renderer) {
            case "sequence": return R.drawable.ic_algo_sort;
            case "search":   return R.drawable.ic_algo_search;
            case "graph":    return R.drawable.ic_algo_graph;
            case "tree":     return R.drawable.ic_algo_tree;
            default:         return R.drawable.ic_algo_sort;
        }
    }

    @ColorRes
    private static int iconTintFor(String renderer) {
        switch (renderer) {
            case "sequence": return R.color.primary;
            case "search":   return R.color.success;
            case "graph":    return R.color.warning;
            case "tree":     return R.color.purple;
            default:         return R.color.primary;
        }
    }

    @ColorRes
    private static int iconBgFor(String renderer) {
        switch (renderer) {
            case "sequence": return R.color.icon_bg;
            case "search":   return R.color.icon_bg_green;
            case "graph":    return R.color.icon_bg_orange;
            case "tree":     return R.color.icon_bg_purple;
            default:         return R.color.icon_bg;
        }
    }

    // ── ViewHolder ────────────────────────────────────────────────

    class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView    cardIcon;
        private final ImageView   ivIcon;
        private final TextView    tvTitle;
        private final TextView    tvComplexity;
        private final ImageButton btnBookmark;

        ViewHolder(View itemView) {
            super(itemView);
            cardIcon     = itemView.findViewById(R.id.cardIcon);
            ivIcon       = itemView.findViewById(R.id.ivIcon);
            tvTitle      = itemView.findViewById(R.id.tvTitle);
            tvComplexity = itemView.findViewById(R.id.tvComplexity);
            btnBookmark  = itemView.findViewById(R.id.btnBookmark);
        }

        /**
         * 알고리즘 데이터를 뷰에 바인딩한다.
         * 아이콘·배경색은 {@code algo.getRenderer()}를 기반으로 언어와 무관하게 결정된다.
         *
         * @param algo 표시할 알고리즘 객체
         */
        void bind(Algorithm algo) {
            tvTitle.setText(algo.getTitle());
            tvComplexity.setText(itemView.getContext().getString(
                    R.string.label_avg_complexity, algo.getTimeComplexityAvg()));

            // renderer 기반 아이콘 + 색상 (언어 독립적)
            String renderer = algo.getRenderer();
            ivIcon.setImageResource(iconFor(renderer));
            ivIcon.setImageTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.getContext(), iconTintFor(renderer))));
            cardIcon.setCardBackgroundColor(
                    ContextCompat.getColor(itemView.getContext(), iconBgFor(renderer)));

            btnBookmark.setImageResource(
                    algo.isBookmarked() ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCardClick(algo);
            });
            btnBookmark.setOnClickListener(v -> {
                if (listener != null) listener.onBookmarkClick(algo);
            });
        }
    }
}
