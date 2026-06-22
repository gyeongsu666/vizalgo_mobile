package com.example.algoviz;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.algoviz.databinding.ActivityRegisterBinding;
import com.example.algoviz.firebase.FirebaseManager;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseManager firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_register);
        }

        firebase = FirebaseManager.getInstance();
        binding.btnRegisterConfirm.setOnClickListener(v -> register());
    }

    private void register() {
        String id       = binding.etId.getText().toString().trim();
        String name     = binding.etName.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirm  = binding.etPasswordConfirm.getText().toString().trim();

        int minIdLen  = getResources().getInteger(R.integer.min_id_length);
        int minPwdLen = getResources().getInteger(R.integer.min_password_length);

        if (TextUtils.isEmpty(id)) {
            showToast(getString(R.string.msg_id_required)); return;
        }
        if (id.length() < minIdLen) {
            showToast(getString(R.string.msg_id_too_short, minIdLen)); return;
        }
        if (TextUtils.isEmpty(name)) {
            showToast(getString(R.string.msg_name_required)); return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast(getString(R.string.msg_password_required)); return;
        }
        if (password.length() < minPwdLen) {
            showToast(getString(R.string.msg_password_too_short, minPwdLen)); return;
        }
        if (!password.equals(confirm)) {
            showToast(getString(R.string.msg_password_mismatch)); return;
        }

        setLoading(true);
        firebase.registerWithIdNamePassword(id, name, password)
                .addOnSuccessListener(r -> {
                    showToast(getString(R.string.msg_welcome_user, name));
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    String msg = e.getMessage();
                    if (msg != null && msg.contains("already in use")) {
                        showToast(getString(R.string.msg_id_already_used));
                    } else {
                        showToast(getString(R.string.msg_register_failed, msg));
                    }
                });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnRegisterConfirm.setEnabled(!loading);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
