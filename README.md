# AlgoViz (vizalgo_mobile)

알고리즘 시각화 학습 Android 앱. 정렬·탐색·그래프·트리 순회 등 **12종 알고리즘**의 동작 과정을 단계별 애니메이션과 코드 하이라이팅으로 보여줍니다.

## 주요 기능

- **12종 알고리즘 시각화** — 버블·선택·삽입·퀵·병합 정렬 / 순차·이진 탐색 / DFS·BFS / 전위·중위·후위 순회
- **단계별 재생** — 재생·일시정지·이전·다음·속도 조절, 배열 막대/그래프/트리를 Canvas로 렌더링
- **코드 하이라이팅** — 현재 실행 단계에 해당하는 Python 코드 줄을 실시간 강조 (코드 ↔ 동작 1:1 동기화)
- **로그인** — Firebase Authentication (이메일/비밀번호 + Google OAuth)
- **학습 기록 · 북마크** — Firebase Firestore 클라우드 저장
- **최근 학습** — Room(SQLite) 로컬 캐싱
- **지역화** — 한국어/영어 인앱 전환 (`AppCompatDelegate.setApplicationLocales`)

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| 언어 / 플랫폼 | Java · Android (minSdk 26, targetSdk 34) |
| 인증 / DB | Firebase Auth · Firestore · Room |
| UI | RecyclerView + DiffUtil · ViewBinding · Material Components · Custom View(Canvas) |
| 빌드 | Gradle |

## 패키지 구조

```
com.example.algoviz
├── (root)      8개 Activity (Login, Register, AlgorithmList, AlgorithmDetail,
│               Input, Visualization, LearningHistory, CodeView)
├── adapter/    AlgorithmAdapter, HistoryAdapter (RecyclerView + DiffUtil)
├── algorithm/  AlgorithmRegistry, StepBuilder (12종 알고리즘 단계 생성)
├── db/         AppDatabase, RecentAlgorithm, RecentAlgorithmDao (Room)
├── firebase/   FirebaseManager (Auth + Firestore)
├── model/      Algorithm, AlgorithmStep, LearningRecord
└── view/       VisualizationView, CodeHighlightView (Canvas 커스텀 뷰)
```

## 빌드 방법

1. Android Studio로 프로젝트를 엽니다.
2. Gradle Sync 후 실행(Run)합니다. (Firebase 설정 `app/google-services.json` 포함)
3. minSdk 26 이상 기기 또는 에뮬레이터에서 동작합니다.

## 라이선스

학습용 프로젝트입니다.
