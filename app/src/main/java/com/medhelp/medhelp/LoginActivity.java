package com.medhelp.medhelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhelp.medhelp.exceptions.PasswordInvalidException;
import com.medhelp.medhelp.helpers.authenticationValidator;
import com.medhelp.medhelp.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // TODO: Change url to production
    private static final String LOGIN_URL = "http://192.168.1.3:4000/api/users/login";

    @Bind(R.id.input_email_login) EditText _emailText;
    @Bind(R.id.input_password_login) EditText _passwordText;
    @Bind(R.id.link_forgotPassword_login) TextView _forgotPasswordLink;
    @Bind(R.id.btn_login_login) Button _loginButton;
    @Bind(R.id.btn_signup_login) Button _signupButton;

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
                 Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                 startActivity(intent);
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

        if (validateEmail(email) && validatePassword(password))
            authenticate(email, password);

        else
            _loginButton.setEnabled(true);
    }

    public void authenticate(final String email, final String password) {
        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login realizado com sucesso");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                User user = null;
                try {
                    user = objectMapper.readValue(response.toString(), User.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Login falhou");

                NetworkResponse networkResponse = error.networkResponse;
                String stringError = new String(networkResponse.data);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> errorMessage = null;
                try {
                     errorMessage = mapper.readValue(stringError, new TypeReference<Map<String,String>>() { });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(errorMessage.get("error")))
                    onLoginFailed(errorMessage.get("error"));
                else
                    onLoginFailed("Problema de conexão");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);

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
