package com.medhelp.medhelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhelp.medhelp.exceptions.PasswordInvalidException;
import com.medhelp.medhelp.helpers.SHA;
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.helpers.authenticationValidator;
import com.medhelp.medhelp.model.EUserType;
import com.medhelp.medhelp.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    private EditText mNameText;
    private EditText mEmailText;
    private EditText mPasswordText;
    private EditText mPasswordConfirmationText;

    private RadioGroup mUserTypeGroup;
    private RadioButton mPatientRadio;
    private RadioButton mDoctorRadio;

    private Button mSignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initFields();

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    private void initFields() {
        mNameText = (EditText) findViewById(R.id.input_name_signup);
        mEmailText = (EditText) findViewById(R.id.input_email_signup);
        mPasswordText = (EditText) findViewById(R.id.input_password_signup);
        mPasswordConfirmationText = (EditText) findViewById(R.id.input_confirmationPassword_signup);
        mUserTypeGroup = (RadioGroup) findViewById(R.id.grup_userType_signup);

        mPatientRadio = (RadioButton) findViewById(R.id.radio_patient_signup);
        mDoctorRadio = (RadioButton) findViewById(R.id.radio_doctor_signup);

        mSignupButton = (Button) findViewById(R.id.btn_signup_signup);
    }

    private void signup() {
        Log.d(TAG, "Signup");

        mSignupButton.setEnabled(false);

        String name = mNameText.getText().toString();
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();
        String passwordConfirmation = mPasswordConfirmationText.getText().toString();


        EUserType userType = EUserType.Patient;
        if (mUserTypeGroup.getCheckedRadioButtonId() == mDoctorRadio.getId()) {
            userType = EUserType.Doctor;
        } else if (mUserTypeGroup.getCheckedRadioButtonId() == mPatientRadio.getId()) {
            userType = EUserType.Patient;
        }

        if (validateName(name) && validateEmail(email) && validatePassword(password) && validatePasswordConfirmation(password, passwordConfirmation)) {
            register(name, email, password, passwordConfirmation, userType);
        }
        else {
            mSignupButton.setEnabled(true);
        }
    }

    private void register(final String name, final String email, final String password, final String passwordConfirmation, final EUserType userType) {
        StringRequest request = new StringRequest(Request.Method.POST, URLHelper.SIGNUP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Cadastro realizado com sucesso");

                User user = parseResponseJSON(response);

                if (user != null) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Cadastro falhou");

                handleError(error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", SHA.Hash(password));
                params.put("rePassword", SHA.Hash(passwordConfirmation));
                params.put("userType", String.valueOf(userType.getValue()));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);

        mSignupButton.setEnabled(true);
    }

    private void handleError(VolleyError error) {
        if (error instanceof TimeoutError) {
            onSignupFailed("Erro de tempo de resposta");
        } else if (error instanceof NoConnectionError) {
            onSignupFailed("Falha na conexão com o servidor");
        } else {
            parseErrorJSON(error);
        }
    }

    private void parseErrorJSON(VolleyError error) {
        NetworkResponse networkResponse = error.networkResponse;
        String stringError = new String(networkResponse.data);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> errorMessage = null;
        try {
            errorMessage = mapper.readValue(stringError, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (errorMessage != null && !TextUtils.isEmpty(errorMessage.get("error"))) {
            onSignupFailed(errorMessage.get("error"));
        }
        else {
            onSignupFailed("Problema de conexão");
        }
    }

    private User parseResponseJSON(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        User user = null;
        try {
            user = objectMapper.readValue(response, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }

    private boolean validateName(String name) {
        if (TextUtils.isEmpty(name)) {
            mNameText.setError("Campo obrigatório");
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        if (!authenticationValidator.isValidEmail(email)) {
            mEmailText.setError("Entre um email válido");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        try {
            authenticationValidator.isValidPassword(password);
        } catch (PasswordInvalidException ex) {
            mPasswordText.setError(ex.getMessage());
            return false;
        }

        return true;
    }

    private boolean validatePasswordConfirmation(String password, String passwordConfirmation) {
        try {
            authenticationValidator.isValidPassword(passwordConfirmation);
        } catch (PasswordInvalidException ex) {
            mPasswordConfirmationText.setError(ex.getMessage());
            return false;
        }

        if (!passwordConfirmation.equals(password)) {
            mPasswordConfirmationText.setError("Senhas devem ser iguais");
            return false;
        }

        return true;
    }

    private void onSignupFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        mSignupButton.setEnabled(true);
    }
}
