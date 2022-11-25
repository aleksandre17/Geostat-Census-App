package com.geostat.census_2024.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.geostat.census_2024.PopupWritePermission;
import com.geostat.census_2024.R;
import com.geostat.census_2024.databinding.ActivityLoginBinding;
import com.geostat.census_2024.ui.fileManager.FileActivity;
import com.geostat.census_2024.ui.map.MapActivity;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Settings.System.canWrite(getApplicationContext())) {

            FragmentManager ft = getSupportFragmentManager();
            PopupWritePermission dialogFragment = PopupWritePermission.newInstance("1", "2");
            dialogFragment.show(ft, "1");
        }

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory(getApplication())).get(LoginViewModel.class);

        if (loginViewModel.getLoginRepository().isLoggedIn()) {
            redirect();
        } else {
            binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            auth();
        }
    }


    private void redirect() {

        if (!new File(getExternalFilesDir(null).getPath() + "/samgori.gpkg").exists()) {

            Intent intent = new Intent(LoginActivity.this, FileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } else {
            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            // finish();
            startActivity(intent); finish();
        }
    }

    private void auth () {

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        usernameEditText.setText(new String("1158072"));

        InterTextWatcherListener interTextWatcherListener = new InterTextWatcherListener(loginViewModel, List.of(usernameEditText, passwordEditText));

        usernameEditText.addTextChangedListener(interTextWatcherListener);
        passwordEditText.addTextChangedListener(interTextWatcherListener);

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            try {
                loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });


        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) { return; }

            loginButton.setEnabled(loginFormState.isDataValid());

            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }

            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {

            if (loginResult == null) { return; }

            loadingProgressBar.setVisibility(View.GONE);

            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }

            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());

                //setResult(Activity.RESULT_OK);
                //Complete and destroy login activity once successful

                redirect();
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static class InterTextWatcherListener implements TextWatcher {

        LoginViewModel loginViewModel;
        List<EditText> editTexts;

        public InterTextWatcherListener(LoginViewModel loginViewModel, List<EditText> editTexts) {
            this.loginViewModel = loginViewModel;
            this.editTexts = editTexts;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            loginViewModel.loginDataChanged(editTexts.get(0).getText().toString(), editTexts.get(1).getText().toString());
        }
    }
}