package com.example.algoviz.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * {@link RecentAlgorithm} 테이블에 대한 DAO(Data Access Object) 인터페이스.
 * <p>
 * Room 라이브러리가 런타임에 구현체를 자동 생성한다.
 * 모든 메서드는 반드시 백그라운드 스레드에서 호출해야 한다.
 * </p>
 */
@Dao
public interface RecentAlgorithmDao {

    /** 이미 있으면 visitedAt을 갱신(REPLACE) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RecentAlgorithm recent);

    /** 최근 순으로 최대 5개 반환 */
    @Query("SELECT * FROM recent_algorithms ORDER BY visitedAt DESC LIMIT 5")
    List<RecentAlgorithm> getRecent();
}
