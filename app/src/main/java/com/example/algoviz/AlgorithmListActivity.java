package com.example.algoviz;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.os.LocaleListCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.algoviz.adapter.AlgorithmAdapter;
import com.example.algoviz.algorithm.AlgorithmRegistry;
import com.example.algoviz.databinding.ActivityAlgorithmListBinding;
import com.example.algoviz.db.AppDatabase;
import com.example.algoviz.db.RecentAlgorithm;
import com.example.algoviz.firebase.FirebaseManager;
import com.example.algoviz.model.Algorithm;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AlgorithmListActivity extends AppCompatActivity {

    private ActivityAlgorithmListBinding binding;
    private AlgorithmAdapter adapter;
    private FirebaseManager firebase;
    private String currentCategory = null;  // null = 전체
    private String searchQuery = "";
    private boolean searchVisible = false;
    private String[] categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlgorithmListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebase = FirebaseManager.getInstance();
        categories = getResources().getStringArray(R.array.algorithm_categories);

        setupHeader();
        setupLanguageToggle();
        setupTabs();
        setupRecyclerView();
        loadBookmarks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentAlgorithms();
    }

    // ── 언어 토글 ─────────────────────────────────────────────────

    private void setupLanguageToggle() {
        updateLanguageButton();
        binding.btnLanguage.setOnClickListener(v -> {
            boolean currentlyEnglish = isCurrentlyEnglish();
            String newTag = currentlyEnglish ? "ko" : "en";
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(newTag));
            // AppCompatDelegate가 자동으로 Activity를 재생성
        });
    }

    private void updateLanguageButton() {
        binding.btnLanguage.setText(isCurrentlyEnglish() ? "한" : "EN");
    }

    private boolean isCurrentlyEnglish() {
        LocaleListCompat locales = AppCompatDelegate.getApplicationLocales();
        String lang = locales.isEmpty()
                ? Locale.getDefault().getLanguage()
                : locales.get(0).getLanguage();
        return "en".equals(lang);
    }

    // ── 헤더: 검색 + 계정 ──────────────────────────────────────────

    private void setupHeader() {
        // 로그인한 경우 이름 표시
        if (firebase.isLoggedIn()) {
            String name = firebase.getCurrentUserName();
            if (!name.isEmpty()) {
                binding.tvAppTitle.setText(getString(R.string.greeting_user, name));
                binding.tvAppTitle.setTextSize(getResources().getInteger(R.integer.greeting_text_size_sp));
            }
        }

        binding.btnSearch.setOnClickListener(v -> toggleSearch());
        binding.btnAccount.setOnClickListener(v -> showAccountMenu(v));

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim();
                refreshList();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void toggleSearch() {
        searchVisible = !searchVisible;
        binding.layoutSearch.setVisibility(searchVisible ? View.VISIBLE : View.GONE);
        if (searchVisible) {
            binding.etSearch.requestFocus();
        } else {
            searchQuery = "";
            binding.etSearch.setText("");
            refreshList();
        }
    }

    private void showAccountMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 1, 0, getString(R.string.menu_history));
        popup.getMenu().add(0, 2, 1, getString(R.string.menu_bookmark));
        popup.getMenu().add(0, 3, 2, getString(R.string.menu_account_info));
        popup.getMenu().add(0, 4, 3,
                firebase.isLoggedIn() ? getString(R.string.menu_logout) : getString(R.string.menu_login));

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if      (id == 1) onMenuHistory();
            else if (id == 2) onMenuBookmarks();
            else if (id == 3) onMenuAccountInfo();
            else if (id == 4) onMenuAuthToggle();
            return true;
        });
        popup.show();
    }

    private void onMenuHistory() {
        if (!firebase.isLoggedIn()) {
            Toast.makeText(this, getString(R.string.msg_login_required), Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, LearningHistoryActivity.class));
    }

    private void onMenuBookmarks() {
        List<Algorithm> bookmarked = AlgorithmRegistry.getBookmarked();
        if (bookmarked.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_no_bookmarks), Toast.LENGTH_SHORT).show();
            return;
        }
        searchQuery = "";
        binding.etSearch.setText("");
        adapter.submitList(bookmarked);
        Toast.makeText(this,
                getString(R.string.msg_bookmark_count, bookmarked.size()), Toast.LENGTH_SHORT).show();
    }

    private void onMenuAccountInfo() {
        if (!firebase.isLoggedIn()) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.title_account))
                    .setMessage(getString(R.string.msg_go_to_login))
                    .setPositiveButton(getString(R.string.btn_login), (d, w) ->
                            startActivity(new Intent(this, LoginActivity.class)))
                    .setNegativeButton(getString(R.string.btn_cancel), null)
                    .show();
            return;
        }
        String name = firebase.getCurrentUserName();
        String id   = firebase.getCurrentUserId();
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_account_info))
                .setMessage(getString(R.string.account_info_format, name, id))
                .setPositiveButton(getString(R.string.btn_confirm), null)
                .setNeutralButton(getString(R.string.btn_logout), (d, w) -> {
                    firebase.logout();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .show();
    }

    private void onMenuAuthToggle() {
        if (firebase.isLoggedIn()) {
            firebase.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    // ── 탭 / 목록 ─────────────────────────────────────────────────

    private void setupTabs() {
        for (String cat : categories) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(cat));
        }
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                currentCategory = (pos == 0) ? null : categories[pos];
                refreshList();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new AlgorithmAdapter();
        adapter.setListener(new AlgorithmAdapter.OnItemClickListener() {
            @Override
            public void onCardClick(Algorithm algorithm) {
                Intent intent = new Intent(AlgorithmListActivity.this, AlgorithmDetailActivity.class);
                intent.putExtra("algorithmKey", algorithm.getKey());
                startActivity(intent);
            }

            @Override
            public void onBookmarkClick(Algorithm algorithm) {
                boolean newState = !algorithm.isBookmarked();
                algorithm.setBookmarked(newState);
                refreshList();
                if (firebase.isLoggedIn()) {
                    firebase.setAlgorithmBookmark(algorithm.getKey(), newState);
                }
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        refreshList();
    }

    private void refreshList() {
        List<Algorithm> list;
        if (!searchQuery.isEmpty()) {
            list = AlgorithmRegistry.search(searchQuery);
        } else {
            list = AlgorithmRegistry.getByCategory(currentCategory);
        }
        adapter.submitList(list);
    }

    // ── 최근 학습 ─────────────────────────────────────────────────

    private void loadRecentAlgorithms() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<RecentAlgorithm> recent = AppDatabase.getInstance(this)
                    .recentAlgorithmDao().getRecent();
            runOnUiThread(() -> showRecentSection(recent));
        });
    }

    private void showRecentSection(List<RecentAlgorithm> recentList) {
        if (recentList.isEmpty()) {
            binding.layoutRecent.setVisibility(View.GONE);
            return;
        }
        binding.layoutRecent.setVisibility(View.VISIBLE);
        binding.chipGroupRecent.removeAllViews();
        for (RecentAlgorithm r : recentList) {
            Algorithm algo = AlgorithmRegistry.get(r.algorithmKey);
            Chip chip = new Chip(this);
            chip.setText(algo != null ? algo.getTitle() : r.title);
            chip.setChipBackgroundColorResource(R.color.primary_light);
            chip.setTextColor(ContextCompat.getColor(this, R.color.primary));
            chip.setEnsureMinTouchTargetSize(false);
            chip.setOnClickListener(v -> {
                Intent intent = new Intent(this, AlgorithmDetailActivity.class);
                intent.putExtra("algorithmKey", r.algorithmKey);
                startActivity(intent);
            });
            binding.chipGroupRecent.addView(chip);
        }
    }

    private void loadBookmarks() {
        if (!firebase.isLoggedIn()) return;
        firebase.getBookmarkedKeys()
                .addOnSuccessListener(snapshots -> {
                    for (Algorithm a : AlgorithmRegistry.getAll()) {
                        a.setBookmarked(false);
                    }
                    snapshots.forEach(doc -> {
                        Algorithm algo = AlgorithmRegistry.get(doc.getId());
                        if (algo != null) algo.setBookmarked(true);
                    });
                    refreshList();
                });
    }
}
