package com.example.algoviz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.algoviz.databinding.ActivityLoginBinding;
import com.example.algoviz.firebase.FirebaseManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseManager firebase;
    private GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    showToast(getString(R.string.msg_google_login_failed, e.getMessage()));
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebase = FirebaseManager.getInstance();

        if (firebase.isLoggedIn()) {
            goToList();
            return;
        }

        // Google Sign-In 설정
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnLogin.setOnClickListener(v -> loginWithId());
        binding.btnRegister.setOnClickListener(v -> goToRegister());
        binding.btnGoogleLogin.setOnClickListener(v -> loginWithGoogle());
        binding.tvSkip.setOnClickListener(v -> goToList());
    }

    private void loginWithId() {
        String id = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(password)) {
            showToast(getString(R.string.msg_id_password_required));
            return;
        }
        setLoading(true);
        firebase.loginWithId(id, password)
                .addOnSuccessListener(r -> goToList())
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showToast(getString(R.string.msg_login_failed));
                });
    }

    private void loginWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        setLoading(true);
        firebase.loginWithGoogle(idToken)
                .addOnSuccessListener(r -> goToList())
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showToast(getString(R.string.msg_google_auth_failed, e.getMessage()));
                });
    }

    private void goToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void goToList() {
        startActivity(new Intent(this, AlgorithmListActivity.class));
        finish();
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!loading);
        binding.btnGoogleLogin.setEnabled(!loading);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
