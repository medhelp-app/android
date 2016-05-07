package com.medhelp.medhelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.medhelp.medhelp.exceptions.PasswordInvalidException;
import com.medhelp.medhelp.helpers.authenticationValidator;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    // TODO: Change url to production
    private static final String SIGNUP_URL = "http://192.168.1.3:4000/api/users";

    @Bind(R.id.input_name_signup) EditText _nameText;
    @Bind(R.id.input_email_signup) EditText _emailText;
    @Bind(R.id.input_password_signup) EditText _passwordText;
    @Bind(R.id.input_confirmationPassword_signup) EditText _passwordConfirmationText;
    @Bind(R.id.btn_signup_signup) Button _signupButton;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

    }

    private void signup() {
        Log.d(TAG, "Signup");

        _signupButton.setEnabled(false);

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String passwordConfirmation = _passwordConfirmationText.getText().toString();

        if (validateName(name) && validateEmail(email) && validatePassword(password) && validatePasswordConfirmation(password, passwordConfirmation))
            register(name, email, password, passwordConfirmation);
        else
            _signupButton.setEnabled(true);
    }

    private void register(final String name, final String email, final String password, final String passwordConfirmation) {
        StringRequest request = new StringRequest(Request.Method.POST, SIGNUP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Cadastro realizado com sucesso");

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Cadastro falhou");

                if (!TextUtils.isEmpty(error.getMessage()))
                    onLoginFailed(error.getMessage());
                else
                    onLoginFailed("Cadastro falhou");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("rePassword", passwordConfirmation);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);

        _signupButton.setEnabled(true);
    }

    private boolean validateName(String name) {
        if (TextUtils.isEmpty(name)) {
            _nameText.setError("Campo obrigatório");
            return false;
        }
        return true;
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

    private boolean validatePasswordConfirmation(String password, String passwordConfirmation) {
        try {
            authenticationValidator.isValidPassword(passwordConfirmation);
        } catch (PasswordInvalidException ex) {
            _passwordConfirmationText.setError(ex.getMessage());
            return false;
        }

        if (!passwordConfirmation.equals(password)) {
            _passwordConfirmationText.setError("Senhas devem ser iguais");
            return false;
        }

        return true;
    }

    private void onLoginFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }
}
