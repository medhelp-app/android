package com.medhelp.medhelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.helpers.authenticationValidator;
import com.medhelp.medhelp.model.EUserType;
import com.medhelp.medhelp.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name_signup) EditText _nameText;
    @Bind(R.id.input_email_signup) EditText _emailText;
    @Bind(R.id.input_password_signup) EditText _passwordText;
    @Bind(R.id.input_confirmationPassword_signup) EditText _passwordConfirmationText;
    @Bind(R.id.btn_signup_signup) Button _signupButton;
    private Spinner mUserTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        mUserTypeSpinner = (Spinner) findViewById(R.id.spinner_userType_signup);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_type, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mUserTypeSpinner.setAdapter(adapter);

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

        EUserType userType = EUserType.Patient;
        if (mUserTypeSpinner.getSelectedItemPosition() == 1)
            userType = EUserType.Doctor;

        if (validateName(name) && validateEmail(email) && validatePassword(password) && validatePasswordConfirmation(password, passwordConfirmation))
            register(name, email, password, passwordConfirmation, userType);
        else
            _signupButton.setEnabled(true);
    }

    private void register(final String name, final String email, final String password, final String passwordConfirmation, final EUserType userType) {
        StringRequest request = new StringRequest(Request.Method.POST, URLHelper.SIGNUP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Cadastro realizado com sucesso");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                User user = null;
                try {
                    user = objectMapper.readValue(response, User.class);
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
                Log.d(TAG, "Cadastro falhou");

                NetworkResponse networkResponse = error.networkResponse;
                String stringError = new String(networkResponse.data);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> errorMessage = null;
                try {
                    errorMessage = mapper.readValue(stringError, new TypeReference<Map<String,String>>() { });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (errorMessage != null && !TextUtils.isEmpty(errorMessage.get("error")))
                    onSignupFailed(errorMessage.get("error"));
                else
                    onSignupFailed("Problema de conexão");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("rePassword", passwordConfirmation);
                params.put("userType", String.valueOf(userType.getValue()));

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

    private void onSignupFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }
}
