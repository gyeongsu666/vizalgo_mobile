package com.example.algoviz.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 최근 학습한 알고리즘을 로컬에 저장하는 Room 엔티티.
 * <p>
 * {@code algorithmKey}를 기본키로 사용하므로, 동일 알고리즘을 재방문하면
 * {@link com.example.algoviz.db.RecentAlgorithmDao#insert}의 REPLACE 전략에 의해
 * {@code visitedAt}이 갱신된다.
 * </p>
 */
@Entity(tableName = "recent_algorithms")
public class RecentAlgorithm {
    @PrimaryKey
    @NonNull
    public String algorithmKey = "";
    public String title;
    public String category;
    public long visitedAt;
}
