package com.example.algoviz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.algoviz.algorithm.AlgorithmRegistry;
import com.example.algoviz.algorithm.StepBuilder;
import com.example.algoviz.algorithm.StepBuilder.Edge;
import com.example.algoviz.databinding.ActivityVisualizationBinding;
import com.example.algoviz.firebase.FirebaseManager;
import com.example.algoviz.model.Algorithm;
import com.example.algoviz.model.AlgorithmStep;
import com.example.algoviz.model.LearningRecord;

import java.util.ArrayList;
import java.util.List;

public class VisualizationActivity extends AppCompatActivity {

    private ActivityVisualizationBinding binding;
    private Algorithm algorithm;
    private List<AlgorithmStep> steps = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isPlaying = false;
    private int speedMs;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPlaying) return;
            if (currentIndex < steps.size() - 1) {
                currentIndex++;
                showStep();
                handler.postDelayed(this, speedMs);
            } else {
                stopPlayback();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVisualizationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String key = getIntent().getStringExtra("algorithmKey");
        algorithm = AlgorithmRegistry.get(key);
        if (algorithm == null) { finish(); return; }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(algorithm.getTitle());
        }

        // 코드 보기 / 닫기 토글
        binding.codeHighlightView.setCode(algorithm.getPythonCode());
        binding.tvViewCode.setOnClickListener(v -> {
            boolean showing = binding.cardCode.getVisibility() == View.VISIBLE;
            binding.cardCode.setVisibility(showing ? View.GONE : View.VISIBLE);
            binding.tvViewCode.setText(showing ? R.string.label_code_view : R.string.label_code_hide);
        });

        // 홈 버튼: 백 스택을 정리하고 메인 화면으로 이동
        binding.btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlgorithmListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        setupPlayControls();

        // InputActivity에서 전달받은 입력값으로 바로 시각화 시작
        String inputData = getIntent().getStringExtra("inputData");
        if (inputData == null || inputData.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_no_input), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        buildSteps(inputData);
    }

    private void setupPlayControls() {
        speedMs = getResources().getInteger(R.integer.default_speed_ms);
        final int minSpeed = getResources().getInteger(R.integer.min_speed_ms);
        final int maxSpeed = getResources().getInteger(R.integer.max_speed_ms);

        binding.btnPrev.setOnClickListener(v -> {
            stopPlayback();
            if (currentIndex > 0) { currentIndex--; showStep(); }
        });
        binding.btnNext.setOnClickListener(v -> {
            stopPlayback();
            if (currentIndex < steps.size() - 1) { currentIndex++; showStep(); }
        });
        binding.btnPlay.setOnClickListener(v -> {
            if (isPlaying) stopPlayback();
            else startPlayback();
        });

        binding.seekbarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                speedMs = minSpeed + (maxSpeed - minSpeed) * progress / 100;
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });
        binding.seekbarSpeed.setProgress(getResources().getInteger(R.integer.default_speed_progress));
    }

    private void buildSteps(String input) {
        stopPlayback();
        try {
            steps = generateSteps(input);
            currentIndex = 0;
            showStep();
            saveRecord();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.msg_input_error, e.getMessage()), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private List<AlgorithmStep> generateSteps(String raw) throws Exception {
        String key = algorithm.getKey();
        switch (key) {
            case "bubbleSort":    return StepBuilder.buildBubbleSort(parseNumbers(raw));
            case "selectionSort": return StepBuilder.buildSelectionSort(parseNumbers(raw));
            case "insertionSort": return StepBuilder.buildInsertionSort(parseNumbers(raw));
            case "quickSort":     return StepBuilder.buildQuickSort(parseNumbers(raw));
            case "mergeSort":     return StepBuilder.buildMergeSort(parseNumbers(raw));
            case "linearSearch":  return StepBuilder.buildLinearSearch(parseSearchInput(raw), parseTarget(raw));
            case "binarySearch":  return StepBuilder.buildBinarySearch(parseSearchInput(raw), parseTarget(raw));
            case "dfs":  { GraphInput g = parseGraph(raw); return StepBuilder.buildDFS(g.edges, g.start); }
            case "bfs":  { GraphInput g = parseGraph(raw); return StepBuilder.buildBFS(g.edges, g.start); }
            case "preorder":  return StepBuilder.buildPreorder(parseTreeLevel(raw));
            case "inorder":   return StepBuilder.buildInorder(parseTreeLevel(raw));
            case "postorder": return StepBuilder.buildPostorder(parseTreeLevel(raw));
            default: throw new IllegalArgumentException(getString(R.string.msg_input_error, key));
        }
    }

    private void showStep() {
        if (steps.isEmpty()) return;
        AlgorithmStep step = steps.get(currentIndex);
        binding.visualizationView.setStep(step, algorithm.getRenderer());
        binding.codeHighlightView.setActiveLines(step.getActiveLines());
        binding.tvMessage.setText(step.getMessage());
        binding.tvProgress.setText((currentIndex + 1) + " / " + steps.size());
        binding.btnPrev.setEnabled(currentIndex > 0);
        binding.btnNext.setEnabled(currentIndex < steps.size() - 1);

        if (step.getFrontierChips() != null && !step.getFrontierChips().isEmpty()) {
            binding.layoutChips.setVisibility(View.VISIBLE);
            binding.tvFrontierLabel.setText(step.getFrontierLabel() + ": ");
            binding.tvFrontierChips.setText(TextUtils.join(" ", step.getFrontierChips()));
            binding.tvVisitedChips.setText(getString(R.string.label_visited) + ": " + TextUtils.join(" → ", step.getVisitedChips()));
        } else {
            binding.layoutChips.setVisibility(View.GONE);
        }
    }

    private void startPlayback() {
        if (currentIndex >= steps.size() - 1) currentIndex = 0;
        isPlaying = true;
        binding.btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        handler.postDelayed(playRunnable, speedMs);
    }

    private void stopPlayback() {
        isPlaying = false;
        handler.removeCallbacks(playRunnable);
        binding.btnPlay.setImageResource(android.R.drawable.ic_media_play);
    }

    private void saveRecord() {
        if (!FirebaseManager.getInstance().isLoggedIn()) return;
        LearningRecord record = new LearningRecord(
                algorithm.getKey(), algorithm.getTitle(), algorithm.getCategory());
        FirebaseManager.getInstance().saveRecord(record);
    }

    // ── 파서 헬퍼 ──────────────────────────────────────────────

    private float[] parseNumbers(String raw) throws Exception {
        String[] tokens = raw.split("[,\\s]+");
        float[] result = new float[tokens.length];
        for (int i = 0; i < tokens.length; i++)
            result[i] = Float.parseFloat(tokens[i].trim());
        if (result.length == 0)
            throw new Exception(getString(R.string.msg_empty_numbers));
        int maxCount = getResources().getInteger(R.integer.max_input_count);
        if (result.length > maxCount)
            throw new Exception(getString(R.string.msg_too_many_numbers, maxCount));
        return result;
    }

    private float[] parseSearchInput(String raw) throws Exception {
        String listPart = raw.contains("|") ? raw.split("\\|")[0] : raw;
        return parseNumbers(listPart.trim());
    }

    private float parseTarget(String raw) throws Exception {
        if (!raw.contains("|"))
            throw new Exception(getString(R.string.msg_search_format));
        return Float.parseFloat(raw.split("\\|")[1].trim());
    }

    static class GraphInput { List<Edge> edges; String start; }

    private GraphInput parseGraph(String raw) throws Exception {
        GraphInput g = new GraphInput();
        g.edges = new ArrayList<>();
        String edgePart = raw.contains("|") ? raw.split("\\|")[0].trim() : raw.trim();
        g.start = raw.contains("|") ? raw.split("\\|")[1].trim() : null;
        for (String token : edgePart.split(",")) {
            token = token.trim();
            if (!token.contains("-"))
                throw new Exception(getString(R.string.msg_graph_format));
            String[] parts = token.split("-", 2);
            g.edges.add(new Edge(parts[0].trim(), parts[1].trim()));
        }
        if (g.edges.isEmpty())
            throw new Exception(getString(R.string.msg_empty_edges));
        if (g.start == null || g.start.isEmpty()) g.start = g.edges.get(0).from;
        return g;
    }

    private String[] parseTreeLevel(String raw) {
        String[] tokens = raw.split("[,\\s]+");
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String t = tokens[i].trim().toLowerCase();
            result[i] = (t.equals("null") || t.equals("none") || t.equals("_")) ? null : tokens[i].trim();
        }
        return result;
    }

    @Override
    protected void onPause() { super.onPause(); stopPlayback(); }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
