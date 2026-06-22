package com.example.algoviz.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * 앱의 Room 로컬 데이터베이스 싱글턴 클래스.
 * <p>
 * 이중 확인 잠금(double-checked locking) 패턴으로 단일 인스턴스를 보장한다.
 * {@link #getInstance(Context)}를 통해 접근하며,
 * DB 파일명은 {@code algoviz_db}이다.
 * </p>
 */
@Database(entities = {RecentAlgorithm.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract RecentAlgorithmDao recentAlgorithmDao();

    /**
     * 데이터베이스 싱글턴 인스턴스를 반환한다.
     * <p>
     * 이중 확인 잠금으로 스레드 안전하게 단일 인스턴스를 생성·반환한다.
     * {@code context.getApplicationContext()}를 사용하므로 메모리 누수가 없다.
     * </p>
     *
     * @param context 애플리케이션 또는 액티비티 컨텍스트
     * @return AppDatabase 싱글턴 인스턴스
     */
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "algoviz_db"
                    ).build();
                }
            }
        }
        return instance;
    }
}
