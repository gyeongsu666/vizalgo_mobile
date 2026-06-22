package com.example.algoviz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.algoviz.algorithm.AlgorithmRegistry;
import com.example.algoviz.databinding.ActivityAlgorithmDetailBinding;
import com.example.algoviz.db.AppDatabase;
import com.example.algoviz.db.RecentAlgorithm;
import com.example.algoviz.model.Algorithm;

import java.util.concurrent.Executors;

public class AlgorithmDetailActivity extends AppCompatActivity {

    private ActivityAlgorithmDetailBinding binding;
    private Algorithm algorithm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlgorithmDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String key = getIntent().getStringExtra("algorithmKey");
        algorithm = AlgorithmRegistry.get(key);
        if (algorithm == null) { finish(); return; }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(algorithm.getTitle());
        }

        bindData();
        saveToRecent(algorithm);

        binding.btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(this, InputActivity.class);
            intent.putExtra("algorithmKey", algorithm.getKey());
            startActivity(intent);
        });
    }

    private void bindData() {
        binding.tvTitle.setText(algorithm.getTitle());
        binding.tvBadge.setText(algorithm.getBadge());
        binding.tvDescription.setText(algorithm.getDescription());

        // 복잡도 표
        binding.tvBest.setText(algorithm.getTimeComplexityBest());
        binding.tvAvg.setText(algorithm.getTimeComplexityAvg());
        binding.tvWorst.setText(algorithm.getTimeComplexityWorst());
        binding.tvSpace.setText(algorithm.getSpaceComplexity());

        // 활용 사례
        binding.tvUseCases.setText(algorithm.getUseCases());
    }

    private void saveToRecent(Algorithm algo) {
        RecentAlgorithm recent = new RecentAlgorithm();
        recent.algorithmKey = algo.getKey();
        recent.title = algo.getTitle();
        recent.category = algo.getCategory();
        recent.visitedAt = System.currentTimeMillis();
        Executors.newSingleThreadExecutor().execute(() ->
                AppDatabase.getInstance(this).recentAlgorithmDao().insert(recent));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
