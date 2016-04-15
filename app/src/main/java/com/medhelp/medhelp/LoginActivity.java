package com.medhelp.medhelp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.medhelp.medhelp.exceptions.PasswordInvalidException;
import com.medhelp.medhelp.helpers.authenticationValidator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.link_forgotPassword) TextView _forgotPasswordLink;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.btn_signup) Button _signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO: Start the SignUp Activity
                // Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                // ...
            }
        });

        _forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Start the Forgot Activity
                // Intent intent = new Intent(getApplicationContext(), ForgotPassoword.class);
                // ...
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        _loginButton.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (!validateEmail(email) || !validatePassword(password))
            onLoginFailed("Falha no login");
        else
            authenticate(email, password);
    }

    public void authenticate(String email, String password) {
        //TODO: Implement call to web service

        _loginButton.setEnabled(true);
    }

    private boolean validateEmail(String email) {
        if (!authenticationValidator.isValidEmail(email)) {
            _emailText.setError("Entre um email válido");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        try {
            authenticationValidator.isValidPassword(password);

        } catch (PasswordInvalidException ex) {
            _passwordText.setError(ex.getMessage());
            return false;
        }

        return true;
    }

    private void onLoginFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

}
