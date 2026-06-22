package com.example.algoviz.model;

import com.google.firebase.Timestamp;

/**
 * Firebase Firestore에 저장되는 학습 기록 모델 클래스.
 * <p>
 * 사용자가 알고리즘 시각화를 실행할 때마다 생성되며,
 * Firestore의 {@code users/{uid}/records} 컬렉션에 저장된다.
 * 북마크 여부({@code bookmarked})는 Firestore에서 개별 필드 업데이트로 변경된다.
 * </p>
 */
public class LearningRecord {
    private String id;
    private String algorithmKey;
    private String algorithmTitle;
    private String category;
    private Timestamp studiedAt;
    private boolean bookmarked;

    public LearningRecord() {}

    /**
     * 학습 기록을 생성한다. {@code studiedAt}은 현재 시각으로 자동 설정된다.
     *
     * @param algorithmKey   알고리즘 고유 식별자
     * @param algorithmTitle 표시할 알고리즘 제목 (기록 당시 로케일 기준)
     * @param category       알고리즘 카테고리
     */
    public LearningRecord(String algorithmKey, String algorithmTitle, String category) {
        this.algorithmKey = algorithmKey;
        this.algorithmTitle = algorithmTitle;
        this.category = category;
        this.studiedAt = Timestamp.now();
        this.bookmarked = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAlgorithmKey() { return algorithmKey; }
    public void setAlgorithmKey(String algorithmKey) { this.algorithmKey = algorithmKey; }

    public String getAlgorithmTitle() { return algorithmTitle; }
    public void setAlgorithmTitle(String algorithmTitle) { this.algorithmTitle = algorithmTitle; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Timestamp getStudiedAt() { return studiedAt; }
    public void setStudiedAt(Timestamp studiedAt) { this.studiedAt = studiedAt; }

    public boolean isBookmarked() { return bookmarked; }
    public void setBookmarked(boolean bookmarked) { this.bookmarked = bookmarked; }
}
