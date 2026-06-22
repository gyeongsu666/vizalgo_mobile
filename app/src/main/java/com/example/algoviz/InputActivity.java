package com.example.algoviz;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.algoviz.algorithm.AlgorithmRegistry;
import com.example.algoviz.databinding.ActivityInputBinding;
import com.example.algoviz.model.Algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class InputActivity extends AppCompatActivity {

    private ActivityInputBinding binding;
    private Algorithm algorithm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String key = getIntent().getStringExtra("algorithmKey");
        algorithm = AlgorithmRegistry.get(key);
        if (algorithm == null) { finish(); return; }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_input_setup);
        }

        // 알고리즘 정보 표시
        binding.tvBadge.setText(algorithm.getBadge());
        binding.tvTitle.setText(algorithm.getTitle());
        binding.tvInputHint.setText(algorithm.getInputHint());
        binding.etInput.setText(algorithm.getExample());

        // 정렬·탐색 알고리즘만 랜덤 버튼 표시
        boolean supportsRandom = algorithm.getRenderer().equals("sequence")
                              || algorithm.getRenderer().equals("search");
        binding.btnRandom.setVisibility(supportsRandom ? View.VISIBLE : View.GONE);

        binding.btnRandom.setOnClickListener(v -> showRandomDialog());

        binding.btnStart.setOnClickListener(v -> {
            String input = binding.etInput.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_input_required), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, VisualizationActivity.class);
            intent.putExtra("algorithmKey", algorithm.getKey());
            intent.putExtra("inputData", input);
            startActivity(intent);
        });
    }

    private void showRandomDialog() {
        int dp = (int) getResources().getDisplayMetrics().density;
        int defaultCount = getResources().getInteger(R.integer.random_default_count);
        int minCount     = getResources().getInteger(R.integer.random_min_count);
        int maxCount     = getResources().getInteger(R.integer.max_input_count);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp * 24, dp * 16, dp * 24, dp * 8);

        EditText etCount = new EditText(this);
        etCount.setInputType(InputType.TYPE_CLASS_NUMBER);
        etCount.setHint(getString(R.string.hint_random_count, minCount, maxCount));
        etCount.setText(String.valueOf(defaultCount));
        etCount.selectAll();
        layout.addView(etCount);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_random_dialog))
                .setView(layout)
                .setPositiveButton(getString(R.string.btn_generate), (dialog, which) -> {
                    int count = defaultCount;
                    try {
                        count = Integer.parseInt(etCount.getText().toString().trim());
                        if (count < minCount) count = minCount;
                        if (count > maxCount) count = maxCount;
                    } catch (NumberFormatException ignored) {}
                    generateRandom(count);
                })
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }

    private void generateRandom(int count) {
        Random rnd = new Random();
        int maxVal = getResources().getInteger(R.integer.random_max_value);
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < count; i++) nums.add(rnd.nextInt(maxVal) + 1);

        if (algorithm.getRenderer().equals("search")) {
            Collections.sort(nums);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < nums.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(nums.get(i));
            }
            int target = nums.get(rnd.nextInt(nums.size()));
            sb.append(" | ").append(target);
            binding.etInput.setText(sb.toString());
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < nums.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(nums.get(i));
            }
            binding.etInput.setText(sb.toString());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
