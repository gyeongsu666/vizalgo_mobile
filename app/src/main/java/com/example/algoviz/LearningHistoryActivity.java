package com.example.algoviz;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.algoviz.adapter.HistoryAdapter;
import com.example.algoviz.databinding.ActivityLearningHistoryBinding;
import com.example.algoviz.firebase.FirebaseManager;
import com.example.algoviz.model.LearningRecord;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LearningHistoryActivity extends AppCompatActivity {

    private ActivityLearningHistoryBinding binding;
    private HistoryAdapter adapter;
    private FirebaseManager firebase;
    private boolean showingBookmarks = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLearningHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_learning_history);
        }

        firebase = FirebaseManager.getInstance();
        setupRecyclerView();
        loadHistory();

        binding.btnToggle.setOnClickListener(v -> {
            showingBookmarks = !showingBookmarks;
            binding.btnToggle.setText(showingBookmarks ? R.string.btn_all_records : R.string.btn_bookmarks_only);
            if (showingBookmarks) loadBookmarks();
            else loadHistory();
        });
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter();
        adapter.setListener((record, position) -> {
            boolean newState = !record.isBookmarked();
            firebase.toggleBookmark(record.getId(), newState)
                    .addOnSuccessListener(v -> adapter.updateBookmark(position, newState))
                    .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.msg_bookmark_save_failed), Toast.LENGTH_SHORT).show());
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadHistory() {
        setLoading(true);
        firebase.getRecords()
                .addOnSuccessListener(snapshots -> {
                    List<LearningRecord> list = parseRecords(snapshots.getDocuments());
                    setLoading(false);
                    if (list.isEmpty()) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        binding.recyclerView.setVisibility(View.GONE);
                    } else {
                        binding.tvEmpty.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        adapter.setItems(list);
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, getString(R.string.msg_load_failed, e.getMessage()), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadBookmarks() {
        setLoading(true);
        firebase.getBookmarks()
                .addOnSuccessListener(snapshots -> {
                    List<LearningRecord> list = parseRecords(snapshots.getDocuments());
                    setLoading(false);
                    if (list.isEmpty()) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        binding.tvEmpty.setText(R.string.msg_no_bookmarks_history);
                        binding.recyclerView.setVisibility(View.GONE);
                    } else {
                        binding.tvEmpty.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        adapter.setItems(list);
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, getString(R.string.msg_load_failed_short), Toast.LENGTH_SHORT).show();
                });
    }

    private List<LearningRecord> parseRecords(List<DocumentSnapshot> docs) {
        List<LearningRecord> list = new ArrayList<>();
        for (DocumentSnapshot doc : docs) {
            LearningRecord r = doc.toObject(LearningRecord.class);
            if (r != null) {
                r.setId(doc.getId());
                list.add(r);
            }
        }
        return list;
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
