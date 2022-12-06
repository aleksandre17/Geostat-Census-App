package com.geostat.census_2024.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.geostat.census_2024.R;
import com.geostat.census_2024.architecture.module.EncryptModule;
import com.geostat.census_2024.data.model.UserModel;
import com.geostat.census_2024.data.repository.LoginRepository;
import com.geostat.census_2024.data.response.Result;
import com.geostat.census_2024.data.local.entities.UserEntity;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LoginRepository getLoginRepository() { return loginRepository; }

    public void login(String username, String password) throws ExecutionException, InterruptedException {

        // can be launched in a separate asynchronous job
        Result<UserModel> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            UserModel userModel = ((Result.Success<UserModel>) result).getData();

            if (((Result.Success<?>) result).getFrom().equals("server")) {

                EncryptModule encryptModule = new EncryptModule();
                UserEntity insert = new UserEntity();
                insert.setUserName(userModel.getUserName());
                insert.setToken(userModel.getToken());
                insert.setPassword(Arrays.toString(encryptModule.encryptPassword(password)));

                loginRepository.insert(insert);
            }

            LoggedInUserView loggedInUserView = new LoggedInUserView(userModel.getUserName());

            loginResult.setValue(new LoginResult(loggedInUserView));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }

    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}