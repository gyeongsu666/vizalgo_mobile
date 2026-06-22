package com.example.algoviz.firebase;

import com.example.algoviz.model.LearningRecord;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Firebase Auth 및 Firestore 접근을 담당하는 싱글턴 매니저 클래스.
 * <p>
 * 인증(ID/PW 로그인, Google 로그인, 회원가입, 로그아웃)과
 * Firestore 학습 기록·북마크 CRUD를 단일 클래스에서 관리한다.
 * 아이디는 내부적으로 {@value #EMAIL_SUFFIX} 접미사를 붙여 Firebase 이메일 형식으로 변환한다.
 * </p>
 */
public class FirebaseManager {

    // 아이디를 Firebase Auth 이메일로 변환하는 접미사
    private static final String EMAIL_SUFFIX = "@algoviz.com";

    private static FirebaseManager instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    private FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseManager getInstance() {
        if (instance == null) instance = new FirebaseManager();
        return instance;
    }

    // ── Auth ────────────────────────────────────────────────────

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    /** 현재 로그인한 사용자 이름 반환 */
    public String getCurrentUserName() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return "";
        String name = user.getDisplayName();
        return (name != null && !name.isEmpty()) ? name : idFromEmail(user.getEmail());
    }

    /** 현재 로그인한 사용자 아이디 반환 */
    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return "";
        return idFromEmail(user.getEmail());
    }

    private String idFromEmail(String email) {
        if (email == null) return "";
        return email.replace(EMAIL_SUFFIX, "");
    }

    /** 아이디 + 이름 + 비밀번호로 회원가입 */
    public Task<Void> registerWithIdNamePassword(String id, String name, String password) {
        String email = id + EMAIL_SUFFIX;
        return auth.createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) throw new Exception("사용자 생성 실패");
                    // displayName에 이름 저장
                    UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    return user.updateProfile(profile);
                });
    }

    /** 아이디 + 비밀번호로 로그인 */
    public Task<com.google.firebase.auth.AuthResult> loginWithId(String id, String password) {
        return auth.signInWithEmailAndPassword(id + EMAIL_SUFFIX, password);
    }

    public Task<com.google.firebase.auth.AuthResult> loginWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return auth.signInWithCredential(credential);
    }

    public void logout() {
        auth.signOut();
    }

    // ── Firestore: 학습 기록 ────────────────────────────────────

    private String uid() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    /**
     * 학습 기록을 Firestore에 저장한다.
     *
     * @param record 저장할 학습 기록 객체
     * @return 저장된 문서의 {@link DocumentReference}를 담은 Task
     * @throws IllegalStateException 로그인하지 않은 상태에서 호출 시
     */
    public Task<DocumentReference> saveRecord(LearningRecord record) {
        String uid = uid();
        if (uid == null) throw new IllegalStateException("로그인 필요");
        return db.collection("users").document(uid)
                .collection("records").add(record);
    }

    /**
     * 현재 사용자의 학습 기록을 최신 순으로 최대 50건 조회한다.
     *
     * @return {@link QuerySnapshot}을 담은 Task
     * @throws IllegalStateException 로그인하지 않은 상태에서 호출 시
     */
    public Task<QuerySnapshot> getRecords() {
        String uid = uid();
        if (uid == null) throw new IllegalStateException("로그인 필요");
        return db.collection("users").document(uid)
                .collection("records")
                .orderBy("studiedAt", Query.Direction.DESCENDING)
                .limit(50)
                .get();
    }

    public Task<QuerySnapshot> getBookmarks() {
        String uid = uid();
        if (uid == null) throw new IllegalStateException("로그인 필요");
        return db.collection("users").document(uid)
                .collection("records")
                .whereEqualTo("bookmarked", true)
                .orderBy("studiedAt", Query.Direction.DESCENDING)
                .get();
    }

    public Task<Void> toggleBookmark(String recordId, boolean bookmarked) {
        String uid = uid();
        if (uid == null) throw new IllegalStateException("로그인 필요");
        Map<String, Object> update = new HashMap<>();
        update.put("bookmarked", bookmarked);
        return db.collection("users").document(uid)
                .collection("records").document(recordId)
                .update(update);
    }

    // ── Firestore: 알고리즘별 북마크 ──────────────────────────

    public Task<QuerySnapshot> getBookmarkedKeys() {
        String uid = uid();
        if (uid == null) throw new IllegalStateException("로그인 필요");
        return db.collection("users").document(uid)
                .collection("bookmarks")
                .get();
    }

    /**
     * 알고리즘 북마크 상태를 Firestore에 설정한다.
     * <p>
     * {@code bookmarked=true}이면 {@code bookmarks/{algorithmKey}} 문서를 생성하고,
     * {@code false}이면 삭제한다.
     * </p>
     *
     * @param algorithmKey 북마크할 알고리즘 키
     * @param bookmarked   설정할 북마크 상태
     * @return 완료를 나타내는 Task
     */
    public Task<Void> setAlgorithmBookmark(String algorithmKey, boolean bookmarked) {
        String uid = uid();
        if (uid == null) throw new IllegalStateException("로그인 필요");
        DocumentReference ref = db.collection("users").document(uid)
                .collection("bookmarks").document(algorithmKey);
        if (bookmarked) {
            Map<String, Object> data = new HashMap<>();
            data.put("key", algorithmKey);
            return ref.set(data);
        } else {
            return ref.delete();
        }
    }
}
