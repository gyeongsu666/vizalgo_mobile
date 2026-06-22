package com.example.algoviz.model;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.util.List;

/**
 * 알고리즘 정보를 담는 모델 클래스.
 * <p>
 * 한국어/영어 양국어 필드를 보유하며, {@link #localized(String, String)} 헬퍼를 통해
 * 현재 앱 로케일에 맞는 언어의 텍스트를 자동으로 반환한다.
 * 복잡도(시간·공간), Python 예시 코드, 활용 사례 등 상세 정보를 포함한다.
 * </p>
 */
public class Algorithm {
    private final String key;
    private final String title;
    private final String titleEn;
    private final String category;
    private final String categoryEn;
    private final String badge;
    private final String badgeEn;
    private final String description;
    private final String descriptionEn;
    private final String inputHint;
    private final String inputHintEn;
    private final String example;
    private final List<String> pythonCode;
    private final String renderer;
    private final String timeComplexityBest;
    private final String timeComplexityAvg;
    private final String timeComplexityWorst;
    private final String spaceComplexity;
    private final String useCases;
    private final String useCasesEn;
    private boolean bookmarked;

    /**
     * 알고리즘 객체를 생성한다.
     *
     * @param key           알고리즘 고유 식별자 (예: "bubbleSort")
     * @param title         한국어 제목
     * @param titleEn       영어 제목
     * @param category      한국어 카테고리 (예: "정렬")
     * @param categoryEn    영어 카테고리 (예: "Sorting")
     * @param renderer      시각화 렌더러 타입 ("sequence" | "search" | "graph" | "tree")
     */
    public Algorithm(String key,
                     String title, String titleEn,
                     String category, String categoryEn,
                     String badge, String badgeEn,
                     String description, String descriptionEn,
                     String inputHint, String inputHintEn,
                     String example, List<String> pythonCode, String renderer,
                     String timeComplexityBest, String timeComplexityAvg,
                     String timeComplexityWorst, String spaceComplexity,
                     String useCases, String useCasesEn) {
        this.key = key;
        this.title = title;
        this.titleEn = titleEn;
        this.category = category;
        this.categoryEn = categoryEn;
        this.badge = badge;
        this.badgeEn = badgeEn;
        this.description = description;
        this.descriptionEn = descriptionEn;
        this.inputHint = inputHint;
        this.inputHintEn = inputHintEn;
        this.example = example;
        this.pythonCode = pythonCode;
        this.renderer = renderer;
        this.timeComplexityBest = timeComplexityBest;
        this.timeComplexityAvg = timeComplexityAvg;
        this.timeComplexityWorst = timeComplexityWorst;
        this.spaceComplexity = spaceComplexity;
        this.useCases = useCases;
        this.useCasesEn = useCasesEn;
        this.bookmarked = false;
    }

    /**
     * 현재 앱 로케일에 따라 한국어 또는 영어 문자열을 반환한다.
     * {@link AppCompatDelegate#getApplicationLocales()}를 기준으로 판단하며,
     * 로케일이 설정되지 않은 경우 한국어({@code ko})를 기본값으로 반환한다.
     */
    private static String localized(String ko, String en) {
        if (en == null) return ko;
        LocaleListCompat locales = AppCompatDelegate.getApplicationLocales();
        if (locales.isEmpty()) return ko;
        return "en".equals(locales.get(0).getLanguage()) ? en : ko;
    }

    public String getKey()                { return key; }
    public String getTitle()              { return localized(title, titleEn); }
    public String getCategory()           { return localized(category, categoryEn); }
    public String getBadge()              { return localized(badge, badgeEn); }
    public String getDescription()        { return localized(description, descriptionEn); }
    public String getInputHint()          { return localized(inputHint, inputHintEn); }
    public String getExample()            { return example; }
    public List<String> getPythonCode()   { return pythonCode; }
    public String getRenderer()           { return renderer; }
    public String getTimeComplexityBest() { return timeComplexityBest; }
    public String getTimeComplexityAvg()  { return timeComplexityAvg; }
    public String getTimeComplexityWorst(){ return timeComplexityWorst; }
    public String getSpaceComplexity()    { return spaceComplexity; }
    public String getUseCases()           { return localized(useCases, useCasesEn); }
    public boolean isBookmarked()         { return bookmarked; }
    public void setBookmarked(boolean bookmarked) { this.bookmarked = bookmarked; }
}
