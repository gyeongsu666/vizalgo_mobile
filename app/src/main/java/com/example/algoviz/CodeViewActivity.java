package com.example.algoviz;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.algoviz.algorithm.AlgorithmRegistry;
import com.example.algoviz.databinding.ActivityCodeViewBinding;
import com.example.algoviz.model.Algorithm;

public class CodeViewActivity extends AppCompatActivity {

    private ActivityCodeViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCodeViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String key = getIntent().getStringExtra("algorithmKey");
        Algorithm algorithm = AlgorithmRegistry.get(key);
        if (algorithm == null) { finish(); return; }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(algorithm.getTitle());
        }

        binding.codeView.setCode(algorithm.getPythonCode());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
